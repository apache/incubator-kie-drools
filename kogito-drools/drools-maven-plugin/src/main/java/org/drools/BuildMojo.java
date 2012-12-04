package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * This goal builds the drools file belonging to the kproject.
 *
 * @goal build
 *
 * @phase compile
 */
public class BuildMojo extends AbstractMojo {

    private static final String KBASES_FOLDER = "src/kbases";

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
/*
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

        copyKBasesToOutput(rootFolder, outputDirectory);
*/
    }
/*
    public void copyKBasesToOutput(File rootFolder, File outputFolder) {
        File kProjectFile = new File(rootFolder, KnowledgeContainerImpl.KPROJECT_RELATIVE_PATH);
        KieModuleModel kieProject = fromXML(new File(rootFolder, KnowledgeContainerImpl.KPROJECT_RELATIVE_PATH));
        copyFile(kProjectFile, new File(outputFolder, KnowledgeContainerImpl.KPROJECT_JAR_PATH));

        for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
            for (String kBaseFile : getFiles(new File(rootFolder, KBASES_FOLDER))) {
                copyFile(new File(rootFolder, KBASES_FOLDER + "/" + kBaseFile), new File(outputFolder, kBaseFile));
            }
        }
    }
*/
}