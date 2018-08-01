println "[Running post-generation script]";

def caseProject = "${caseProject}";
def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId());

if( caseProject == "true" ) {
    println "Updating case project...";
    def baseProjectDDFile = new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor.xml");
    boolean baseProjectDDFileDeleted =  baseProjectDDFile.delete();
    if(baseProjectDDFileDeleted) {
        println " - deleting base project deployment descriptor.....success";
    } else {
        println " - deleting base project deployment descriptor......fail";
    }
    def caseProjectDDFile = new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor-caseproject.xml");
    boolean caseProjectDDFileRenamed =  caseProjectDDFile.renameTo(new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor.xml"));
    if(caseProjectDDFileRenamed) {
        println " - renamed case project deployment descriptor......success";
    } else {
        println " - renamed case project deployment descriptor......fail";
    }
} else {
    println "Updating base project...";
    def caseProjectDotFile = new File(moduleDir, ".caseproject");
    boolean caseProjectDotFileDeleted =  caseProjectDotFile.delete();
    if(caseProjectDotFileDeleted) {
        println " - deleting case project dot file......success";
    } else {
        println " - deleting case project dot file......fail";
    }

    def caseProjectDDFile = new File(moduleDir, "src/main/resources/META-INF/kie-deployment-descriptor-caseproject.xml");
    boolean caseProjectDDFileDeleted =  caseProjectDDFile.delete();
    if(caseProjectDDFileDeleted) {
        println " - deleting case project deployment descriptor......success";
    } else {
        println " - deleting case project deployment descriptor......fail";
    }

    def caseProjectWidFile = new File(moduleDir, "src/main/resources/WorkDefinition.wid");
    boolean caseProjectWidFileDeleted =  caseProjectWidFile.delete();
    if(caseProjectWidFileDeleted) {
        println " - deleting caseproject wid......success";
    } else {
        println " - deleting case project wid......fail";
    }
}
