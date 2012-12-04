package org.kie.builder.impl;

import org.drools.cdi.KProjectExtension;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.xml.MinimalPomParser;
import org.drools.xml.PomModel;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.util.ClassLoaderUtil;
import org.kie.util.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
 *
 */
public class KieModuleKieProject
    implements
    KieProject {

    private static final Logger                  log               = LoggerFactory.getLogger( KProjectExtension.class );

    private Map<GAV, InternalKieModule>          kieModules;

    private final Map<String, InternalKieModule> kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private final Map<String, KieBaseModel>      kBaseModels       = new HashMap<String, KieBaseModel>();

    private final Map<String, KieSessionModel>   kSessionModels    = new HashMap<String, KieSessionModel>();

    private InternalKieModule                    kieModule;
    private KieRepository                        kr;
    private CompositeClassLoader                 cl;

    public KieModuleKieProject(InternalKieModule kieModule,
                               KieRepository kr) {
        this.kieModule = kieModule;
        this.kr = kr;
        this.cl = ClassLoaderUtil.getClassLoader( null, null, true );
    }

    public void init() {
        if ( kieModules == null ) {
            kieModules = new HashMap<GAV, InternalKieModule>();
            kieModules.putAll( kieModule.getDependencies() );
            kieModules.put( kieModule.getGAV(),
                            kieModule );
            AbstractKieModule.indexParts( kieModules,
                                          kBaseModels,
                                          kSessionModels,
                                          kJarFromKBaseName );
            initClassLaoder();
        }
    }

    public void verify(Messages messages) {

        for ( KieBaseModel model : kBaseModels.values() ) {
            AbstractKieModule.createKieBase( (KieBaseModelImpl) model,
                                             this,
                                             messages );
        }
    }   
    
    public void initClassLaoder() {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( InternalKieModule kModule : kieModules.values() ) {
            for ( String fileName : kModule.getFileNames() ) {
                if ( fileName.endsWith( ".class" ) ) {
                    classes.put( fileName,
                                 kModule.getBytes( fileName ) );
                }
            }
        }
        if ( !classes.isEmpty() ) {
            cl.addClassLoaderToEnd( new ClassUtils.MapClassLoader( classes,
                                                                   cl ) );
        }
    }

    public GAV getGAV() {
        return kieModule.getGAV();
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get( kBaseName );
    }

    public boolean kieBaseExists(String kBaseName) {
        return kBaseModels.containsKey( kBaseName );
    }

    public boolean kieSessionExists(String kSessionName) {
        return kSessionModels.containsKey( kSessionName );
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kBaseModels.get( kBaseName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionModels.get( kSessionName );
    }

    @Override
    public CompositeClassLoader getClassLoader() {
        return this.cl;
    }

}
