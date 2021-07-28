# Seamless deployment with Docker Compose + Nginx + Websockets demo

This repo demonstrates how to set up a seamless deployment using Docker Compose
and Nginx. Websockets are handled too, by having the websocket server tell its
clients to reconnect while both server instances are running.

## Preparation

1. Build the server: run `mvn package` in the server folder
2. Run `docker-compose up -d` in the root
3. Visit http://localhost, and connect to the websocket

## Deployment

1. Run`./deploy.sh` in this folder

That's all.

## Observe

Run `./deploy.sh` again, but this time:

1. `while true; do curl -I https://www.ktbyte.com/login ; done` - observe the
randomly generated server ID change once the new server starts responding
2. Open the console in your websocket client - observe the disconnect/reconnect
event but no actual connection errors occur

## Open questions

Briefly, two instances of the server are running, both identified with the same
hostname. Nginx always seems to choose the new one. Why the new and not the old?
Or why not both, as the Tines blog post suggests? Perhaps a different nginx config?

## Credit

* The zero downtime deployment script is adopted from https://engineering.tines.com/blog/simple-zero-downtime-deploys
* The websocket client page is from https://www.pegaxchange.com/2018/03/23/websocket-client/
* The websocket reconnect idea comes from https://nimblea.pe/monkey-business/2015/05/19/achieving-zero-downtime-deployments-with-nodejs-and-websockets/