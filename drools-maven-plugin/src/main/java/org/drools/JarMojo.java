package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeJarBuilder;

import java.io.File;

/**
 * This goal creates a knowledge jar.
 *
 * @goal jar
 * 
 * @phase process-sources
 */
public class JarMojo extends AbstractMojo {

    /**
     * Project root folder.
     *
     * @parameter default-value="."
     * @required
     */
    private File rootFolder;

    /**
     * Directory containing the generated JAR.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Name of the generated JAR.
     *
     * @parameter default-value="kproject.jar"
     * @required
     */
    private String jarName;

    public void execute() throws MojoExecutionException {
        KnowledgeJarBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeJarBuilder();
        File kJar = kbuilder.buildKJar(rootFolder, outputDirectory, jarName);
        getLog().info("Knowledge jar file written in " + kJar.getAbsolutePath());
    }
}
