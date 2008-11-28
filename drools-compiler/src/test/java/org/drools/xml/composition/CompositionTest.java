package org.drools.xml.composition;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.compiler.KnowledgeComposition;
import org.drools.compiler.KnowledgeResource;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.xml.XmlCompositionReader;
import org.xml.sax.SAXException;

public class CompositionTest extends TestCase {
    public void testXmlParser() throws SAXException,
                               IOException {

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        XmlCompositionReader xmlReader = new XmlCompositionReader( conf.getSemanticModules() );

        String str = "";
        str += "<composition ";
        str += "xmlns='http://drools.org/drools-4.0/composition' ";
        str += "xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' ";
        str += "xs:schemaLocation='http://drools.org/drools-4.0/composition drools-composition-4.0.xsd' >";
        str += "    <resource source='http://www.domain.com/test.drl' type='DRL' />";
        str += "    <resource source='http://www.domain.com/test.xls' type='DTABLE' >";
        str += "        <decisiontable-conf worksheet-name='sheet10' input-type='XLS' />";
        str += "    </resource>";
        str += "</composition>";

        StringReader reader = new StringReader( str );
        KnowledgeComposition composition = xmlReader.read( reader );

        assertEquals( 2,
                      composition.getResources().size() );
        KnowledgeResource resource = composition.getResources().get( 0 );
        assertNull( resource.getConfiguration() );
        assertEquals( "http://www.domain.com/test.drl",
                      resource.getSource() );
        assertEquals( KnowledgeType.DRL,
                      resource.getType() );

        resource = composition.getResources().get( 1 );
        assertEquals( "http://www.domain.com/test.xls",
                      resource.getSource() );
        assertEquals( KnowledgeType.DTABLE,
                      resource.getType() );
        DecisionTableConfiguration dtConf = (DecisionTableConfiguration) resource.getConfiguration();
        assertEquals( DecisionTableInputType.XLS,
                      dtConf.getInputType() );
    }
    
    public void testIntegregation() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "composition1Test.xml", getClass()), KnowledgeType.COMPOSITION );
        assertFalse( kbuilder.hasErrors() );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();
        
        assertEquals( 2, list.size() );
        assertTrue ( list.containsAll( Arrays.asList(  new String[] { "rule1", "rule2" } ) ) );
        
    }
}
