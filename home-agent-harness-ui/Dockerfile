FROM node:22-alpine AS build

WORKDIR /workspace
COPY home-agent-harness-ui/package*.json ./
RUN npm install
COPY home-agent-harness-ui/ .
RUN npm run build -- --configuration=production

FROM nginx:1.27-alpine

COPY home-agent-harness-ui/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/home-agent-harness-ui/browser /usr/share/nginx/html

EXPOSE 4200

