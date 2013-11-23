package org.drools.pmml.pmml_4_1;

import junit.framework.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.io.ResourceFactory;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class PMMLUsageDemoTest extends DroolsAbstractPMMLTest {


    private static final String pmmlSource = "org/drools/pmml/pmml_4_1/mock_cold_simple.xml";


    @Test
    public void invokePmmlWithRawData() {

        KieSession kSession = getModelSession( pmmlSource, false );
        // One entry-point per input field
        //      field name "xyz" => entry point name "in_Xyz"
        kSession.getEntryPoint( "in_Temp" ).insert( 22.0 );
        kSession.fireAllRules();

        // Query results
        //      output field name   --> query name
        //      model name          --> first arg
        //      value               --> second arg ( Variable.v for output, any value for testing )
        QueryResults qrs = kSession.getQueryResults( "Cold", "MockCold", Variable.v );
        assertTrue( qrs.iterator().hasNext() );
        Object val = qrs.iterator().next().get( "$result" );

        assertEquals( 0.56, val );

        QueryResults qrs2 = kSession.getQueryResults( "Cold", "MockCold", 0.56 );
        assertTrue( qrs2.iterator().hasNext() );

        QueryResults qrs3 = kSession.getQueryResults( "Cold", "MockCold", 0.99 );
        assertFalse( qrs3.iterator().hasNext() );

    }



    @Test
    public void invokePmmlWithTrait() {

        String extraDrl = "package org.drools.pmml.pmml_4_1.test;" +
                          "" +
                          "import org.drools.core.factmodel.traits.Entity;" +
                          "" +
                          "rule \"Init\" " +
                          "when " +
                          "   $s : String( this == \"trigger\" ) " +
                          "then " +
                          "   System.out.println( \"Trig\" ); " +
                          "   Entity o = new Entity(); " +
                          "   insert( o ); \n" +
                          "" +
                          // don an object with the default input trait ( modelName + "Input" )
                          // both soft and hard fields will be used to feed data into the model
                          "" +
                          "   MockColdInput input = don( o, MockColdInput.class ); " +
                          "   modify( input ) { " +
                          "       setTemp( 22.0 );" +
                          "   } " +
                          "end " +
                          "" +
                          "" +
                          "rule Log when $x : MockColdInput() then System.out.println( \"IN \" + $x ); end " +
                          "rule Log2 when $x : Cold() then System.out.println( \"OUT \" + $x ); end "
                ;

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/" + pmmlSource.replace( ".xml", ".pmml" ), ResourceFactory.newClassPathResource( pmmlSource ).setResourceType( ResourceType.PMML ) );
        kfs.write( "src/main/resources/" + "extra.drl", ResourceFactory.newByteArrayResource( extraDrl.getBytes() ).setResourceType( ResourceType.DRL ) );

        ks.newKieBuilder( kfs ).buildAll();

        KieSession kSession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        kSession.insert( "trigger" );
        kSession.fireAllRules();

        QueryResults qrs = kSession.getQueryResults( "Cold", "MockCold", Variable.v );
        assertTrue( qrs.iterator().hasNext() );
        Object val = qrs.iterator().next().get( "$result" );
        Assert.assertEquals( 0.56, val );
    }

}
