package org.drools.modelcompiler.benchmark;

import java.io.File;
import java.util.Collection;

public class KJarWithKnowledgeFiles {
    private final File jarFile;
    private final Collection<String> knowledgeFiles;

    public KJarWithKnowledgeFiles( final File jarFile, final Collection<String> knowledgeFiles ) {
        this.jarFile = jarFile;
        this.knowledgeFiles = knowledgeFiles;
    }

    public File getJarFile() {
        return jarFile;
    }

    public Collection<String> getKnowledgeFiles() {
        return knowledgeFiles;
    }
}
