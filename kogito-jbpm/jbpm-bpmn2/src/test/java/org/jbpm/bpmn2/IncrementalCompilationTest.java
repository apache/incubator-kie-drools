package org.jbpm.bpmn2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static junit.framework.Assert.*;

public class IncrementalCompilationTest {

    @Test
    public void testIncrementalProcessCompilation() throws Exception {

        String invalidProcessDefinition = getResource( "/BPMN2-Incremental-Build-Invalid.bpmn2" );
        String validProcessDefinition = getResource( "/BPMN2-Incremental-Build-Valid.bpmn2" );

        KieServices ks = KieServices.Factory.get();

        //This process file contains 4 errors
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/p1.bpmn2",
                                                         invalidProcessDefinition );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();
        assertEquals( 4,
                      results.getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //This process file has the errors fixed
        kfs.write("src/main/resources/p1.bpmn2",
                  validProcessDefinition);
        IncrementalResults addResults = ((InternalKieBuilder)kieBuilder).createFileSet("src/main/resources/p1.bpmn2").build();

        //I'd expect the 4 previous errors to be cleared
        assertEquals( 0,
                      addResults.getAddedMessages().size() );
        assertEquals( 4,
                      addResults.getRemovedMessages().size() );
    }

    private String getResource(String name) throws IOException {
        InputStream is = this.getClass().getResourceAsStream( name );
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();

        } finally {
            br.close();
        }
    }


}
