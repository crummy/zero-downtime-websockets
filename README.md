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

## How does it work?

1. We use `docker-compose` to spin up a second instance of the server
2. We tell nginx to reload
    * At this point, Nginx will route new connections will go to the second instance. But long-
    running HTTP requests and websocket connections may still be active from the
    first.
3. We query the second server via IP to wait until it's up
    * actually on Mac this doesn't work, so we just wait 5 seconds
4. We use `docker-compose` to tell the first instance to shut down
5. When the first instance is told to shut down, it tells every websocket
   client it knows of to disconnect and reconnect. When they do this,
   their new connection is made to the second server.
6. We tell nginx to reload

## Notes

There is a brief moment where only the new server is active, but Nginx does not
realise it yet. Normally with two servers active, Nginx by default alternates
requests between the two, but in this case requests to the old server (which no
long exists) will time out - and when that does, Nginx will re-send the request
to the new server, which will succeed and Nginx will mark the old as down.

This means, in a worst case scenario, some requests will take 2s extra (the
timeout is configured in nginx.conf) than they normally would.

## Credit

* The zero downtime deployment script is adopted from https://engineering.tines.com/blog/simple-zero-downtime-deploys
* The websocket client page is from https://www.pegaxchange.com/2018/03/23/websocket-client/
* The websocket reconnect idea comes from https://nimblea.pe/monkey-business/2015/05/19/achieving-zero-downtime-deployments-with-nodejs-and-websockets/