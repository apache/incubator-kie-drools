package org.drools.pmml_4_0.global;


import org.drools.definition.KnowledgePackage;
import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.pmml_4_0.PMML4Compiler;
import org.drools.pmml_4_0.PMML4Wrapper;
import org.drools.pmml_4_0.descr.PMML;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;


/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 11/12/10
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestHeader extends DroolsAbstractPMMLTest {





    @Test
    public void testPMMLHeader() {
        String source = "test_header.xml";


		PMML4Wrapper wrapper = new PMML4Wrapper();
			wrapper.setPack("org.drools.pmml_4_0.test");

        boolean header = false;
        boolean timestamp = false;
        boolean appl = false;
        boolean descr = false;
        boolean copyright = false;
        boolean annotation = false;

		String theory = new PMML4Compiler().compile(source);
        BufferedReader reader = new BufferedReader(new StringReader(theory));
        try {
            String line = "";
            while ((line=reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("// Imported PMML Model Theory")) header = true;
                else if (line.startsWith("// Creation timestamp :")) timestamp = line.contains("now");
                else if (line.startsWith("// Description :")) descr = line.contains("test");
                else if (line.startsWith("// Copyright :")) copyright = line.contains("opensource");
                else if (line.startsWith("// Annotation :")) annotation = line.contains("notes here");
                else if (line.startsWith("// Trained with :")) appl = line.contains("handmade");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail();
        }
        assertTrue(header);
        assertTrue(timestamp);
        assertTrue(descr);
        assertTrue(copyright);
        assertTrue(annotation);
        assertTrue(appl);



        StatefulKnowledgeSession ksession = getSession(theory);
        KnowledgePackage pack = ksession.getKnowledgeBase().getKnowledgePackage("org.drools.pmml_4_0.test");
        assertNotNull(pack);

    }



}