Kogito Codegen
==============

This repository contains the shared (Maven Plug-In, Quarkus Extension, ...)
code generation logic for Kogito: processes, rules, decisions, etc.

- `ApplicationGenerator` is the main entry point. The fluent API allows to 
configure its global behavior.

    ```java
    ApplicationGenerator appGen =
            new ApplicationGenerator(context)
                    .withAddons(...);
    ```

- Each supported component (process, rules, etc.) implements the `Generator` 
  interface
-`Generator`s are plugged into the `ApplicationGenerator` instance
- Upon construction, a `Generator` is given the path(s) of the directory/files 
  that it must process. Scanning of the directory take place contextually
- Each `Generator` may come with its own specific configuration

    ```java
    appGen.setupGenerator(RuleCodegen.ofPath(context, ruleSourceDirectory))
            .withKModule(getKModuleModel())
            .withClassLoader(...);
    
    appGen.setupGenerator(ProcessCodegen.ofPath(context, processSourceDirectory));
    ```

- Each `Generator` should delegate to a subcomponent, to process a single
  component. E.g., the `ProcessCodegen` should 
  delegate to a `ProcessGenerator` to work on a single process; `RuleCodegen`
  should delegate to a `RuleUnitGenerator`, etc.
  
  note: naming convention may vary in the future
    
- The `ApplicationGenerator#generate()` method starts the code generation
  procedure, delegating to each `Generator` where appropriate.
  
- Generators **do not** write files to disk, rather return a `GeneratedFile`
  instance, with the relative file path (derived from the original path
  and further analysis on the contents of the file) and the byte array
  of the contents of the file to be dumped to disk.

# Generated Application file

The result of the processing is the main entry point `your.group.id.Application`.

- Components are organized into "sections". The idea, is that for a component C,
  it is possible to invoke some method such that an instance of C is returned.
  e.g.:
  
   * for process P, one may write `new Application().get(Processes.class).create("P")`
   * for rule unit R, one may write `new Application().get(RuleUnits.class).create("R")`
  
  note: specific APIs may vary.

```java
package your.group.id;
public class Application implements org.kie.kogito.Application {
    
    org.kie.kogito.Config config = new ApplicationConfig();
    RuleUnits ruleUnits = new RuleUnits(); // rule unit section
    Processes processes = new Processes(); // process section

    public class RuleUnits implements org.kie.kogito.rules.RuleUnits {
        ...
    }
    public class Processes implements org.kie.kogito.process.Processes {
        ...
    }
}
```

This Application API is currently not meant for direct user-level code
consumption, but this is expected to be a future possibility.

# Additionally-generated files

Implementations may (and usually *do*) generate additional source code. 
In particular:
 
- Rules generate source code for the RuleUnit implementation **and** the 
  executable model description
- Processes generate source code for their Process implementation using
  their specific executable model description
- Most Generators also generate `Resources`, i.e. REST API endpoints.  

