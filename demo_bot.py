from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains
import time
import os
import random
from faker import Faker

# ========== CẤU HÌNH ==========
DRIVER_PATH = "C:/Nam3HK2/BaoMatWeb/Project/chrome_driver/chromedriver-win64/chromedriver.exe"
SAMPLE_IMAGE = os.path.abspath("sample.png")
TARGET_URL = "https://localhost:8443/register"
NUMBER_OF_BOTS = 10
WAIT_TIMEOUT = 10  # Tăng thời gian chờ

# ========== KHỞI TẠO FAKER ==========
fake = Faker('vi_VN')

def generate_fake_data():
    return {
        "userName": f"bot_{fake.user_name()}_{random.randint(1000,9999)}",
        "fullName": fake.name(),
        "email": fake.unique.free_email(),
        "password": "P@ssw0rd123!",
        "phone": f"0{random.randint(30,99)}{random.randint(1000000,9999999)}",
        "address": fake.address()[:50]
    }

# ========== KIỂM TRA FILE ẢNH ==========
if not os.path.exists(SAMPLE_IMAGE):
    raise FileNotFoundError(f"Không tìm thấy ảnh tại: {SAMPLE_IMAGE}")

# ========== CẤU HÌNH TRÌNH DUYỆT ==========
service = Service(executable_path=DRIVER_PATH)
options = webdriver.ChromeOptions()
options.add_argument('--ignore-certificate-errors')
options.add_argument('--disable-notifications')
options.add_argument('--disable-popup-blocking')
options.add_argument('--start-maximized')
options.add_argument('--force-device-scale-factor=0.8')  # Giảm tỉ lệ hiển thị

driver = webdriver.Chrome(service=service, options=options)
wait = WebDriverWait(driver, WAIT_TIMEOUT)
actions = ActionChains(driver)

def scroll_and_click(element):
    """Hàm helper để cuộn và click an toàn"""
    try:
        # Thử click trực tiếp
        element.click()
    except:
        # Nếu fail thì dùng JavaScript
        driver.execute_script("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'});", element)
        time.sleep(0.5)
        driver.execute_script("arguments[0].click();", element)

try:
    for attempt in range(1, NUMBER_OF_BOTS + 1):
        print(f"\n=== Attempt {attempt}/{NUMBER_OF_BOTS} ===")
        
        driver.get(TARGET_URL)
        
        # Chờ form load
        form = wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, "form[method='POST']")))
        
        # Đóng popup nếu có
        try:
            close_buttons = driver.find_elements(By.CSS_SELECTOR, ".modal-close, .btn-close")
            for btn in close_buttons:
                btn.click()
                time.sleep(0.3)
        except:
            pass

        fake_data = generate_fake_data()
        print(f"Trying: {fake_data['userName']}")

        # ========== ĐIỀN FORM ==========
        fields = {
            "userName": fake_data["userName"],
            "fullName": fake_data["fullName"],
            "email": fake_data["email"],
            "password": fake_data["password"],
            "confirmPassword": fake_data["password"],
            "phone": fake_data["phone"],
            "address": fake_data["address"]
        }

        for field_id, value in fields.items():
            element = wait.until(EC.element_to_be_clickable((By.ID, field_id)))
            element.clear()
            element.send_keys(value)
            time.sleep(0.1)  # Giảm tốc độ gõ

        # ========== UPLOAD ẢNH ==========
        driver.find_element(By.ID, "image").send_keys(SAMPLE_IMAGE)

        # ========== CHỌN ROLE ==========
        role_radio = wait.until(EC.presence_of_element_located((By.ID, "role_3")))
        scroll_and_click(role_radio)

        # ========== SUBMIT FORM ==========
        submit_button = wait.until(EC.presence_of_element_located((By.XPATH, "//button[@type='submit']")))
        
        # Click bằng 3 cách khác nhau
        try:
            submit_button.click()
        except:
            try:
                actions.move_to_element(submit_button).click().perform()
            except:
                driver.execute_script("arguments[0].click();", submit_button)

        # ========== XỬ LÝ KẾT QUẢ ==========
        try:
            alert = wait.until(EC.visibility_of_any_elements_located(
                (By.CSS_SELECTOR, ".alert, .error-message, .success-message")
            ))[-1]  # Lấy thông báo cuối cùng
            print(f"Result: {alert.text[:50]}...")
        except Exception as e:
            print(f"Không bắt được thông báo: {str(e)}")
            driver.save_screenshot(f"error_{attempt}.png")

        time.sleep(1.5)

finally:
    driver.quit()
    print("\nHoàn thành quá trình thử nghiệm!")