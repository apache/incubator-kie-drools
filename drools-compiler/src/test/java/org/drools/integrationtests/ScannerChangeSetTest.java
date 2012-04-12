package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.junit.Test;

public class ScannerChangeSetTest {

    public static final String TMP_DIR = "target/classes/";
    
    @Test
    public void testPKGByResourceChangeScanner() throws Exception {
        
        SystemEventListenerFactory.setSystemEventListener(new PrintStreamSystemEventListener(System.out));        

        // create a PKG file
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "Sample.drl" ) ) );
        Package pkg = builder.getPackage();
        
        byte[] blob1 = DroolsStreamUtils.streamOut( pkg );
        File file = new File( TMP_DIR + "rules.pkg");
        writeBinaryFile( file,
                         blob1 );
        Thread.sleep(1100);
        
        // changeset
        String XLS_CHANGESET = 
            "<change-set xmlns=\"http://drools.org/drools-5.0/change-set\"\n" +
            "            xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "            xs:schemaLocation=\"http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd\">\n" +
            "  <add>\n" +
            "    <resource source=\"file:" + file.getAbsolutePath() + "\" type=\"PKG\" />\n" +
            "  </add>\n" +
            "</change-set>\n";
        File xlsChangeset = new File(TMP_DIR + "pkgChangeset.xml");
        xlsChangeset.deleteOnExit();
        writeToFile(xlsChangeset, XLS_CHANGESET);
        
        // scan every second
        ResourceChangeScannerConfiguration config = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        config.setProperty("drools.resource.scanner.interval", "1");
        ResourceFactory.getResourceChangeScannerService().configure(config);
        
        // create knowledge agent
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("xls agent");
        kagent.applyChangeSet(ResourceFactory.newFileResource(xlsChangeset));
        KnowledgeBase kbase = kagent.getKnowledgeBase();

        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();

        assertEquals(1, kbase.getKnowledgePackages().size());
        assertEquals(2, kbase.getKnowledgePackages().iterator().next()
                .getRules().size());

        // after some waiting we change number of rules in the file
        Rule hw = pkg.getRule( "Hello World" );
        pkg.removeRule( hw );
        byte[] blob2 = DroolsStreamUtils.streamOut( pkg );
        
        // scanner should notice the change
        Thread.sleep(1500);
        writeBinaryFile( file,
                         blob2 );
        Thread.sleep(1500);
        try {
            kbase = kagent.getKnowledgeBase();
            assertEquals(1, kbase.getKnowledgePackages().size());
            assertEquals(1, kbase.getKnowledgePackages().iterator().next()
                    .getRules().size());
        } finally {
            ResourceFactory.getResourceChangeNotifierService().stop();
            ResourceFactory.getResourceChangeScannerService().stop();
            file.delete();
            kagent.dispose();
        }
    }

    private void writeBinaryFile( File file, byte[] blob1 ) throws FileNotFoundException, IOException {
        file.delete();
        FileOutputStream out = new FileOutputStream( file );
        out.write( blob1 );
        out.close();
    }
    
    private static void writeToFile(File file, String content) throws Exception {
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
            fw.write(content);
        } finally {
            if (fw != null) fw.close();
        }
    }
}
