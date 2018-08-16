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

def caseProject = "${caseProject}";
def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId());

if( caseProject == "true" ) {
    logOut("Updating case project...", quietMode);
    def baseProjectDDFile = new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor.xml");
    boolean baseProjectDDFileDeleted =  baseProjectDDFile.delete();
    if(baseProjectDDFileDeleted) {
        logOut(" - deleting base project deployment descriptor.....success", quietMode);
    } else {
        logOut(" - deleting base project deployment descriptor......fail", quietMode);
    }
    def caseProjectDDFile = new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor-caseproject.xml");
    boolean caseProjectDDFileRenamed =  caseProjectDDFile.renameTo(new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor.xml"));
    if(caseProjectDDFileRenamed) {
        logOut(" - renamed case project deployment descriptor......success", quietMode);
    } else {
        logOut(" - renamed case project deployment descriptor......fail", quietMode);
    }
} else {
    logOut("Updating base project...", quietMode);
    def caseProjectDotFile = new File(moduleDir, ".caseproject");
    boolean caseProjectDotFileDeleted =  caseProjectDotFile.delete();
    if(caseProjectDotFileDeleted) {
        logOut(" - deleting case project dot file......success", quietMode);
    } else {
        logOut(" - deleting case project dot file......fail", quietMode);
    }

    def caseProjectDDFile = new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor-caseproject.xml");
    boolean caseProjectDDFileDeleted =  caseProjectDDFile.delete();
    if(caseProjectDDFileDeleted) {
        logOut(" - deleting case project deployment descriptor......success", quietMode);
    } else {
        logOut(" - deleting case project deployment descriptor......fail", quietMode);
    }

    def caseProjectWidFile = new File(moduleDir, "src/main/resources/WorkDefinition.wid");
    boolean caseProjectWidFileDeleted =  caseProjectWidFile.delete();
    if(caseProjectWidFileDeleted) {
        logOut(" - deleting caseproject wid......success", quietMode);
    } else {
        logOut(" - deleting case project wid......fail", quietMode);
    }
}
