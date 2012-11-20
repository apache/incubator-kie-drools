package org.drools.builder.impl;

import org.drools.kproject.KBase;
import org.drools.kproject.KBaseImpl;
import org.drools.kproject.KSession;
import org.kie.KBaseUnit;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderErrors;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.kproject.KBaseImpl.getFiles;

public class KBaseUnitImpl implements KBaseUnit {

    private static final Logger log = LoggerFactory.getLogger(KBaseUnit.class);

    private final String url;

    private final KBase kBase;
    private final ClassLoader classLoader;

    private KnowledgeBuilder kbuilder;
    private KnowledgeBase knowledgeBase;

    private List<KBase> includes = null;

    public KBaseUnitImpl(String url, KBase kBase) {
        this(url, kBase, null);
    }

    public KBaseUnitImpl(String url, KBase kBase, ClassLoader classLoader) {
        this.url = url;
        this.kBase = kBase;
        this.classLoader = classLoader;
    }

    public KnowledgeBase getKnowledgeBase() {
        if (knowledgeBase != null) {
            return knowledgeBase;
        }
        KnowledgeBuilder kbuilder = getKBuilder();
        if (kbuilder.hasErrors()) {
            return null;
        }

        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration() );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

    void addInclude(KBase kBase) {
        if (includes == null) {
            includes = new ArrayList<KBase>();
        }
        includes.add(kBase);
    }

    boolean hasIncludes() {
        return includes != null;
    }

    public String getKBaseName() {
        return kBase.getQName();
    }

    public boolean hasErrors() {
        return getKBuilder().hasErrors();
    }

    public KnowledgeBuilderErrors getErrors() {
        return getKBuilder().getErrors();
    }

    public StatefulKnowledgeSession newStatefulKnowledegSession(String ksessionName) {
        return getKnowledgeBase().newStatefulKnowledgeSession(getKnowledgeSessionConfiguration(ksessionName), null);
    }

    public StatelessKnowledgeSession newStatelessKnowledegSession(String ksessionName) {
        return getKnowledgeBase().newStatelessKnowledgeSession(getKnowledgeSessionConfiguration(ksessionName));
    }

    private KnowledgeBuilder getKBuilder() {
        if (kbuilder != null) {
            return kbuilder;
        }

        if (classLoader != null) {
            KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kConf);
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        }
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();
        buildKBaseFiles(ckbuilder, kBase);
        if (includes != null) {
            for (KBase include : includes) {
                buildKBaseFiles(ckbuilder, include);
            }
        }
        ckbuilder.build();
        return kbuilder;
    }

    private void buildKBaseFiles(CompositeKnowledgeBuilder ckbuilder, KBase kBase) {
        String rootPath = url;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = url.substring( rootPath.lastIndexOf( ':' ) + 1 );
        }

        if ( url.endsWith( ".jar" ) ) {
            File actualZipFile = new File( rootPath );
            if ( !actualZipFile.exists() ) {
                log.error( "Unable to build KBase:" + kBase.getName() + " as jarPath cannot be found\n" + rootPath );
            }

            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile( actualZipFile );
            } catch ( Exception e ) {
                log.error( "Unable to build KBase:" + kBase.getName() + " as jar cannot be opened\n" + e.getMessage() );
            }

            try {
                for ( String file : getFiles(kBase.getQName(), zipFile) ) {
                    ZipEntry zipEntry = zipFile.getEntry( file );
                    ckbuilder.add( ResourceFactory.newInputStreamResource( zipFile.getInputStream( zipEntry ) ), ResourceType.DRL );
                }
            } catch ( Exception e ) {
                try {
                    zipFile.close();
                } catch ( IOException e1 ) {

                }
                log.error( "Unable to build KBase:" + kBase.getName() + " as jar cannot be read\n" + e.getMessage() );
            }
        } else {
            try {
                for ( String file : getFiles(kBase.getQName(), new File(rootPath)) ) {
                    ckbuilder.add( ResourceFactory.newFileResource( new File(rootPath, file) ), ResourceType.DRL );
                }
            } catch ( Exception e) {
                log.error( "Unable to build KBase:" + kBase.getName() + "\n" + e.getMessage() );
            }
        }
    }

    private KSession getKSession(String ksessionName) {
        KSession kSession = kBase.getKSessions().get(ksessionName);
        if (kSession == null) {
            throw new RuntimeException("Unknown Knowledge Session: " + ksessionName + " in Knowledge Base: " + getKBaseName());
        }
        return kSession;
    }

    private KnowledgeBaseConfiguration getKnowledgeBaseConfiguration() {
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbConf.setOption(kBase.getEqualsBehavior());
        kbConf.setOption(kBase.getEventProcessingMode());
        return kbConf;
    }

    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(String ksessionName) {
        KSession kSession = getKSession(ksessionName);
        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption(kSession.getClockType());
        return ksConf;
    }
}