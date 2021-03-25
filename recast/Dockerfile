# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM ${repository}/crayfish:${tag}

RUN ln -s /var/www/crayfish/Recast/src /var/www/html

ENV \
    RECAST_DRUPAL_URL=islandora.traefik.me \
    RECAST_FCREPO_URL=islandora.traefik.me:8081/fcrepo/rest \
    RECAST_LOG_LEVEL=debug

COPY rootfs /

WORKDIR /var/www/crayfish/Recast/
