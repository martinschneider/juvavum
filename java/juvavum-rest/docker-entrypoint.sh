#!/bin/sh
set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${GREEN}Starting Juvavum REST API...${NC}"

# Fix permissions on logs directory (run as root before dropping privileges)
if [ -d "/app/logs" ]; then
    echo "${YELLOW}Ensuring write permissions for logs directory...${NC}"
    # Ensure the directory is writable by the spring user
    chown -R spring:spring /app/logs
    chmod -R 755 /app/logs
fi

# Check if keystore exists, if not create a self-signed certificate
KEYSTORE_PATH="/app/certs/keystore.p12"
KEYSTORE_PASSWORD="${KEYSTORE_PASSWORD:-changeit}"
KEY_ALIAS="${KEY_ALIAS:-juvavum}"

if [ ! -f "$KEYSTORE_PATH" ]; then
    echo "${YELLOW}Keystore not found at $KEYSTORE_PATH${NC}"
    echo "${GREEN}Generating self-signed certificate...${NC}"

    # Create certs directory if it doesn't exist
    mkdir -p /app/certs

    # Generate self-signed certificate
    keytool -genkeypair \
        -alias "$KEY_ALIAS" \
        -keyalg RSA \
        -keysize 2048 \
        -storetype PKCS12 \
        -keystore "$KEYSTORE_PATH" \
        -storepass "$KEYSTORE_PASSWORD" \
        -validity 365 \
        -dname "CN=localhost, OU=Development, O=Juvavum, L=Unknown, ST=Unknown, C=US" \
        -ext "SAN=dns:localhost,ip:127.0.0.1"

    # Ensure the spring user can read the keystore
    chown spring:spring "$KEYSTORE_PATH"
    chmod 644 "$KEYSTORE_PATH"

    echo "${GREEN}Self-signed certificate generated successfully${NC}"
    echo "${YELLOW}Note: This is a self-signed certificate for development only${NC}"
else
    echo "${GREEN}Using existing keystore at $KEYSTORE_PATH${NC}"
fi

# Ensure certs directory is owned by spring user
chown -R spring:spring /app/certs

# Execute the main application as spring user
echo "${GREEN}Starting Spring Boot application as 'spring' user...${NC}"
exec su-exec spring java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar app.jar
