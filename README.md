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
- Mit application.yml habe ich es nicht zum Laufen gekriegt. Vielleicht liegt es noch an einem fehlenden yml-Support, obwohl ich diesen explizit _nicht_ ausgeschaltet habe.
- Jaxb macht nun Probleme. Werfe ich wohl beim Refactoring eh über Bord. AvService verwendet es.

