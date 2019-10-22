Contributing to Kogito
--------------------

All contributions are welcome! Before you start please read the [Developing Drools and jBPM](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md) guide.

Building from source
--------------------

Check out the source:
```
git clone git@github.com:kiegroup/kogito-apps.git
```

> If you don't have a GitHub account use this command instead:
> ```
> git clone https://github.com/kiegroup/kogito-apps.git
> ```

Build with Yarn:
```bash
cd kogito-apps
yarn run init

#prod
yarn run build:prod

# dev
yarn run build # skips integration tests and production packing
yarn run build:fast # skips lint and unit tests
```

> Final artifacts will be on `packages/*/dist` directories.