import requests
import threading
import time
from datetime import datetime
import concurrent.futures
import logging
import urllib3
import json
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
import socket
import ssl

# Tắt cảnh báo SSL
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# Cấu hình logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('ddos_test.log'),
        logging.StreamHandler()
    ]
)

# Cấu hình test
BASE_URL = "http://localhost:8080"  # Đổi sang HTTP và port 8080
CONCURRENT_THREADS = 10  # Số lượng request đồng thời
MAX_RETRIES = 3  # Số lần thử lại tối đa
RETRY_BACKOFF = 1  # Thời gian chờ giữa các lần thử lại (giây)

# Cấu hình session với retry mechanism
def create_session():
    session = requests.Session()
    retry_strategy = Retry(
        total=MAX_RETRIES,
        backoff_factor=RETRY_BACKOFF,
        status_forcelist=[500, 502, 503, 504]
    )
    adapter = HTTPAdapter(max_retries=retry_strategy)
    session.mount("http://", adapter)
    session.mount("https://", adapter)
    return session

# Danh sách các endpoint cần test
ENDPOINTS = [
    # Authentication endpoints
    {"path": "/login", "method": "POST", "data": {"username": "test", "password": "test"}},
    {"path": "/register", "method": "POST", "data": {"username": "test", "password": "test", "email": "test@test.com"}},
    {"path": "/forgot-password", "method": "POST", "data": {"email": "test@test.com"}},
    {"path": "/reset-password", "method": "POST", "data": {"token": "test", "password": "newpass"}},
    
    # Public endpoints
    {"path": "/", "method": "GET"},
    {"path": "/home", "method": "GET"},
    {"path": "/about", "method": "GET"},
    {"path": "/contact", "method": "GET"},
    
    # Product endpoints
    {"path": "/products", "method": "GET"},
    {"path": "/products/{id}", "method": "GET", "params": {"id": 1}},
    {"path": "/products/category/{categoryId}", "method": "GET", "params": {"categoryId": 1}},
    
    # Cart endpoints
    {"path": "/cart", "method": "GET"},
    {"path": "/cart/add", "method": "POST", "data": {"productId": 1, "quantity": 1}},
    {"path": "/cart/update", "method": "POST", "data": {"productId": 1, "quantity": 2}},
    {"path": "/cart/remove", "method": "POST", "data": {"productId": 1}},
    
    # Order endpoints
    {"path": "/orders", "method": "GET"},
    {"path": "/orders/create", "method": "POST", "data": {"items": [{"productId": 1, "quantity": 1}]}},
    {"path": "/orders/{id}", "method": "GET", "params": {"id": 1}},
    
    # Admin endpoints
    {"path": "/admin/dashboard", "method": "GET"},
    {"path": "/admin/users", "method": "GET"},
    {"path": "/admin/products", "method": "GET"},
    {"path": "/admin/orders", "method": "GET"},
    
    # API endpoints
    {"path": "/api/products", "method": "GET"},
    {"path": "/api/categories", "method": "GET"},
    {"path": "/api/orders", "method": "GET"},
]

def check_server_availability():
    """Kiểm tra server có sẵn sàng không"""
    try:
        response = requests.get(f"{BASE_URL}/", timeout=5)
        return response.status_code < 500
    except requests.exceptions.RequestException as e:
        logging.error(f"Server không khả dụng: {str(e)}")
        return False

