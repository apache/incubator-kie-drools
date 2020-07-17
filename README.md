# Kogito Apps

## Contributing to Kogito

All contributions are welcome! Before you start please read the [contribution guide](https://github.com/kiegroup/kogito-runtimes/blob/master/CONTRIBUTING.md).

## Building from source

- Check out the source:
```
git clone git@github.com:kiegroup/kogito-apps.git
```

> If you don't have a GitHub account use this command instead:
> ```
> git clone https://github.com/kiegroup/kogito-apps.git
> ```
 
- Install Node and NPM package manager

See detailed instructions [here](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) for your OS.

- Install [Yarn](https://classic.yarnpkg.com/)
```bash
cd kogito-apps/ui-packages
npm install -D yarn
```

- Install projects dependencies using Yarn
```bash
cd kogito-apps/ui-packages
yarn install
```

- Build with Yarn:
```bash
cd kogito-apps/ui-packages
yarn run init

#prod
yarn run build:prod

# dev
yarn run build # skips integration tests and production packing
yarn run build:fast # skips lint and unit tests
```

> Final artifacts will be on `packages/*/dist` directories.

## Management Console

For detailed instructions on how to develop and run the Management Console, please check instructions on the specific 
[README](./ui-packages/packages/management-console/README.md) file.

## `ui-packages` dependencies

`ui-packages` are managed with [Yarn Workspaces](https://classic.yarnpkg.com/en/docs/workspaces/) and [Lerna](https://github.com/lerna/lerna). Dependencies shared between packages are listed in the top-level [`package.json`](./ui-packages/package.json).

A `locktt` npm script relying on [lock-treatment-tool](https://github.com/Ginxo/lock-treatment-tool) is available to allow the usage of a private npm registry during building.

`locktt` replaces the host from [`ui-packages/yarn.lock`](./ui-packages/yarn.lock) resolved field with the custom registry. It is set to run just before the execution of `yarn install`. See [`ui-packages/pom.xml`](./ui-packages/pom.xml) for further details.