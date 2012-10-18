package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeJarBuilder;
import org.drools.builder.impl.KnowledgeJarBuilderImpl;

import java.io.File;

/**
 * This goal builds the drools file belonging to the kproject.
 *
 * @goal build
 *
 * @phase process-sources
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        KnowledgeJarBuilderImpl kbuilder = (KnowledgeJarBuilderImpl)KnowledgeBuilderFactory.newKnowledgeJarBuilder();

        for (KBaseUnit kBaseUnit : kbuilder.getKBaseUnits(rootFolder, sourceFolder)) {
            if (kBaseUnit.hasErrors()) {
                getLog().error(kBaseUnit.getErrors().toString());
                throw new MojoFailureException(kBaseUnit.getKBaseName() + " build failed!");
            }

            getLog().info(kBaseUnit.getKBaseName() + " correctly built!");
        }

    }
}