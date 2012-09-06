package org.drools.kproject;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;


public class KBaseBuilder {
    private String kBaseQName;

    private EventProcessingOption eventProcessingMode;

    private AssertBehaviorOption equalsBehaviour;

    protected KBaseBuilder() {

    }

    public static KBaseBuilder fluent() {
        return new KBaseBuilder();
    }

    public KBaseBuilder setKBaseQName(String kBaseQName) {
        this.kBaseQName = kBaseQName;
        return this;
    }

    public  KBaseBuilder setEqualsBehavior(AssertBehaviorOption equalsBehaviour) {
        this.equalsBehaviour = equalsBehaviour;
        return this;
    }

    public  KBaseBuilder setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        this.eventProcessingMode = eventProcessingMode;
        return this;
    }


    public KnowledgeBase build(Class cls) {
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        String[] files = fileStr.split( "," );
        if ( files.length > 0 ) {
            for ( String file : files ) {
                if ( file.endsWith(".drl" ) ) {
                    ckbuilder.add( ResourceFactory.newUrlResource( cls.getResource( "/" + file.trim() ) ), ResourceType.DRL );
                }
            }
        }
        ckbuilder.build();


        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "Unable to compile " + kBaseQName + ":\n" + kbuilder.getErrors() );
        }

        KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( eventProcessingMode);
        kconf.setOption( equalsBehaviour );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }
}
