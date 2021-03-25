# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM ${repository}/crayfish:${tag}

ENV \
    MILLINER_DRUPAL_URL=http://drupal:80 \
    MILLINER_FCREPO_URL=http://fcrepo:8080/fcrepo/rest \
    MILLINER_FEDORA6=false \
    MILLINER_LOG_LEVEL=debug

RUN ln -s /var/www/crayfish/Milliner/src /var/www/html

COPY rootfs /

WORKDIR /var/www/crayfish/Milliner/
