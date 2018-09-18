def quietMode = true;
if(System.getenv('MAVEN_CMD_LINE_ARGS') != null && !System.getenv('MAVEN_CMD_LINE_ARGS').contains("-q") && !System.getenv('MAVEN_CMD_LINE_ARGS').contains("-quiet")) {
    quietMode = true;
}

def logOut(log, quietMode) {
    if(!quietMode) {
        println log;
    }
}

logOut("[Running post-generation script]", quietMode);


def appType = "${appType}";
def myAppArtifactId = "${artifactId}";
def myAppVersion = "${version}";
def myAppPortId = "${appServerPort}";
def kjarArtifactId = "${kjarArtifactId}";
def kjarVersion = "${kjarVersion}";
def kjarContainerId = "";
def remoteDebugEnabled = "${remoteDebugEnabled}";

if( kjarArtifactId != "none" && kjarVersion != "none") {
    kjarContainerId = kjarArtifactId + "-" + kjarVersion.replaceAll("\\.", "_");
}

def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId());

def appPropertiesFile = new File(moduleDir, "src/main/resources/application.properties");
def appPropertiesFileMySql = new File(moduleDir, "src/main/resources/application-mysql.properties");
def appPropertiesFilePostgres = new File(moduleDir, "src/main/resources/application-postgres.properties");
def devAppPropertiesFile = new File(moduleDir, "src/main/resources/application-dev.properties");
def pomFile = new File(moduleDir, "pom.xml");
def indexFile = new File(moduleDir, "src/main/resources/static/index.html");
def launchFile = new File(moduleDir, "launch.sh");
def devLaunchFile = new File(moduleDir, "launch-dev.sh");
def batLaunchFile = new File(moduleDir, "launch.bat");
def batDevLaunchFile = new File(moduleDir, "launch-dev.bat");
def kieServerStateFile = new File(moduleDir, myAppArtifactId + ".xml");

def kieServerCapabilitiesMarker = 'KIE_SERVER_CAPABILITIES_MARKER';
def jbpmConfigMarker = 'JBPM_CONFIG_MARKER';
def springBootStarterMarker = 'KIE_SPRING_BOOT_STARTER_MARKER';
def indexCSSMarkerBA = "INDEX_CSS_MARKER_BA";
def indexIconMarkerBA = "INDEX_ICON_MARKER_BA";
def indexCSSMarkerDM = "INDEX_CSS_MARKER_DM";
def indexIconMarkerDM = "INDEX_ICON_MARKER_DM";
def indexCSSMarkerBO = "INDEX_CSS_MARKER_BO";
def indexIconMarkerBO = "INDEX_ICON_MARKER_BO";
def myServiceNameMarker = 'MYSERVICE_NAME_MARKER';
def myServiceVersionMarker = 'MYSERVICE_VERSION_MARKER';
def myServicePortMarker = 'MYSERVICE_PORT_MARKER';
def kjarContainerIdMarker = "KJAR_CONTAINER_ID_MARKER";
def dbProfilesMarker = "DB_PROFILES_MARKER";
def remoteDebugMarker = "REMOTE_DEBUG_MARKER";

def BPMSpringBootStarterDepends = """
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-server-spring-boot-starter</artifactId>
    <version>\${version.org.kie}</version>
</dependency>
""";

def BRMSpringBootStarterDepends = """
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-server-spring-boot-starter-drools</artifactId>
    <version>\${version.org.kie}</version>
</dependency>
""";

def PlannerSpringBootStarterDepends = """
<dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-server-spring-boot-starter-optaplanner</artifactId>
    <version>\${version.org.kie}</version>
</dependency>
""";

def serverCapabilitiesBPM = """
#kie server capabilities
kieserver.drools.enabled=true
kieserver.dmn.enabled=true
kieserver.jbpm.enabled=true
kieserver.jbpmui.enabled=true
kieserver.casemgmt.enabled=true
""";

