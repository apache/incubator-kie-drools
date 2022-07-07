# Rule Induction to DMN utility

Experimental DMN generator for PMML RuleSet models to be converted to DMN decision tables

## How to use it

Please refer to the command line utility help available with the `--help` command line option:

```sh
java -jar kie-dmn-ruleset2dmn-cli-8.24.0-SNAPSHOT.jar --help
```

For example:

![help command line](cli-help.png)

## Example usages:

```sh
java -jar kie-dmn-ruleset2dmn-cli/target/kie-dmn-ruleset2dmn-cli-8.24.0-SNAPSHOT.jar  kie-dmn-ruleset2dmn/src/test/resources/wifi.pmml 
java -jar kie-dmn-ruleset2dmn-cli/target/kie-dmn-ruleset2dmn-cli-8.24.0-SNAPSHOT.jar -o=wifi.dmn kie-dmn-ruleset2dmn/src/test/resources/wifi.pmml 
cat kie-dmn-ruleset2dmn/src/test/resources/wifi.pmml | java -jar kie-dmn-ruleset2dmn-cli/target/kie-dmn-ruleset2dmn-cli-8.24.0-SNAPSHOT.jar -o=wifi.dmn
cat kie-dmn-ruleset2dmn/src/test/resources/wifi.pmml | java -jar kie-dmn-ruleset2dmn-cli/target/kie-dmn-ruleset2dmn-cli-8.24.0-SNAPSHOT.jar 
```
