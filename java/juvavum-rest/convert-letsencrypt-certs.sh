#!/bin/bash

# Script to convert Let's Encrypt certificates to PKCS12 format for Spring Boot
# Usage: ./convert-letsencrypt-certs.sh <domain> <keystore-password>

set -e

# Check arguments
if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <domain> [keystore-password] [alias]"
    echo "Example: $0 example.com mySecretPassword juvavum"
    exit 1
fi

DOMAIN=$1
KEYSTORE_PASSWORD=${2:-changeit}
KEY_ALIAS=${3:-juvavum}

# Let's Encrypt default certificate locations
LETSENCRYPT_DIR="/etc/letsencrypt/live/$DOMAIN"
CERT_FILE="$LETSENCRYPT_DIR/cert.pem"
CHAIN_FILE="$LETSENCRYPT_DIR/chain.pem"
FULLCHAIN_FILE="$LETSENCRYPT_DIR/fullchain.pem"
PRIVKEY_FILE="$LETSENCRYPT_DIR/privkey.pem"

# Output directory
OUTPUT_DIR="./certs"
KEYSTORE_FILE="$OUTPUT_DIR/keystore.p12"

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Check if Let's Encrypt certificates exist
if [ ! -f "$FULLCHAIN_FILE" ]; then
    echo "Error: Let's Encrypt certificates not found at $LETSENCRYPT_DIR"
    echo "Please ensure Let's Encrypt certificates are installed for domain: $DOMAIN"
    exit 1
fi

if [ ! -f "$PRIVKEY_FILE" ]; then
    echo "Error: Private key not found at $PRIVKEY_FILE"
    exit 1
fi

echo "Converting Let's Encrypt certificates for domain: $DOMAIN"
echo "Certificate location: $LETSENCRYPT_DIR"
echo "Output keystore: $KEYSTORE_FILE"

# Convert to PKCS12 format
openssl pkcs12 -export \
    -in "$FULLCHAIN_FILE" \
    -inkey "$PRIVKEY_FILE" \
    -out "$KEYSTORE_FILE" \
    -name "$KEY_ALIAS" \
    -passout "pass:$KEYSTORE_PASSWORD"

# Set appropriate permissions
chmod 600 "$KEYSTORE_FILE"

echo ""
echo "✓ Certificate conversion successful!"
echo ""
echo "Keystore details:"
echo "  File: $KEYSTORE_FILE"
echo "  Alias: $KEY_ALIAS"
echo "  Type: PKCS12"
echo ""
echo "To use with Docker, update your .env file with:"
echo "  KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD"
echo "  KEY_ALIAS=$KEY_ALIAS"
echo ""
echo "IMPORTANT: Keep your keystore password secure!"
