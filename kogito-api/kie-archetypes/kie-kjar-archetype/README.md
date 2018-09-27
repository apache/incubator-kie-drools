# KIE KJAR Archetype

Archetype used to build new KIE KJAR which can be imported into the Workbench.

This Archetype is capable of generating both a "base" and a "case" kjar project.
This is similar to when creating a new project inside the KIE Workbench where you can
specify one of these two options when creating a new project.

Generate KJAR Base project
--------------------
1. Build the kie-kjar-archetype module (mvn clean install)
2. Change to directory of your choice where you want to build the 
base kjar from this archetype.
3. Create your new base kjar project from the archetype with:
```
mvn archetype:generate 
   -DarchetypeGroupId=org.kie 
   -DarchetypeArtifactId=kie-kjar-archetype 
   -DarchetypeVersion=7.13.0-SNAPSHOT
```
or use this one-liner

```
mvn archetype:generate -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-kjar-archetype -DarchetypeVersion=7.13.0-SNAPSHOT
```
4. Change the prompted values during the generation as needed (or leave the defaults)
5. Compile and test your generated base kjar project with 
```
mvn clean install
```

Generate KJAR Case project
---------------------------

1. build the kie-kjar-archetype module (mvn clean install)
2. Change to directory of your choice where you want to build the 
case kjar from this archetype.
3. Create your new case kjar project from the archetype with:
```
mvn archetype:generate 
   -DarchetypeGroupId=org.kie 
   -DarchetypeArtifactId=kie-kjar-archetype 
   -DarchetypeVersion=7.13.0-SNAPSHOT
   -DcaseProject=true
```
or use this one-liner

```
mvn archetype:generate -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-kjar-archetype -DarchetypeVersion=7.13.0-SNAPSHOT -DcaseProject=true
```
4. Change the prompted values during the generation as needed (or leave the defaults)
5. Compile and test your generated case kjar project with 
```
mvn clean install
```

Changing the default KIE Runtime version
-----------------------------------
You can define a specific KIE Runtime version to be used with the following property:
```
-DkieVersion=YOUR_KIE_VERSION
```

Import project to KIE Workbench
---------------------------------

After generating your project you can import it into the KIE Workbench
In your project directory:
1. Initialize git for your project
```
git init
```
2. Add all your project files to git
```
git add .
```
3. Commit all your project files to git
```
git commit -m "YOUR COMMIT MESSAGE HERE"
```

In your KIE Workbech Instance
1. In the Space View select "Import Project"
2. Type the path to your git kjar project, for example 
```
file://myuser/mypath/to/my/new/project/projectname
```

Workbench should now import your created kjar project and you can start
working on adding new assets to it etc. 

Troubleshooting
-----------------------------------
This archetype requires maven-archetype-plugin version 3.0.1 or above.
In case you run into issues with the post generation scripts 
not being executed during archetype generation run it once with
force update maven option
```
-U 
```
This will make sure you up the 3.0.1 version of maven-archetype-plugin
and get it installed in your local maven repo.
