# Stage 1: Build môi trường và compile code
FROM node:18 AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Chạy lệnh build (nếu có)
# RUN npm run build

# Stage 2: Môi trường chạy siêu nhẹ
FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app . 
CMD ["node", "app.js"]