# cadastralinfo-client

## Develop
```
./mvnw spring-boot:run -Penv-dev -pl *-server -am
```

```
./mvnw gwt:codeserver -pl *-client -am
```


## Build

### Docker image on Apple Silicon
```
docker buildx build --platform linux/amd64,linux/arm64 --push -t edigonzales/cadastralinfo-client-jvm -f cadastralinfo-server/src/main/docker/Dockerfile.jvm .
```

### Native image
Seems to be broken. Fehlen config files?

