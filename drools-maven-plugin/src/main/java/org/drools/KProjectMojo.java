package org.drools;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.drools.builder.impl.KnowledgeContainerImpl;
import org.drools.kproject.KBase;
import org.drools.kproject.KProjectImpl;
import org.drools.kproject.KSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.drools.core.util.IoUtils.recursiveListFile;

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

        KProjectImpl kproj = new KProjectImpl();

        for (File kBaseFolder : sourceFolder.listFiles()) {
            String qName = kBaseFolder.getName();
            int dotPos = qName.lastIndexOf('.');
            String namespace = dotPos > 0 ? qName.substring(0, dotPos) : "";
            String kBaseName = dotPos > 0 ? qName.substring(dotPos+1) : qName;

            KBase kBase = kproj.newKBase( namespace, kBaseName )
                    .setFiles(recursiveListFile(kBaseFolder));

            KSession ksession1 = kBase.newKSession( namespace, kBaseName + ".session" )
                    .setType( "stateful" );
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
