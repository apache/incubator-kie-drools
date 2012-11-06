package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeJarBuilder;
import org.drools.builder.impl.KnowledgeJarBuilderImpl;

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
     * Project root folder.
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
        KnowledgeJarBuilderImpl kbuilder = (KnowledgeJarBuilderImpl)KnowledgeBuilderFactory.newKnowledgeJarBuilder(kConf);

        for (KBaseUnit kBaseUnit : kbuilder.getKBaseUnits(rootFolder, sourceFolder)) {
            if (kBaseUnit.hasErrors()) {
                getLog().error(kBaseUnit.getErrors().toString());
                throw new MojoFailureException(kBaseUnit.getKBaseName() + " build failed!");
            }

            getLog().info(kBaseUnit.getKBaseName() + " correctly built!");
        }

        kbuilder.copyKBasesToOutput(rootFolder, outputDirectory);
    }
}