def serverCapabilitiesBRM = """
#kie server capabilities
kieserver.drools.enabled=true
kieserver.dmn.enabled=true
""";

def serverCapabilitiesPlanner = """
#kie server capabilities
kieserver.drools.enabled=true
kieserver.dmn.enabled=true
kieserver.optaplanner.enabled=true
""";

def BPMJBPMConfig = """
#jbpm configuration
jbpm.executor.enabled=false
#jbpm.executor.retries=5
#jbpm.executor.interval=3
#jbpm.executor.threadPoolSize=1
#jbpm.executor.timeUnit=SECONDS
""";

def DBProfilesConfig = """
<profile>
  <id>h2</id>
  <activation>
    <activeByDefault>true</activeByDefault>
  </activation>
  <dependencies>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
    </dependency>
  </dependencies>
</profile>

<profile>
  <id>mysql</id>
  <activation>
    <property>
      <name>mysql</name>
    </property>
  </activation>
  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
</profile>

<profile>
  <id>postgres</id>
  <activation>
    <property>
      <name>postgres</name>
    </property>
  </activation>
  <dependencies>
    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
</profile>
""";

def remoteDebugSettings = """
<configuration>
    <jvmArguments>-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005</jvmArguments>
</configuration>
""";

logOut("Updating app configuration for app type: " + appType, quietMode);

