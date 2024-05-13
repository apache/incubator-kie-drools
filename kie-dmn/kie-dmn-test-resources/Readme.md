<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

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