#!/bin/bash

# Script to configure UFW firewall to block HTTP (port 80) and allow only HTTPS (port 443)
# Run with: sudo ./setup-firewall.sh

set -e

echo "Configuring UFW firewall for Juvavum REST API"
echo "=============================================="
echo ""

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    echo "Please run as root (use sudo)"
    exit 1
fi

# Install UFW if not already installed
if ! command -v ufw &> /dev/null; then
    echo "Installing UFW..."
    apt update
    apt install -y ufw
fi

# Reset UFW to default
echo "Resetting UFW to default configuration..."
ufw --force reset

# Set default policies
echo "Setting default policies..."
ufw default deny incoming
ufw default allow outgoing

# Allow SSH (important to not lock yourself out!)
echo "Allowing SSH (port 22)..."
ufw allow 22/tcp comment 'SSH'

# Allow HTTPS only
echo "Allowing HTTPS (port 443)..."
ufw allow 443/tcp comment 'HTTPS - Juvavum REST API'

# Explicitly deny HTTP (port 80)
echo "Denying HTTP (port 80)..."
ufw deny 80/tcp comment 'HTTP - Blocked'

# Enable UFW
echo "Enabling UFW..."
ufw --force enable

# Show status
echo ""
echo "Firewall configuration complete!"
echo ""
echo "Current firewall rules:"
ufw status verbose

echo ""
echo "✓ SSH (port 22): ALLOWED"
echo "✓ HTTPS (port 443): ALLOWED"
echo "✗ HTTP (port 80): DENIED"
echo ""
echo "Your server is now configured to serve HTTPS only."
