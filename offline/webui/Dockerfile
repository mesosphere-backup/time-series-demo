FROM alpine:3.2
MAINTAINER Michael Hausenblas "michael.hausenblas@mesosphere.io"
ENV REFRESHED_AT 2015-10-28
ENV NGINX_VERSION nginx-1.9.5

RUN apk --update add openssl-dev pcre-dev zlib-dev wget curl build-base && \
    mkdir -p /tmp/src && \
    cd /tmp/src && \
    wget http://nginx.org/download/${NGINX_VERSION}.tar.gz && \
    tar -zxvf ${NGINX_VERSION}.tar.gz && \
    cd /tmp/src/${NGINX_VERSION} && \
    ./configure \
        --with-http_ssl_module \
        --with-http_gzip_static_module \
        --prefix=/etc/nginx \
        --http-log-path=/var/log/nginx/access.log \
        --error-log-path=/var/log/nginx/error.log \
        --sbin-path=/usr/local/sbin/nginx && \
    make && \
    make install && \
    apk del build-base && \
    rm -rf /tmp/src && \
    rm -rf /var/cache/apk/*

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log

COPY front-end /etc/nginx/html

VOLUME ["/var/log/nginx"]

WORKDIR /etc/nginx

EXPOSE 80

USER root

CMD chmod 755 /etc/nginx/html/data ; nginx -g "daemon off;"