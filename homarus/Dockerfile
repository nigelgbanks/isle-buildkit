# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM alpine:3.13.2 AS cache
FROM ${repository}/crayfish:${tag}

RUN --mount=type=cache,id=homarus-apk,sharing=locked,from=cache,target=/var/cache/apk \
    apk add ffmpeg && \
    addgroup nginx jwt && \
    ln -s /var/www/crayfish/Homarus/src /var/www/html && \
    cleanup.sh

ENV HOMARUS_LOG_LEVEL=debug

COPY rootfs /

WORKDIR /var/www/crayfish/Homarus/