if( appType == "bpm" ) {
    logOut("Updating application properties...", quietMode);
    def appPropertiesContent = appPropertiesFile.getText('UTF-8');
    def appPropertiesContentMySql = appPropertiesFileMySql.getText('UTF-8');
    def appPropertiesContentPostgres = appPropertiesFilePostgres.getText('UTF-8');
    def devAppPropertiesContent = devAppPropertiesFile.getText('UTF-8');

    logOut("- adding server capabilities", quietMode);
    appPropertiesContent = appPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesBPM);
    appPropertiesContentMySql = appPropertiesContentMySql.replace(kieServerCapabilitiesMarker, serverCapabilitiesBPM);
    appPropertiesContentPostgres = appPropertiesContentPostgres.replace(kieServerCapabilitiesMarker, serverCapabilitiesBPM);
    devAppPropertiesContent = devAppPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesBPM);

    logOut("- adding jbpm configuration", quietMode);
    appPropertiesContent = appPropertiesContent.replace(jbpmConfigMarker, BPMJBPMConfig);
    appPropertiesContentMySql = appPropertiesContentMySql.replace(jbpmConfigMarker, BPMJBPMConfig);
    appPropertiesContentPostgres = appPropertiesContentPostgres.replace(jbpmConfigMarker, BPMJBPMConfig);
    devAppPropertiesContent = devAppPropertiesContent.replace(jbpmConfigMarker, BPMJBPMConfig);

    appPropertiesFile.newWriter().withWriter { w ->
        w << appPropertiesContent
    }

    appPropertiesFileMySql.newWriter().withWriter { w ->
        w << appPropertiesContentMySql
    }

    appPropertiesFilePostgres.newWriter().withWriter { w ->
        w << appPropertiesContentPostgres
    }

    devAppPropertiesFile.newWriter().withWriter { w ->
        w << devAppPropertiesContent
    }

    logOut("Updating pom...", quietMode);
    def pomContent = pomFile.getText('UTF-8');

    logOut("- adding spring boot starter dependency", quietMode);
    pomContent = pomContent.replace(springBootStarterMarker, BPMSpringBootStarterDepends);
    pomContent = pomContent.replace(dbProfilesMarker, DBProfilesConfig);

    if(remoteDebugEnabled == "true") {
        pomContent = pomContent.replace(remoteDebugMarker, remoteDebugSettings);
    } else {
        pomContent = pomContent.replace(remoteDebugMarker, '');
    }

    pomFile.newWriter().withWriter { w ->
        w << pomContent
    }

    logOut("Updating index.html...", quietMode);
    def indexContent = indexFile.getText('UTF-8');
    indexContent = indexContent.replace(indexCSSMarkerBA, 'alert alert-success');
    indexContent = indexContent.replace(indexCSSMarkerDM, 'alert alert-success');
    indexContent = indexContent.replace(indexCSSMarkerBO, 'alert alert-success');
    indexContent = indexContent.replace(indexIconMarkerBA, 'fa fa-check-circle-o');
    indexContent = indexContent.replace(indexIconMarkerDM, 'fa fa-check-circle-o');
    indexContent = indexContent.replace(indexIconMarkerBO, 'fa fa-check-circle-o');
    indexFile.newWriter().withWriter { w ->
        w << indexContent
    }

} else if(appType == "brm") {
    logOut("Updating application properties...", quietMode);
    def appPropertiesContent = appPropertiesFile.getText('UTF-8');
    def devAppPropertiesContent = devAppPropertiesFile.getText('UTF-8');

    logOut("- adding server capabilities", quietMode);
    appPropertiesContent = appPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesBRM);
    devAppPropertiesContent = devAppPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesBRM);

    logOut("- removing jbpm configuration", quietMode);
    appPropertiesContent = appPropertiesContent.replace(jbpmConfigMarker, '');
    devAppPropertiesContent = devAppPropertiesContent.replace(jbpmConfigMarker, '');

    appPropertiesFile.newWriter().withWriter { w ->
        w << appPropertiesContent
    }

    devAppPropertiesFile.newWriter().withWriter { w ->
        w << devAppPropertiesContent
    }
    
    appPropertiesFileMySql.delete();
    appPropertiesFilePostgres.delete();

    logOut("Updating pom...", quietMode);
    def pomContent = pomFile.getText('UTF-8');

    logOut("- adding spring boot starter dependency", quietMode);
    pomContent = pomContent.replace(springBootStarterMarker, BRMSpringBootStarterDepends);
    pomContent = pomContent.replace(dbProfilesMarker, '');

    if(remoteDebugEnabled == "true") {
        pomContent = pomContent.replace(remoteDebugMarker, remoteDebugSettings);
    } else {
        pomContent = pomContent.replace(remoteDebugMarker, '');
    }

    pomFile.newWriter().withWriter { w ->
        w << pomContent
    }

    logOut("Updating index.html...", quietMode);
    def indexContent = indexFile.getText('UTF-8');
    indexContent = indexContent.replace(indexCSSMarkerBA, 'alert alert-danger');
    indexContent = indexContent.replace(indexCSSMarkerDM, 'alert alert-success');
    indexContent = indexContent.replace(indexCSSMarkerBO, 'alert alert-danger');
    indexContent = indexContent.replace(indexIconMarkerBA, 'fa fa-times-circle-o');
    indexContent = indexContent.replace(indexIconMarkerDM, 'fa fa-check-circle-o');
    indexContent = indexContent.replace(indexIconMarkerBO, 'fa fa-times-circle-o');
    indexFile.newWriter().withWriter { w ->
        w << indexContent
    }
} else if(appType == "planner") {
    logOut("Updating application properties...", quietMode);
    def appPropertiesContent = appPropertiesFile.getText('UTF-8');
    def devAppPropertiesContent = devAppPropertiesFile.getText('UTF-8');

    logOut("- adding server capabilities", quietMode);
    appPropertiesContent = appPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesPlanner);
    devAppPropertiesContent = devAppPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesPlanner);

    logOut("- removing jbpm configuration", quietMode);
    appPropertiesContent = appPropertiesContent.replace(jbpmConfigMarker, '');
    devAppPropertiesContent = devAppPropertiesContent.replace(jbpmConfigMarker, '');

    appPropertiesFile.newWriter().withWriter { w ->
        w << appPropertiesContent
    }

    devAppPropertiesFile.newWriter().withWriter { w ->
        w << devAppPropertiesContent
    }
        
    appPropertiesFileMySql.delete();
    appPropertiesFilePostgres.delete();
    

    logOut("Updating pom...", quietMode);
    def pomContent = pomFile.getText('UTF-8');

    logOut("- adding spring boot starter dependency", quietMode);
    pomContent = pomContent.replace(springBootStarterMarker, PlannerSpringBootStarterDepends);
    pomContent = pomContent.replace(dbProfilesMarker, '');

    if(remoteDebugEnabled == "true") {
        pomContent = pomContent.replace(remoteDebugMarker, remoteDebugSettings);
    } else {
        pomContent = pomContent.replace(remoteDebugMarker, '');
    }

    pomFile.newWriter().withWriter { w ->
        w << pomContent
    }

    logOut("Updating index.html...", quietMode);
    def indexContent = indexFile.getText('UTF-8');
    indexContent = indexContent.replace(indexCSSMarkerBA, 'alert alert-danger');
    indexContent = indexContent.replace(indexCSSMarkerDM, 'alert alert-success');
    indexContent = indexContent.replace(indexCSSMarkerBO, 'alert alert-success');
    indexContent = indexContent.replace(indexIconMarkerBA, 'fa fa-times-circle-o');
    indexContent = indexContent.replace(indexIconMarkerDM, 'fa fa-check-circle-o');
    indexContent = indexContent.replace(indexIconMarkerBO, 'fa fa-check-circle-o');
    indexFile.newWriter().withWriter { w ->
        w << indexContent
    }
} else {
    logOut("[ERROR: Invalid app type specified - unable to finish needed configurations!]", quietMode);
}

