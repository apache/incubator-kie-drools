package org.kie.dmn.feel.lang.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

public abstract class ExamplesBaseTest {
    public static final  String DEFAULT_IDENT = "    ";

    private static final Logger logger = LoggerFactory.getLogger( ExamplesBaseTest.class );

    protected static String loadExpression(String fileName) {
        try {
            return new String( Files.readAllBytes( Paths.get( ExamplesTest.class.getResource( fileName ).toURI() ) ) );
        } catch ( Exception e ) {
            logger.error( "Error reading file " + fileName, e );
            fail( "Error reading file " + fileName);
        }
        return null;
    }

    protected String printContext(Map context) {
        return printContext( context, "" );
    }

    private String printContext(Map<String, Object> context, String ident ) {
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
