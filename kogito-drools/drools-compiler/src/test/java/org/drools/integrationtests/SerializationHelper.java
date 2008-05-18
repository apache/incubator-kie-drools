package org.drools.integrationtests;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.marshalling.DefaultMarshaller;
import org.drools.marshalling.Marshaller;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.util.DroolsStreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {
    public static <T> T serializeObject(T obj) throws IOException,
                                              ClassNotFoundException {
        return serializeObject( obj,
                                null );
    }

    public static <T> T serializeObject(T obj,
                                        ClassLoader classLoader) throws IOException,
                                                                ClassNotFoundException {
        return (T) DroolsStreamUtils.streamIn( DroolsStreamUtils.streamOut( obj ),
                                               classLoader );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session) throws Exception {
        return getSerialisedStatefulSession( session,
                                             true );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               RuleBase ruleBase) throws Exception {
        return getSerialisedStatefulSession( session,
                                             ruleBase,
                                             true );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               boolean dispose) throws Exception {
        return getSerialisedStatefulSession( session,
                                             session.getRuleBase(),
                                             dispose );
    }

    public static StatefulSession getSerialisedStatefulSession(StatefulSession session,
                                                               RuleBase ruleBase,
                                                               boolean dispose) throws Exception {
        Marshaller marshaller = new DefaultMarshaller();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ruleBase.writeStatefulSession( session,
                                       baos,
                                       marshaller );

        byte[] b1 = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );
        StatefulSession session2 = ruleBase.readStatefulSession( bais,
                                                                true,
                                                                marshaller );

        // write methods allways needs a new marshaller for Identity strategies
        marshaller = new DefaultMarshaller();        
        baos = new ByteArrayOutputStream();
        ruleBase.writeStatefulSession( session2,
                                       baos,
                                       marshaller );

        byte[] b2 = baos.toByteArray();
        // bytes should be the same.
        if ( !areByteArraysEqual( b1,
                                  b2 ) ) {
            assert false : "byte streams for serialisation test are not equal";
        }

        session2.setGlobalResolver( session.getGlobalResolver() );

        if ( dispose ) {
            session.dispose();
        }

        return session2;
    }

    public static boolean areByteArraysEqual(byte[] b1,
                                             byte[] b2) {
        if ( b1.length != b2.length ) {
            return false;
        }

        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                return false;
            }
        }

        return true;
    }
}
