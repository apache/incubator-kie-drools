DMN Test Resources
==================

This module is meant to be the ultimate single-source-of truth about DMN models used for testing purposes.

The models are stored under `src/test/resources`, so the module has to be imported has

```xml
<dependency>
      <groupId>org.kie</groupId>
      <artifactId>kie-dmn-test-resources</artifactId>
      <classifier>tests</classifier>
      <scope>test</scope>
    </dependency>
```

to have them available.

Models are split in two categories: _valid_models_ and __invalid_models_. 
The former are valid models, expected to succeed the DMN validation.
The latter are invalid ones, expected to have some errors, and used to check that such errors are detected by validation.

For both categories, there is a subdivision: _DMNv1_x_ and _DMNV1_5_. 
The former are all the models created before the 1.5 implementation, that are hard to sort based on the version relates to. 
The latter contains models with 1.5. specific feature.
In the future, for each new DMN release there will be a specific folder.