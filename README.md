# Seamless deployment with Docker Compose + ~~Nginx~~ + Websockets demo

**This branch contains a demonstration that uses Caddy, not nginx**

Turns out this is way easier with Caddy.

## Seamless deployment with Caddy

When our backend goes down, Caddy can buffer our requests while it waits
for it to come back up. As a result, as long as our Caddyfile has a sufficiently
large `lb_try_duration` value (long enough to wait out the deploy) then
simply deploying a new version of our service will not cause any errors to our
clients (though they may see a delay in response).
