# syntax=docker/dockerfile:1.2.1
ARG repository=local
ARG tag=latest
FROM --platform=$BUILDPLATFORM ${repository}/download:${tag} AS download

RUN --mount=type=cache,id=fits-downloads,sharing=locked,target=/opt/downloads \
    FITSSERVLET_VERSION=1.2.1 && \
    FITSSERVLET_FILE="fits-${FITSSERVLET_VERSION}.war" && \
    FITSSERVLET_URL="http://projects.iq.harvard.edu/files/fits/files/${FITSSERVLET_FILE}" && \
    FITSSERVLET_SHA256="13cfcb910092b197757e459353f0c30381febfca6baf3031ac69ff92789b200c" && \
    download.sh --url "${FITSSERVLET_URL}" --sha256 "${FITSSERVLET_SHA256}" "${DOWNLOAD_CACHE_DIRECTORY}" && \
    install-war-into-tomcat.sh --name "fits" --file "${DOWNLOAD_CACHE_DIRECTORY}/${FITSSERVLET_FILE}" && \
    FITS_VERSION="1.5.0" && \
    FITS_FILE="fits-${FITS_VERSION}.zip" && \
    FITS_URL="https://github.com/harvard-lts/fits/releases/download/${FITS_VERSION}/${FITS_FILE}" \
    FITS_SHA256="1378a78892db103b3a00e45c510b58c70e19a1a401b3720ff4d64a51438bfe0b" && \
    mkdir /opt/fits && \
    download.sh --url "${FITS_URL}" --sha256 "${FITS_SHA256}" "${DOWNLOAD_CACHE_DIRECTORY}" && \
    unzip "${DOWNLOAD_CACHE_DIRECTORY}/${FITS_FILE}" -d /opt/fits && \
    rm -fr \
        /opt/fits/lib/jna-* \
        /opt/fits/tools/exiftool/perl/html \
        /opt/fits/tools/exiftool/windows \
        /opt/fits/tools/file_utility_windows \
        /opt/fits/tools/mediainfo

FROM alpine:3.13.2 AS cache
FROM ${repository}/tomcat:${tag}

COPY --from=download --chown=tomcat:tomcat /opt/tomcat /opt/tomcat
COPY --from=download --chown=tomcat:tomcat /opt/fits /opt/fits

# Replace linux shared libraries with ones that target muslibc and are platform specific.
# Also add perl for exiftool, and platform specific jna so native libs can be loaded.
RUN --mount=type=cache,id=fits-apk,sharing=locked,from=cache,target=/var/cache/apk \
    apk add \
        file \
        java-jna \
        libmediainfo \
        libzen \
        perl \
    && \
    cp $(realpath /usr/share/java/jna.jar) /opt/fits/lib && \
    cleanup.sh

ENV \
    FITS_MAX_IN_MEMORY_FILE_SIZE=4 \
    FITS_MAX_OBJECTS_IN_POOL=5 \
    FITS_MAX_REQUEST_SIZE=2000 \
    FITS_MAX_UPLOAD_FILE_SIZE=2000

COPY rootfs /
COPY --chown=tomcat:tomcat rootfs/opt/tomcat /opt/tomcat
COPY --chown=tomcat:tomcat rootfs/opt/fits /opt/fits