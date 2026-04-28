FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV UNI_APP_API_BASE_URL=${VITE_API_BASE_URL}
ENV UNI_INPUT_DIR=/app
RUN npm run build:h5

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/unpackage/dist/build/h5 /usr/share/nginx/html
EXPOSE 80
