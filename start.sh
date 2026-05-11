#!/bin/bash
# Run this after deploy.sh and re-login.
set -e

if [ ! -f .env.prod ]; then
  echo "ERROR: .env.prod file not found. Create it first (see README)."
  exit 1
fi

docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
echo "Taskly is starting. Check status with: docker compose -f docker-compose.prod.yml ps"
