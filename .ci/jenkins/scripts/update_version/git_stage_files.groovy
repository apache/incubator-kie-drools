void execute(def pipelinesCommon) {
    githubscm.findAndStageNotIgnoredFiles('pom.xml') 
}

return this
