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
