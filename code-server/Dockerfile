# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM alpine:3.13.2 AS cache
FROM ${repository}/abuild:${tag} AS build

# g++, make, python are only required to build native dependencies via node-gyp.
# spdlog is required for building / using xdebug extension.
RUN --mount=type=cache,id=code-server-apk,sharing=locked,from=cache,target=/var/cache/apk \
    apk add \
        bash \
        g++ \
        make \
        nghttp2-dev \
        python3 \
        spdlog \
        sudo \
        yarn

RUN --mount=type=cache,id=code-server-yarn,sharing=locked,from=cache,target=/usr/local/share/.cache/yarn \
    yarn global add node-gyp bower grunt-cli && \
    JOBS=max yarn global add code-server spdlog

SHELL ["/bin/bash", "-c"]

# Install Editor plugins from bind mounted folder (Not available on online marketplace).
RUN --mount=type=bind,source=/extensions,target=/extensions \
    for extension in /extensions/*.vsix; \
    do \
        code-server \
            --config /opt/code-server/config.yaml \
            --user-data-dir /opt/code-server/data \
            --extensions-dir /opt/code-server/extensions \
            --install-extension="${extension}" & \
    done; \
    wait

# Install Editor plugins from internet (saves downloading when installing).
RUN --mount=type=cache,id=code-server-extensions,sharing=locked,from=cache,target=/opt/code-server/.local/share/code-server/CachedExtensionVSIXs \
    EXTENSIONS=(\
        bmewburn.vscode-intelephense-client \
        felixfbecker.php-debug \
        streetsidesoftware.code-spell-checker \
        whatwedo.twig \
    ) && \
    for extension in "${EXTENSIONS[@]}"; \
    do \
        code-server \
            --config /opt/code-server/config.yaml \
            --user-data-dir /opt/code-server/data \
            --extensions-dir /opt/code-server/extensions \
            --install-extension="${extension}" & \
    done; \
    wait

FROM --platform=$BUILDPLATFORM ${repository}/composer:${tag} AS composer

RUN --mount=type=cache,id=code-server-composer,sharing=locked,target=/root/.composer/cache \
    composer global require "dealerdirect/phpcodesniffer-composer-installer" && \
    composer global require "drupal/coder" && \
    composer global require "sebastian/phpcpd" && \
    composer global require "squizlabs/php_codesniffer"

FROM ${repository}/drupal:${tag}

EXPOSE 8443 \
       9003

# Set a default shell so it can be used via code-server.
RUN sed -i "/nginx:x:100:101:nginx:\/var\/lib\/nginx:\/sbin\/nologin/cnginx:x:100:101:nginx:/var/lib/nginx:/bin/bash" /etc/passwd

# Include commonly used tools and xdebug.
RUN --mount=type=cache,id=code-server-drupal-apk,sharing=locked,from=cache,target=/var/cache/apk \
    apk --update add \
        htop \
        php7-pecl-xdebug \
        spdlog \
        yarn \
        sudo \
        unison \
        parallel \
    && \
    cleanup.sh

# Drush requires HOME to be set as such.
ENV \
    HOME="/var/lib/nginx" \
    PATH=$PATH:/var/lib/nginx/.composer/vendor/bin:/var/www/drupal/vendor/bin

# Code server / xdebug settings.
ENV \
    CODE_SERVER_AUTHENTICATION=password \
    CODE_SERVER_PASSWORD=password \
    XDEBUG_FLAGS="-d xdebug.mode=develop,debug"

COPY --from=composer --chown=nginx:nginx /root/.composer/composer.json /var/lib/nginx/.composer/composer.json
COPY --from=composer --chown=nginx:nginx /root/.composer/composer.lock /var/lib/nginx/.composer/composer.lock
COPY --from=composer --chown=nginx:nginx /root/.composer/vendor /var/lib/nginx/.composer/vendor
COPY --from=build --chown=nginx:nginx /opt/code-server /opt/code-server
COPY --from=build /usr/local/share/.config/yarn /usr/local/share/.config/yarn

COPY rootfs /

RUN ln -s /usr/local/share/.config/yarn/global/node_modules/.bin/code-server /usr/local/bin/code-server && \
    ln -s /usr/local/share/.config/yarn/global/node_modules/.bin/node-gyp /usr/local/bin/node-gyp && \
    ln -s /usr/local/share/.config/yarn/global/node_modules/.bin/grunt /usr/local/bin/grunt && \
    ln -s /usr/local/share/.config/yarn/global/node_modules/.bin/bower /usr/local/bin/bower

# https://github.com/sudo-project/sudo/issues/42
RUN chmod a=r,u+w /etc/sudo.conf