def make_request(endpoint, request_id):
    """Gửi một request và log kết quả"""
    session = create_session()
    try:
        url = f"{BASE_URL}{endpoint['path']}"
        
        # Thay thế các tham số trong URL
        if 'params' in endpoint:
            for key, value in endpoint['params'].items():
                url = url.replace(f"{{{key}}}", str(value))
        
        start_time = time.time()
        
        # Gửi request dựa trên method
        if endpoint['method'] == 'GET':
            response = session.get(url, timeout=10)
        elif endpoint['method'] == 'POST':
            response = session.post(url, json=endpoint.get('data', {}), timeout=10)
        
        end_time = time.time()
        
        log_message = f"Request {request_id} to {endpoint['path']}: Status={response.status_code}, Time={end_time - start_time:.2f}s"
        
        if response.status_code == 429:
            retry_after = response.headers.get('X-Rate-Limit-Retry-After-Seconds', 'N/A')
            log_message += f", Retry-After={retry_after}s"
            logging.warning(log_message)
        else:
            remaining = response.headers.get('X-Rate-Limit-Remaining', 'N/A')
            log_message += f", Remaining={remaining}"
            logging.info(log_message)
            
        return response.status_code
    except requests.exceptions.ConnectionError as e:
        logging.error(f"Request {request_id} to {endpoint['path']} failed: Connection error - {str(e)}")
        return None
    except requests.exceptions.Timeout as e:
        logging.error(f"Request {request_id} to {endpoint['path']} failed: Timeout - {str(e)}")
        return None
    except Exception as e:
        logging.error(f"Request {request_id} to {endpoint['path']} failed: {str(e)}")
        return None
    finally:
        session.close()

def test_endpoint(endpoint, num_requests):
    """Test một endpoint cụ thể"""
    logging.info(f"\nTesting endpoint: {endpoint['path']}")
    logging.info(f"Method: {endpoint['method']}")
    logging.info(f"Total requests: {num_requests}")
    
    success_count = 0
    rate_limited_count = 0
    error_count = 0
    
    with concurrent.futures.ThreadPoolExecutor(max_workers=CONCURRENT_THREADS) as executor:
        futures = [executor.submit(make_request, endpoint, i) for i in range(num_requests)]
        
        for future in concurrent.futures.as_completed(futures):
            result = future.result()
            if result == 200:
                success_count += 1
            elif result == 429:
                rate_limited_count += 1
            else:
                error_count += 1
    
    # In kết quả cho endpoint này
    logging.info(f"\nResults for {endpoint['path']}:")
    logging.info(f"Successful Requests: {success_count}")
    logging.info(f"Rate Limited Requests: {rate_limited_count}")
    logging.info(f"Failed Requests: {error_count}")
    if num_requests > 0:
        logging.info(f"Rate Limiting Effectiveness: {(rate_limited_count/num_requests)*100:.2f}%")
    
    return {
        'endpoint': endpoint['path'],
        'success': success_count,
        'rate_limited': rate_limited_count,
        'failed': error_count
    }

def run_test():
    """Chạy test cho tất cả các endpoint"""
    logging.info(f"Starting DDoS test at {datetime.now()}")
    logging.info(f"Configuration: {CONCURRENT_THREADS} concurrent threads")
    
    # Kiểm tra server trước khi test
    if not check_server_availability():
        logging.error("Server không khả dụng. Vui lòng kiểm tra lại server và thử lại.")
        return
    
    results = []
    for endpoint in ENDPOINTS:
        # Số request cho mỗi endpoint
        num_requests = 50 if endpoint['path'].startswith('/api') else 30
        result = test_endpoint(endpoint, num_requests)
        results.append(result)
        time.sleep(1)  # Đợi 1 giây giữa các endpoint
    
    # In kết quả tổng hợp
    logging.info("\nOverall Test Results Summary:")
    total_requests = sum(r['success'] + r['rate_limited'] + r['failed'] for r in results)
    total_rate_limited = sum(r['rate_limited'] for r in results)
    
    logging.info(f"Total Endpoints Tested: {len(ENDPOINTS)}")
    logging.info(f"Total Requests: {total_requests}")
    logging.info(f"Total Rate Limited Requests: {total_rate_limited}")
    if total_requests > 0:
        logging.info(f"Overall Rate Limiting Effectiveness: {(total_rate_limited/total_requests)*100:.2f}%")
    
    # In chi tiết cho từng endpoint
    logging.info("\nDetailed Results by Endpoint:")
    for result in results:
        logging.info(f"\n{result['endpoint']}:")
        logging.info(f"  Success: {result['success']}")
        logging.info(f"  Rate Limited: {result['rate_limited']}")
        logging.info(f"  Failed: {result['failed']}")

if __name__ == "__main__":
    run_test() 