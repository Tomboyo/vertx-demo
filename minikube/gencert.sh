#!/bin/bash
#
# Generate a P12 file with a self-signed certificate.
# All passwords are "password."
# Suiteable for testing only. Obviously.

# Generate a self-signed certificate
openssl req -x509 -newkey rsa -keyout key.pem -out cert.pem -sha256 -config req.options -passin pass:password

# Create a pkcs12 file from server certificate and private key
openssl pkcs12 -export -out server.p12 -inkey key.pem -in cert.pem -passin pass:password -passout pass:password

# Clean up
rm cert.pem key.pem