# Docker Deployment Guide for Juvavum REST API

This guide covers deploying the Juvavum REST API on Digital Ocean using Docker with Let's Encrypt SSL certificates.

## Prerequisites

- Digital Ocean Droplet (Ubuntu 22.04+ recommended)
- Docker and Docker Compose installed
- Valid domain name pointing to your droplet
- Let's Encrypt SSL certificates installed
- Root or sudo access

## Initial Server Setup

### 1. Install Docker and Docker Compose

```bash
# Update package index
sudo apt update

# Install dependencies
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Add Docker GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Add your user to docker group
sudo usermod -aG docker $USER

# Log out and back in for group changes to take effect
```

### 2. Install Let's Encrypt Certificates (if not already installed)

```bash
# Install certbot
sudo apt install -y certbot

# Obtain certificates (replace example.com with your domain)
sudo certbot certonly --standalone -d example.com -d www.example.com

# Certificates will be stored in /etc/letsencrypt/live/example.com/
```

## Deployment Steps

### 1. Clone and Prepare the Application

```bash
# Clone your repository (adjust URL as needed)
cd ~
git clone <your-repo-url> juvavum
cd juvavum/java/juvavum-rest
```

### 2. Convert Let's Encrypt Certificates

```bash
# Run the conversion script (replace example.com and password)
sudo ./convert-letsencrypt-certs.sh example.com YourSecurePassword juvavum

# The script will create a keystore.p12 file in ./certs/ directory
```

### 3. Configure Environment Variables

```bash
# Copy the example environment file
cp .env.example .env

# Edit the .env file with your settings
nano .env
```

Update the following in `.env`:
```env
KEYSTORE_PASSWORD=YourSecurePassword
KEY_ALIAS=juvavum
JAVA_OPTS=-Xms512m -Xmx1024m
```

### 4. Build and Deploy

```bash
# Build the Docker image
docker compose build

# Start the container
docker compose up -d

# View logs
docker compose logs -f
```

### 5. Verify Deployment

```bash
# Check container status
docker compose ps

# Test the endpoint (replace example.com with your domain)
curl -k https://example.com/actuator/health

# Check logs
docker compose logs -f juvavum-rest
```

## Security Configuration

### Block Port 80 (HTTP)

Since the application only serves HTTPS on port 443, ensure port 80 is blocked:

```bash
# Using UFW (Uncomplicated Firewall)
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp     # SSH
sudo ufw allow 443/tcp    # HTTPS only
sudo ufw enable

# Verify rules
sudo ufw status
```

### SSL Certificate Auto-Renewal

Set up automatic certificate renewal and conversion:

```bash
# Create renewal script
sudo nano /etc/letsencrypt/renewal-hooks/deploy/convert-for-juvavum.sh
```

Add the following content:
```bash
#!/bin/bash
cd /home/YOUR_USER/juvavum/java/juvavum-rest
./convert-letsencrypt-certs.sh example.com YourSecurePassword juvavum
docker compose restart juvavum-rest
```

Make it executable:
```bash
sudo chmod +x /etc/letsencrypt/renewal-hooks/deploy/convert-for-juvavum.sh
```

Test certificate renewal:
```bash
sudo certbot renew --dry-run
```

## Container Management

### Start/Stop/Restart

```bash
# Start
docker compose up -d

# Stop
docker compose down

# Restart
docker compose restart

# Rebuild and restart
docker compose up -d --build
```

### View Logs

```bash
# Follow logs
docker compose logs -f

# View last 100 lines
docker compose logs --tail=100

# Application logs are also available in ./logs/ directory
tail -f logs/juvavum.log
```

### Update Application

```bash
# Pull latest changes
git pull

# Rebuild and restart
docker compose up -d --build

# Clean up old images
docker image prune -f
```

## Monitoring

### Health Check

The application includes a health check endpoint:

```bash
# Internal health check (from droplet)
curl -k https://localhost/actuator/health

# External health check
curl https://your-domain.com/actuator/health
```

### Resource Usage

```bash
# Check container resource usage
docker stats juvavum-rest

# Check disk usage
du -sh logs/
df -h
```

## Troubleshooting

### Container won't start

```bash
# Check logs
docker compose logs

# Check if port 443 is already in use
sudo netstat -tlnp | grep :443

# Verify certificates
ls -la certs/
```

### SSL Certificate Issues

```bash
# Verify certificate conversion
openssl pkcs12 -info -in certs/keystore.p12 -nodes

# Check Let's Encrypt certificates
sudo certbot certificates

# Manually renew certificates
sudo certbot renew --force-renewal
```

### Permission Issues

```bash
# Ensure proper ownership
sudo chown -R $USER:$USER certs/ logs/

# Check certificate permissions
ls -la certs/keystore.p12
```

## Backup

### Backup Certificates and Data

```bash
# Backup certificates
sudo tar czf letsencrypt-backup.tar.gz /etc/letsencrypt/

# Backup application data
tar czf juvavum-backup.tar.gz certs/ logs/
```

## Performance Tuning

### Adjust JVM Memory

Edit `.env` file:
```env
# For 1GB droplet
JAVA_OPTS=-Xms256m -Xmx512m

# For 2GB droplet
JAVA_OPTS=-Xms512m -Xmx1024m

# For 4GB+ droplet
JAVA_OPTS=-Xms1024m -Xmx2048m
```

Then restart:
```bash
docker compose restart
```

## Additional Notes

- The application runs on port 443 (HTTPS only)
- Port 80 (HTTP) is not exposed by the container
- Logs are persisted in the `./logs/` directory
- Certificates are mounted read-only from `./certs/` directory
- The container runs as a non-root user for security
- Health checks run every 30 seconds

## Support

For issues or questions, refer to the main Juvavum repository documentation.
