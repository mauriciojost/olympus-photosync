# README

There are two reasons for this module:

- To be able to build the project fully in Docker (no need of installing sbt or java on your machine, only Docker). People that develop with on-host (instead of in-Docker) tools should not be affected
- To be able to launch on different architectures, like ARM architectures.

This can be built with the docker command:

```
build -t photosync -f .\extras\docker\Dockerfile .
```

Thanks [Michael Friis](https://github.com/friism)!
