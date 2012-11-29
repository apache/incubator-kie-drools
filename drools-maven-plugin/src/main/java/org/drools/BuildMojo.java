package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.kie.KBaseUnit;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.drools.builder.impl.KnowledgeContainerImpl;
import org.kie.builder.KnowledgeContainerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This goal builds the drools file belonging to the kproject.
 *
 * @goal build
 *
 * @phase compile
 */
public class BuildMojo extends AbstractMojo {

    /**
     * Project root folder.
     *
     * @parameter default-value="."
     * @required
     */
    private File rootFolder;

    /**
     * Project sourceFolder folder.
     *
     * @parameter default-value="src/kbases"
     * @required
     */
    private File sourceFolder;

    /**
     * Directory containing the generated JAR.
     *
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        URLClassLoader projectClassLoader = null;
        try {
            projectClassLoader = new URLClassLoader( new URL[] { outputDirectory.toURI().toURL() } );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, projectClassLoader);
        KnowledgeContainerImpl kbuilder = (KnowledgeContainerImpl) KnowledgeContainerFactory.newKnowledgeContainer(kConf);

        for (KBaseUnit kBaseUnit : kbuilder.getKBaseUnits(rootFolder, sourceFolder)) {
            if (kBaseUnit.hasErrors()) {
                throw new MojoFailureException(kBaseUnit.getKBaseName() + " build failed!");
            }

            getLog().info(kBaseUnit.getKBaseName() + " correctly built!");
        }

        kbuilder.copyKBasesToOutput(rootFolder, outputDirectory);
    }
}