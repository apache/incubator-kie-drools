package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.builder.impl.KnowledgeContainerImpl;
import org.drools.kproject.KieProjectImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This goal builds the drools file belonging to the kproject.
 *
 * @goal kproject
 *
 * @phase process-resources
 */
public class KProjectMojo extends AbstractMojo {

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
     * If it true recreates the kproject.xml file even if it already exists
     *
     * @parameter default-value=false
     * @required
     */
    private boolean recreate;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File file = new File( rootFolder, KnowledgeContainerImpl.KPROJECT_RELATIVE_PATH );
        if (!recreate && file.exists()) {
            return;
        }

        KieProjectImpl kproj = new KieProjectImpl();

        for (File kBaseFolder : sourceFolder.listFiles()) {
            String kBaseName = kBaseFolder.getName();
            kproj.newKieBaseDescr(kBaseName)
                    .newKieSessionDescr(kBaseName + ".session")
                    .setType("stateful");
        }


        BufferedWriter output = null;
        try {
            file.getParentFile().mkdirs();
            output = new BufferedWriter( new FileWriter( file ) );
            output.write( kproj.toXML() );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
