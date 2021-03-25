# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM --platform=$BUILDPLATFORM ${repository}/download:${tag} AS download

RUN --mount=type=cache,id=crayfits-downloads,sharing=locked,target=/opt/downloads \
    COMMIT=6e95f2f325c910b254a2b7bd1cedf25b17874d30 && \
    git-clone-cached.sh \
        --url https://github.com/roblib/CrayFits.git \
        --cache-dir "${DOWNLOAD_CACHE_DIRECTORY}" \
        --commit "${COMMIT}" \
        --worktree /var/www/crayfits

FROM --platform=$BUILDPLATFORM ${repository}/nginx:${tag} AS composer

COPY --from=download --chown=nginx:nginx /var/www /var/www

RUN --mount=type=cache,id=crayfits-composer,sharing=locked,target=/root/.composer/cache \
    composer install -d /var/www/crayfits

FROM ${repository}/nginx:${tag}

EXPOSE 8000

# Required at the moment as the log location is hard-coded in crayfits.
RUN mkdir -p /var/log/islandora && \
    touch /var/log/islandora/fits.log && \
    chown -R nginx:nginx /var/log/islandora && \
    cleanup.sh

ENV \
    CRAYFITS_LOG_LEVEL=debug \
    CRAYFITS_WEBSERVICE_URI=fits/fits/examine

COPY --from=composer --chown=nginx:nginx /var/www /var/www

COPY rootfs /

WORKDIR /var/www/crayfits