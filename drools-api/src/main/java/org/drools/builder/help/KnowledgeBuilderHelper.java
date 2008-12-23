package org.drools.builder.help;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.drools.KnowledgeBase;
import org.drools.ProviderInitializationException;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.io.Resource;

import com.sun.tools.xjc.Options;

public class KnowledgeBuilderHelper {
    
    private static DroolsJaxbHelperProvider provider;
    
    public static String[] addXsdModel(Resource resource,
                                       KnowledgeBuilder kbuilder,
                                       Options xjcOpts,
                                       String systemId) throws IOException {
         return getDroolsJaxbHelperProvider().addXsdModel( resource, kbuilder, xjcOpts, systemId );
    }
    
    public static JAXBContext newJAXBContext(String[] classNames,
                                          KnowledgeBase kbase) throws JAXBException {
        return newJAXBContext( classNames,
                            Collections.<String, Object> emptyMap(),
                            kbase );
    }

    public static JAXBContext newJAXBContext(String[] classNames,
                                          Map<String, ? > properties,
                                          KnowledgeBase kbase) throws JAXBException {  
        return getDroolsJaxbHelperProvider().newJAXBContext( classNames, properties, kbase );
    }
    
    public static synchronized DroolsJaxbHelperProvider getDroolsJaxbHelperProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }
    
    private static synchronized void setDroolsJaxbHelperProvider(DroolsJaxbHelperProvider provider){
        KnowledgeBuilderHelper.provider = provider;
    }
    
    private static void loadProvider() {
        try {
            Class<DroolsJaxbHelperProvider> cls = (Class<DroolsJaxbHelperProvider>) Class.forName( "org.drools.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl" );
            setDroolsJaxbHelperProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl could not be set.",
                                                       e2 );
        }
    }
    
}
