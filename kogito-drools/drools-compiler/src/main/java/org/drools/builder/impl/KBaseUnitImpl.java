package org.drools.builder.impl;

import org.kie.builder.KieBaseDescr;
import org.kie.builder.KieSessionDescr;
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.kproject.KieBaseDescrImpl.getFiles;
import static org.drools.builder.impl.KnowledgeContainerImpl.KPROJECT_JAR_PATH;

public class KBaseUnitImpl implements KBaseUnit {

    private static final Logger log = LoggerFactory.getLogger(KBaseUnit.class);

    private final String url;

    private final KieBaseDescr kieBaseDescr;
    private final ClassLoader classLoader;

    private KnowledgeBuilder kbuilder;
    private KnowledgeBase knowledgeBase;

    private List<KieBaseDescr> includes = null;

    public KBaseUnitImpl(URL url, KieBaseDescr kieBaseDescr) {
        this(fixURL(url), kieBaseDescr, new URLClassLoader( new URL[] { url }));
    }

    public KBaseUnitImpl(String url, KieBaseDescr kieBaseDescr, ClassLoader classLoader) {
        this.url = url;
        this.kieBaseDescr = kieBaseDescr;
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

        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration(null, classLoader) );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

    void addInclude(KieBaseDescr kieBaseDescr) {
        if (includes == null) {
            includes = new ArrayList<KieBaseDescr>();
        }
        includes.add(kieBaseDescr);
    }

    boolean hasIncludes() {
        return includes != null;
    }

    public String getKBaseName() {
        return kieBaseDescr.getName();
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

        KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kConf);
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();
        buildKBaseFiles(ckbuilder, kieBaseDescr);
        if (includes != null) {
            for (KieBaseDescr include : includes) {
                buildKBaseFiles(ckbuilder, include);
            }
        }
        ckbuilder.build();
        return kbuilder;
    }

    private void buildKBaseFiles(CompositeKnowledgeBuilder ckbuilder, KieBaseDescr kieBaseDescr) {
        String rootPath = url;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = url.substring( rootPath.lastIndexOf( ':' ) + 1 );
        }

        if ( url.endsWith( ".jar" ) ) {
            File actualZipFile = new File( rootPath );
            if ( !actualZipFile.exists() ) {
                log.error( "Unable to build KieBaseDescr:" + kieBaseDescr.getName() + " as jarPath cannot be found\n" + rootPath );
            }

            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile( actualZipFile );
            } catch ( Exception e ) {
                log.error( "Unable to build KieBaseDescr:" + kieBaseDescr.getName() + " as jar cannot be opened\n" + e.getMessage() );
            }

            try {
                for ( String file : getFiles(kieBaseDescr.getName(), zipFile) ) {
                    ZipEntry zipEntry = zipFile.getEntry( file );
                    ckbuilder.add( ResourceFactory.newInputStreamResource( zipFile.getInputStream( zipEntry ) ), ResourceType.determineResourceType( file ) );
                }
            } catch ( Exception e ) {
                try {
                    zipFile.close();
                } catch ( IOException e1 ) {

                }
                log.error( "Unable to build KieBaseDescr:" + kieBaseDescr.getName() + " as jar cannot be read\n" + e.getMessage() );
            }
        } else {
            try {
                for ( String file : getFiles(kieBaseDescr.getName(), new File(rootPath)) ) {
                    ckbuilder.add( ResourceFactory.newFileResource( new File(rootPath, file) ),ResourceType.determineResourceType( file ) );
                }
            } catch ( Exception e) {
                log.error( "Unable to build KieBaseDescr:" + kieBaseDescr.getName() + "\n" + e.getMessage() );
            }
        }
    }

    private KieSessionDescr getKSession(String ksessionName) {
        KieSessionDescr kieSessionDescr = kieBaseDescr.getKieSessionDescrs().get(ksessionName);
        if (kieSessionDescr == null) {
            throw new RuntimeException("Unknown Knowledge Session: " + ksessionName + " in Knowledge Base: " + getKBaseName());
        }
        return kieSessionDescr;
    }

    private KnowledgeBaseConfiguration getKnowledgeBaseConfiguration(Properties properties, ClassLoader... classLoaders) {
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(properties, classLoader);
        kbConf.setOption(kieBaseDescr.getEqualsBehavior());
        kbConf.setOption(kieBaseDescr.getEventProcessingMode());
        return kbConf;
    }

    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(String ksessionName) {
        KieSessionDescr kieSessionDescr = getKSession(ksessionName);
        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption(kieSessionDescr.getClockType());
        return ksConf;
    }

    private static String fixURL(URL url) {
        String urlPath = url.toExternalForm();

        // determine resource type (eg: jar, file, bundle)
        String urlType = "file";
        int colonIndex = urlPath.indexOf( ":" );
        if ( colonIndex != -1 ) {
            urlType = urlPath.substring( 0, colonIndex );
        }

        urlPath = url.getPath();


        if ( "jar".equals( urlType ) ) {
            // switch to using getPath() instead of toExternalForm()

            if ( urlPath.indexOf( '!' ) > 0 ) {
                urlPath = urlPath.substring( 0, urlPath.indexOf( '!' ) );
            }
        } else if (urlPath.endsWith(KPROJECT_JAR_PATH)) {
            urlPath = urlPath.substring( 0, urlPath.length() - KPROJECT_JAR_PATH.length() - 1 );
        }


        // remove any remaining protocols, normally only if it was a jar
        colonIndex = urlPath.lastIndexOf( ":" );
        if ( colonIndex >= 0 ) {
            urlPath = urlPath.substring( colonIndex +  1  );
        }

        try {
            urlPath = URLDecoder.decode(urlPath, "UTF-8");
        } catch ( UnsupportedEncodingException e ) {
            throw new IllegalArgumentException( "Error decoding URL (" + url + ") using UTF-8", e );
        }

        log.debug( "KieProject URL Type + URL: " + urlType + ":" + urlPath );

        return urlPath;
    }
}
