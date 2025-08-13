docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):$(pwd) -w $(pwd) docker/compose:latest docker-compose -f docker-compose.dev.yml up -d
