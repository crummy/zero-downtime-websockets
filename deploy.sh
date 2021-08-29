set -eu

zero_downtime_deploy() {
  service_name=server

  # bring a new container online
  docker-compose up -d --no-deps --force-recreate $service_name

  echo "succesfully redeployed $service_name"
}

zero_downtime_deploy