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

# drools-drlonyaml

This tool offer a mechanism of bidirectional translation between *a subset* of DRL and an analogous YAML representation.

This is an *experimental* feature. The APIs may change in future versions.

Please note that there is *no* guarantee that roundtrips of the translation process results in the original file;
for instance, this translation process does not maintain across translations the original formatting, whitespaces, etc.

## Example

Given a DRL snippet of:

```
rule "Fix the PersistentVolume Claim Pod PENDING"
when
  $pvc : PersistentVolumeClaim( status.phase == "Pending" )
  $pod : Pod( status.phase == "Pending" )
  Volume( persistentVolumeClaim!.claimName == $pvc.metadata.name ) from $pod.spec.volumes
then
  insert(new Advice("Fix the PersistentVolume","Pod PENDING: "+$pod.getMetadata().getName() + " pvc PENDING: "+$pvc.getMetadata().getName()));
end

```

this capability can translate it to YAML as:

```yaml
rules:
- name: Fix the PersistentVolume Claim Pod PENDING
  when:
  - given: PersistentVolumeClaim
    as: $pvc
    having:
    - status.phase == "Pending"
  - given: Pod
    as: $pod
    having:
    - status.phase == "Pending"
  - given: Volume
    having:
    - persistentVolumeClaim!.claimName == $pvc.metadata.name
    from: $pod.spec.volumes
  then: |
    insert(new Advice("Fix the PersistentVolume","Pod PENDING: "+$pod.getMetadata().getName() + " pvc PENDING: "+$pvc.getMetadata().getName()));
```

The YAML may be translated back again into a DRL file using this capability.
