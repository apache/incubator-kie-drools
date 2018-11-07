# KIE Model Archetype

Archetype used to build new KIE Model which can be used to contain data model classes shared between business assets
and service projects.


Generate project
--------------------
1. Build the kie-model-archetype module (mvn clean install)
2. Change to directory of your choice where you want to build the 
project from this archetype.
3. Create your new project from the archetype with:
```
mvn archetype:generate 
   -DarchetypeGroupId=org.kie 
   -DarchetypeArtifactId=kie-model-archetype 
   -DarchetypeVersion=7.15.0-SNAPSHOT
```
or use this one-liner

```
mvn archetype:generate -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-model-archetype -DarchetypeVersion=7.15.0-SNAPSHOT
```
4. Change the prompted values during the generation as needed (or leave the defaults)
5. Compile and test your generated project with 
```
mvn clean install
```
