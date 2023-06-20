To compile and run this project in the main Maven build, ensure the necessary profile property is set.

Eg.

```
mvn clean install -PfullProfile
 or
mvn clean install -Dfull
```

### Trouble-shooting
If you hit a strange error by gwt-maven-plugin after changing a class in `kie-dmn-feel` complaining that the class doesn't seem to be changed, check if the same FQCN class exists in `kie-dmn-feel-gwt`. You would need to change the class in the same way.

for example)
I added `CANNOT_BE_SIGNED` to `org.kie.dmn.feel.util.Msg` in `kie-dmn-feel`.

```
[2021-11-02T07:18:51.749Z] [INFO] --- gwt-maven-plugin:2.9.0:compile (default) @ kie-dmn-feel-gwt-showcase ---
[2021-11-02T07:18:51.749Z] [INFO] auto discovered modules [org.kie.dmn.feel.FEELShowcaseWebapp]
[2021-11-02T07:19:14.267Z] [INFO] Compiling module org.kie.dmn.feel.FEELShowcaseWebapp
[2021-11-02T07:19:14.267Z] [INFO]    Tracing compile failure path for type 'org.kie.dmn.feel.lang.ast.SignedUnaryNode'
[2021-11-02T07:19:14.267Z] [INFO]       [ERROR] Errors in 'jar:file:/home/jenkins/workspace/KIE/7.x/pullrequest/drools-7.x.pr/bc/kiegroup_drools/kie-dmn/kie-dmn-feel/target/kie-dmn-feel-7.75.0-SNAPSHOT-sources.jar!/org/kie/dmn/feel/lang/ast/SignedUnaryNode.java'
[2021-11-02T07:19:14.267Z] [INFO]          [ERROR] Line 66: CANNOT_BE_SIGNED cannot be resolved or is not a field
[2021-11-02T07:19:14.267Z] [INFO]    [ERROR] Aborting compile due to errors in some input files
```

In this case, I have to change `org.kie.dmn.feel.util.Msg` in `kie-dmn-feel-gwt` as well.
