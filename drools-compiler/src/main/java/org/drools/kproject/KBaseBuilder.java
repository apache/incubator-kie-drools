package org.drools.kproject;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import org.drools.core.util.StringUtils;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.io.ResourceFactory;


public class KBaseBuilder {
    private EventProcessingOption eventProcessingMode;

    private AssertBehaviorOption equalsBehaviour;
    protected KBaseBuilder() {

    }

    public static KBaseBuilder fluent() {
        return new KBaseBuilder();
    }

    public  KBaseBuilder setEqualsBehavior(AssertBehaviorOption equalsBehaviour) {
        this.equalsBehaviour = equalsBehaviour;
        return this;
    }

    public  KBaseBuilder setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        this.eventProcessingMode = eventProcessingMode;
        return this;
    }

    public KnowledgeBase build(Class[] kBaseQualifiers) {
        Map<Class, List<String>> map = new HashMap<Class, List<String>>();

        if ( kBaseQualifiers != null && kBaseQualifiers.length > 0 ) {
            for ( Class kBaseQualifier : kBaseQualifiers ) {
                List<String> list = new ArrayList<String>();
                buildResourcesList(kBaseQualifier, kBaseQualifier.getName(), list);
                map.put(kBaseQualifier, list);
            }
        }

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        for ( Map.Entry<Class, List<String>> entry : map.entrySet() ) {
            Class cls = entry.getKey();
            List<String> files = entry.getValue();
            if ( !files.isEmpty() ) {
                for ( String file : files ) {
                    if ( file.endsWith(".drl" ) ) {
                        ckbuilder.add( ResourceFactory.newUrlResource( cls.getResource("/" + file.trim()) ), ResourceType.DRL );
                    }
                }
            }
        }

        ckbuilder.build();


        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "Unable to compile " + kBaseQualifiers[0].getName() + ":\n" + kbuilder.getErrors() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( eventProcessingMode);
        kconf.setOption( equalsBehaviour );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    public static void  buildResourcesList(Class cls, String kBaseQName, List<String> list) {
        String fileStr = null;
        InputStream is = null;
        try {
            is = cls.getResourceAsStream( "/" + kBaseQName + ".files.dat" );
            fileStr = org.drools.core.util.StringUtils.toString( is );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to fine files for KnowledgeBase " + kBaseQName );
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException( "Unable to fine files for KnowledgeBase " + kBaseQName );
                }
            }
        }

        if (!StringUtils.isEmpty(fileStr))  {
            for( String entry : fileStr.split( "," ) ) {
                if (!StringUtils.isEmpty(entry))  {
                    list.add( entry.trim() );
                }
            }
        }
    }

}
