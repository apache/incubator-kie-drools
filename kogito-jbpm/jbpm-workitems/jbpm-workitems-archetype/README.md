# WorkItem Archetype

Archetype used to build new jBPM Workitems.

This Archetype allows you to easily start building a new jBPM Workitem.
It creates your WorkItem Handler Maven project that includes the base Handler class, test, and
a zip file that includes all needed files to upload to a jBPM workitem repository.

How to run it
--------------------
1. build the jbpm-workitems module (mvn clean install)
2. Change to directory of your choice where you want to build the 
base workitem handler from this archetype.
3. Create your new workitem handler from the archetype with:
```
mvn archetype:generate 
   -DarchetypeGroupId=org.jbpm 
   -DarchetypeArtifactId=jbpm-workitems-archetype 
   -DarchetypeVersion=7.11.0-SNAPSHOT
   -Dversion=7.11.0-SNAPSHOT
   -DgroupId=org.jbpm.contrib 
   -DartifactId=myworkitem 
   -DclassPrefix=MyWorkItem
   -DarchetypeCatalog=local
```
or use this one-liner

```
mvn archetype:generate -DarchetypeGroupId=org.jbpm -DarchetypeArtifactId=jbpm-workitems-archetype -DarchetypeVersion=7.11.0-SNAPSHOT -Dversion=7.11.0-SNAPSHOT -DgroupId=org.jbpm.contrib -DartifactId=myworkitem -DclassPrefix=MyWorkItem -DarchetypeCatalog=local
```
4. Change the prompted values during the generation as needed (or leave the defaults)
5. Compile and test your generated workitem handler with 
```
mvn clean install
```
6. Package your workitem with
```
mvn package
```
This will create a zip file in your projects /target directory. This zip when extracted includes the correct
directory structure (including the index.conf file) which then you can easily upload to a jBPM workitem repository
or use directly as-is to upload your new workitem to the workbench via the jBPM Designer workitem repository feature. 
