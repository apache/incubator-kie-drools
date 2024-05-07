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

# Environments CI scripts

This folder contains update scripts which would be called for a specific environments.

## Updating branch for an environment

The `.ci/environments/update.sh` script is responsible to execute the update/migration.

It expect at least one parameter, namely the "environment", corresponding to the folder which will be "executed" (e.g. *quarkus-3*, *quarkus-main*, etc) and optionally some more which would be transmitted to the `before.sh` and/or `after.sh` scripts.

Please look at the specific environment README file for more information about the needed parameters for the before and after scripts.

### Execute script

To execute migration from an environment, ***WITHOUT*** extra parameters:

`.ci/environments/update.sh <environment>`

To execute migration from an environment ***WITH*** extra paramerers:

`.ci/environments/update.sh <environment> <extra_param1> <extra_param2>`

### What happens ?

When called, the update script will (in order and if exists):

1. call `<environment>/before.sh` script with extra parameters
2. apply patches from the `<environment>/patches` folder in alphanumeric order
3. call `<environment>/before.sh` script with extra parameters