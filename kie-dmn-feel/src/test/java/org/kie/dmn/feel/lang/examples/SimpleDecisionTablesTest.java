package org.kie.dmn.feel.lang.examples;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDecisionTablesTest {
    private static final Logger logger = LoggerFactory.getLogger( ExamplesTest.class );
    public static final String DEFAULT_IDENT = "    ";
    private static FEEL feel;

    @BeforeClass
    public static void setupTest() {
        feel = FEEL.newInstance();
    }
    
    @Test
    public void testMain() {
        String expression = loadExpression( "simple_decision_tables.feel" );

//        String expression2 = "decision table ( \n" + 
//                "                               rule list: [\n" + 
//                "                                   [ <18,       \"Young\"],\n" + 
//                "                                   [ >18,       \"Adult\"]\n" + 
//                "                               ]\n" +
//                "                           )";
        String expression2 = "1 = 1";
        System.out.println(expression2);
        CompiledExpression compile = feel.compile(expression2, feel.newCompilerContext());
        
        System.out.println(compile);
        
        System.out.println("---");
        
        Map context = (Map) feel.evaluate( expression );

        System.out.println( printContext( context ) );
    }
    
    private static String loadExpression(String fileName) {
        try {
            return new String( Files.readAllBytes( Paths.get( ExamplesTest.class.getResource( fileName ).toURI() ) ) );
        } catch ( Exception e ) {
            logger.error( "Error reading file " + fileName, e );
            Assert.fail("Error reading file "+fileName);
        }
        return null;
    }

    private String printContext( Map context ) {
        return printContext( context, "" );
    }

    private String printContext( Map<String, Object> context, String ident ) {
        StringBuilder builder = new StringBuilder(  );
        builder.append( "{\n" );
        for( Map.Entry e : context.entrySet() ) {
            builder.append( ident )
                    .append( DEFAULT_IDENT )
                    .append( e.getKey() )
                    .append( ": " );
            if( e.getValue() instanceof Map ) {
                builder.append( printContext( (Map<String, Object>) e.getValue(), ident + DEFAULT_IDENT ) );
            } else {
                builder.append( e.getValue() )
                        .append( "\n" );
            }
        }
        builder.append( ident+"}\n" );
        return builder.toString();
    }
}
