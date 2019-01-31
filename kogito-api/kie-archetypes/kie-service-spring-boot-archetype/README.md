# KIE Service Spring Boot Archetype

Archetype used to build new KIE Service Spring Boot Applications.

Generate Spring Boot Service app
-----------------------------------
1. Build the kie-service-spring-boot-archetype module (mvn clean install)
2. Change to directory of your choice where you want to build the 
app from this archetype.
3. Create your new app from the archetype with:
```
mvn archetype:generate 
   -DarchetypeGroupId=org.kie 
   -DarchetypeArtifactId=kie-service-spring-boot-archetype 
   -DarchetypeVersion=7.18.0-SNAPSHOT
```
or use this one-liner

```
mvn archetype:generate -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-service-spring-boot-archetype -DarchetypeVersion=7.18.0-SNAPSHOT
```
4. Change the prompted values during the generation as needed (or leave the defaults)
5. Compile and test your generated base kjar project with 
```
mvn clean install
```
6. Start your app with
```
mvn clean spring-boot:run
```
6. Access your app in browser
```
http://localhost:8090
```

Building and starting your application with launch scripts
-----------------------------------
An alternative option to build and lanuch your application is with the build scripts. Your application
includes both shell scripts for Unix environments:
```
launch.sh
launch-dev.sh
```
as well as batch scripts for Windows environments:
```
launch.bat
launch-dev.bat
```


These script will try to find the apps model and kjar projects (in parent folder) and build
those before building and starting your application.

To run the shell scripts, you need to give it appropriate permissions, for example
```
chmod 755 launch.sh
./launch.sh clean install
```

and same for the shell dev script:
```
chmod 755 launch-dev.sh
./launch-dev.sh clean install
```

This is not required for the batch scripts which you can just execute with
```
launch.bat clean install
```
or 
```
launch-dev.bat clean install
```

The launch-dev (sh or bat) script will launch you application in development mode, which means it will require
connection to the controller (workbench). By default the controller is set to
```
http://localhost:8080/jbpm-console/rest/controller
```

If your workbench lives under a different host/port
you can change this value by editing the src/main/resources/application-dev.properties file in your 
project, specifically change the value:
```
kieserver.controllers=http://localhost:8080/jbpm-console/rest/controller
```
to whatever you have set up locally.

Default Basic Authentication
-----------------------------------
Your generated app has basic authentication built in for urls with path /rest/*. You can always change this later to add/remove users as needed.
By default there is one user set up in your application with
```
username: user
password: user
```

Manage Apps KIE Server capabilities
-----------------------------------
You can manage what Kie server capabilities are included in your 
service app with the property
```
-DappType
```
If you don't manually specify this property
it will be set to "bpm".

Currently there are three possible choices 
for this property:
1. bpm: includes BRM, BPM, Case Management, BPM-UI, and DMN
2. brm: includes BRM and DMN
3. planner: includes BRM, BRP, and DMN

So to build an "planner" service app you would use the command:
```
mvn archetype:generate 
   -DarchetypeGroupId=org.kie 
   -DarchetypeArtifactId=kie-service-spring-boot-archetype 
   -DarchetypeVersion=7.18.0-SNAPSHOT
   -DappType=planner
```
And similar for the other two available options.

Changing the default KIE Runtime version
-----------------------------------
You can define a specific KIE Runtime version to be used with the following property:
```
-DkieVersion=YOUR_KIE_VERSION
```

The default value of the kie runtime is this archetypes version.

Changing the default app package
-----------------------------------
You can specify your own package structure for Java files for your app by setting following property:
```
-Dpackage=YOUR_APP_PACKAGE
```

The default value of this property is the archetype groupId. 


Changing your apps address and port number
-----------------------------------
You can change your applications address and port with the following proerties
```
-DappServerAddress=YOUR_SERFVER_ADDRESS
-DappServerPort=YOUR_SERVER_PORT
```

The default values if not specified for these are "localhost" and "8090".

Changing database type
-----------------------------------
Your application comes by default with three db profiles, namely h2 (default), mysql, and postgres.

Look at your apps generated pom.xml file to see these profiles. 
Your app also comes with three spring application properties files, namely
application.properties, application-mysql.properties and application-postgres.properties.
These have the predefined db setup for h2, mysql, and postgres respectively.

Note that to run your app with the mysql or postgres db you have to update the
```
spring.datasource.url
```
property in the respective application property files to reflect what is running on your system.
The default h2 setup uses an file based setup so no specific setup is needed.

Once you have this set up, you can build your application with:

```
mvn clean install -P mysql
or
mvn clean install -P postgres
```

and then run it with

```
cd target

java -Dspring.profiles.active=mysql -jar APP_NAME-APP-VERSION.jar
or
java -Dspring.profiles.active=postgres -jar APP_NAME-APP-VERSION.jar
```
where APP_NAME and APP-VERSION reflect the real name and version of your app you define 
when you generate the app.

This setup allows you to easily add more db setups. Just add your profile with
needed depends in your apps pom.xml and then create a new application-YOUR_DB.properties
file where you can update the data source configuration to reflect your db values.

Remote debugging
-----------------------------------
By default your generated application will have remote debugging disabled. If you want 
to generate an application with remote debugging enabled add the following option when 
running mvn archetype:generate:

```
    -DremoteDebugEnabled=true
```

If you have already generated you application and would like to enable remote debugging after,
find your apps pom.xml file and make sure you have there:

```
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <jvmArguments>-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005</jvmArguments>
            </configuration>
      </plugin>
    </plugins>
  </build>
```

and change the remote debug address as you wish or leave the default as is.

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
