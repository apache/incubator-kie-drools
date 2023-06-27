# Kogito Apps

## Contributing to Kogito

All contributions are welcome! Before you start please read the [contribution guide](https://github.com/kiegroup/kogito-runtimes/blob/main/CONTRIBUTING.md).

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

- Install [pnpm](https://pnpm.io/)
```bash
cd kogito-apps/ui-packages
npm install -g pnpm
```

- Install projects dependencies using pnpm
```bash
cd kogito-apps/ui-packages
pnpm install
```

- Build with pnpm:
```bash
cd kogito-apps/ui-packages
pnpm run init

#prod
pnpm run build:prod

# dev
pnpm run build # skips integration tests and production packing
```

> Final artifacts will be on `packages/*/dist` directories.

## Management Console

For detailed instructions on how to develop and run the Management Console, please check instructions on the specific 
[README](./ui-packages/packages/management-console/README.md) file.

## `ui-packages` dependencies

`ui-packages` are managed with [pnpm Workspaces](https://pnpm.io/workspaces). Dependencies shared between packages are listed in the top-level [`package.json`](./ui-packages/package.json).

A `locktt` npm script relying on [lock-treatment-tool](https://github.com/Ginxo/lock-treatment-tool) is available to allow the usage of a private npm registry during building.

`locktt` replaces the host from [`ui-packages/pnpm-lock`](./ui-packages/pnpm-lock) resolved field with the custom registry. It is set to run just before the execution of `pnpm install`. See [`ui-packages/pom.xml`](./ui-packages/pom.xml) for further details.

## Skipping frontend build

To skip the frontend build when running maven, simply execute Maven with the following parameters

```bash
mvn clean install -Dskip.ui.build -Dskip.ui.deps
```

## Getting Help
### Issues
- Do you have a [minimal, reproducible example](https://stackoverflow.com/help/minimal-reproducible-example) for your issue?
  - If so, please open a Jira for it in the [Kogito project](https://issues.redhat.com/projects/KOGITO/summary) with the details of your issue and example.
- Are you encountering an issue but unsure of what is going on? 
  - Start a new conversation in the Kogito [Google Group](https://groups.google.com/g/kogito-development), or open a new thread in the [Kogito stream](https://kie.zulipchat.com/#narrow/stream/232676-kogito) of the KIE Zulip chat.
  - Please provide as much relevant information as you can as to what could be causing the issue, and our developers will help you figure out what's going wrong.

### Requests
- Do you have a feature/enhancement request?
  - Please open a new thread in the [Kogito stream](https://kie.zulipchat.com/#narrow/stream/232676-kogito) of the KIE Zulip chat to start a discussion there.
