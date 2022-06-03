#!/bin/bash
#
# Generate a P12 file with a self-signed certificate.
# All passwords are "password."
# Suiteable for testing only. Obviously.

KEY=$OUT/key.pem
CERT=$OUT/cert.pem
PKCS=$OUT/server.p12

if [ ! -d "$OUT" ] ; then
    mkdir -p "$OUT"
fi

# Generate a self-signed certificate
# Annotate the certificate as a CA so that we can easily trust it in curl using --cacert
openssl req -x509 \
    -newkey rsa \
    -keyout "$KEY" \
    -out "$CERT" \
    -sha256 \
    -config $OPTSPATH \
    -passin pass:password \
    -addext basicConstraints=critical,CA:TRUE,pathlen:1

# Create a pkcs12 file from server certificate and private key
openssl pkcs12 -export \
    -out "$PKCS" \
    -inkey "$KEY" \
    -in "$CERT" \
    -passin pass:password \
    -passout pass:password