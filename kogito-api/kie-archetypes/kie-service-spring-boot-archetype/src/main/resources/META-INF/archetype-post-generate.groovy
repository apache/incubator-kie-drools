def quietMode = false;
if(System.getenv('MAVEN_CMD_LINE_ARGS').contains("-q") || System.getenv('MAVEN_CMD_LINE_ARGS').contains("-quiet")) {
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
def kjarArtifactId = "${kjarArtifactId}";
def kjarVersion = "${kjarVersion}";
def kjarContainerId = "";

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
def kjarContainerIdMarker = "KJAR_CONTAINER_ID_MARKER"

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
    def appPropertiesContentMySql = appPropertiesFileMySql.getText('UTF-8');
    def appPropertiesContentPostgres = appPropertiesFilePostgres.getText('UTF-8');
    def devAppPropertiesContent = devAppPropertiesFile.getText('UTF-8');

    logOut("- adding server capabilities", quietMode);
    appPropertiesContent = appPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesBRM);
    appPropertiesContentMySql = appPropertiesContentMySql.replace(kieServerCapabilitiesMarker, serverCapabilitiesBRM);
    appPropertiesContentPostgres = appPropertiesContentPostgres.replace(kieServerCapabilitiesMarker, serverCapabilitiesBRM);
    devAppPropertiesContent = devAppPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesBRM);

    logOut("- removing jbpm configuration", quietMode);
    appPropertiesContent = appPropertiesContent.replace(jbpmConfigMarker, '');
    appPropertiesContentMySql = appPropertiesContentMySql.replace(jbpmConfigMarker, '');
    appPropertiesContentPostgres = appPropertiesContentPostgres.replace(jbpmConfigMarker, '');
    devAppPropertiesContent = devAppPropertiesContent.replace(jbpmConfigMarker, '');

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
    pomContent = pomContent.replace(springBootStarterMarker, BRMSpringBootStarterDepends);

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
    def appPropertiesContentMySql = appPropertiesFileMySql.getText('UTF-8');
    def appPropertiesContentPostgres = appPropertiesFilePostgres.getText('UTF-8');
    def devAppPropertiesContent = devAppPropertiesFile.getText('UTF-8');

    logOut("- adding server capabilities", quietMode);
    appPropertiesContent = appPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesPlanner);
    appPropertiesContentMySql = appPropertiesContentMySql.replace(kieServerCapabilitiesMarker, serverCapabilitiesPlanner);
    appPropertiesContentPostgres = appPropertiesContentPostgres.replace(kieServerCapabilitiesMarker, serverCapabilitiesPlanner);
    devAppPropertiesContent = devAppPropertiesContent.replace(kieServerCapabilitiesMarker, serverCapabilitiesPlanner);

    logOut("- removing jbpm configuration", quietMode);
    appPropertiesContent = appPropertiesContent.replace(jbpmConfigMarker, '');
    appPropertiesContentMySql = appPropertiesContentMySql.replace(jbpmConfigMarker, '');
    appPropertiesContentPostgres = appPropertiesContentPostgres.replace(jbpmConfigMarker, '');
    devAppPropertiesContent = devAppPropertiesContent.replace(jbpmConfigMarker, '');

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
    pomContent = pomContent.replace(springBootStarterMarker, PlannerSpringBootStarterDepends);

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
devLaunchFileContent = devLaunchFileContent.replaceAll(myServiceNameMarker, myAppArtifactId);

launchFile.newWriter().withWriter { w ->
    w << launchFileContent
}

devLaunchFile.newWriter().withWriter { w ->
    w << devLaunchFileContent
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