logOut("Updating launch scripts...", quietMode);
def launchFileContent = launchFile.getText('UTF-8');
def devLaunchFileContent = devLaunchFile.getText('UTF-8');

launchFileContent = launchFileContent.replaceAll(myServiceNameMarker, myAppArtifactId);
launchFileContent = launchFileContent.replaceAll(myServiceVersionMarker, myAppVersion);
launchFileContent = launchFileContent.replaceAll(myServicePortMarker, myAppPortId);
devLaunchFileContent = devLaunchFileContent.replaceAll(myServiceNameMarker, myAppArtifactId);

launchFile.newWriter().withWriter { w ->
    w << launchFileContent
}

devLaunchFile.newWriter().withWriter { w ->
    w << devLaunchFileContent
}

logOut("Updating bat launch scripts...", quietMode);
def batLaunchFileContent = batLaunchFile.getText('UTF-8');
def batDevLaunchFileContent = batDevLaunchFile.getText('UTF-8');

batLaunchFileContent = batLaunchFileContent.replaceAll(myServiceNameMarker, myAppArtifactId);
batLaunchFileContent = batLaunchFileContent.replaceAll(myServiceVersionMarker, myAppVersion);
batLaunchFileContent = batLaunchFileContent.replaceAll(myServicePortMarker, myAppPortId);
batDevLaunchFileContent = batDevLaunchFileContent.replaceAll(myServiceNameMarker, myAppArtifactId);

batLaunchFile.newWriter().withWriter { w ->
    w << batLaunchFileContent
}

batDevLaunchFile.newWriter().withWriter { w ->
    w << batDevLaunchFileContent
}

logOut("Updating kie server state info...", quietMode);
def kieServerStateContent = kieServerStateFile.getText('UTF-8');

if( kjarContainerId != "" ) {
    logOut("- updating with provided kjar info", quietMode);
    kieServerStateContent = kieServerStateContent.replace(kjarContainerIdMarker, kjarContainerId);

    kieServerStateFile.newWriter().withWriter { w ->
        w << kieServerStateContent
    }
} else {
    logOut("- no kjar info provided, deleting info", quietMode);
    kieServerStateFile.delete();
}

logOut("[Finished running post-generation script]", quietMode);