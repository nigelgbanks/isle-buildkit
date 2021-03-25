# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM --platform=$BUILDPLATFORM ${repository}/download:${tag} AS download

RUN --mount=type=cache,id=crayfish-downloads,sharing=locked,target=/opt/downloads \
    COMMIT=ae9a35c88d2bdfd0343122a8b5b373d6368cc623 && \
    git-clone-cached.sh \
        --url https://github.com/Islandora/Crayfish.git \
        --cache-dir "${DOWNLOAD_CACHE_DIRECTORY}" \
        --commit "${COMMIT}" \
        --worktree /var/www/crayfish

FROM --platform=$BUILDPLATFORM ${repository}/nginx:${tag} AS composer

COPY --from=download --chown=nginx:nginx /var/www /var/www

RUN --mount=type=cache,id=crayfish-composer,sharing=locked,target=/root/.composer/cache \
    find /var/www/crayfish \
        -mindepth 2 \
        -name "composer.json" \
        -exec dirname {} \; | xargs -n1 composer install -d

FROM ${repository}/nginx:${tag}

EXPOSE 8000

COPY --from=composer --chown=nginx:nginx /var/www /var/www

COPY rootfs /
