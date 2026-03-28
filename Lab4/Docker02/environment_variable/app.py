import os

env = os.environ.get('APP_ENV', 'Không tìm thấy biến môi trường')
print(f"Ứng dụng đang chạy trong môi trường: {env}")