# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM ${repository}/imagemagick:${tag} AS imagemagick
FROM ${repository}/crayfish:${tag}

RUN --mount=type=bind,from=imagemagick,source=/packages,target=/packages \
    --mount=type=bind,from=imagemagick,source=/etc/apk/keys,target=/etc/apk/keys \
    apk add /packages/imagemagick-*.apk && \
    addgroup nginx jwt && \
    php /var/www/crayfish/Houdini/bin/console cache:clear && \
    ln -s /var/www/crayfish/Houdini/public /var/www/html && \
    cleanup.sh

ENV HOUDINI_LOG_LEVEL=debug

COPY rootfs /

WORKDIR /var/www/crayfish/Houdini/
