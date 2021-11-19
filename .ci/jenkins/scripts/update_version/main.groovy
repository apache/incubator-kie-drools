void execute(def pipelinesCommon) {
    maven.mvnVersionsSet(pipelinesCommon.getDefaultMavenCommand(), pipelinesCommon.getDroolsVersion(), !pipelinesCommon.isRelease())
}

return this
