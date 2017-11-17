/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import static org.drools.compiler.kproject.ReleaseIdImpl.adapt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.AFReleaseIdImpl;
import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.commons.jci.readers.DiskResourceReader;
import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.compiler.PMMLCompiler;
import org.drools.compiler.compiler.PMMLCompilerFactory;
import org.drools.compiler.compiler.PMMLResource;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KieBuilderSet;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.io.ResourceTypeImpl;

public class KieBuilderImpl
        implements
        InternalKieBuilder {

    static final String RESOURCES_ROOT = "src/main/resources/";
    static final String PMML_POJO_ROOT = "org/drools/pmml/pmml_4_2/model/";
    static final String JAVA_ROOT = "src/main/java/";
    static final String JAVA_TEST_ROOT = "src/test/java/";

    private static final String RESOURCES_ROOT_DOT_SEPARATOR = RESOURCES_ROOT.replace( '/', '.' );
    private static final String SPRING_BOOT_ROOT = "BOOT-INF.classes.";

    private static final String[] SUPPORTED_RESOURCES_ROOTS = new String[] { RESOURCES_ROOT_DOT_SEPARATOR, SPRING_BOOT_ROOT };

    private ResultsImpl results;
    private final ResourceReader srcMfs;

    private MemoryFileSystem trgMfs;

    private MemoryKieModule kModule;

    private byte[] pomXml;
    private AFReleaseId releaseId;

    private byte[] kModuleModelXml;
    private KieModuleModel kModuleModel;

    private Collection<KieModule> kieDependencies;

    private KieBuilderSetImpl kieBuilderSet;

    private ClassLoader classLoader;

    private PomModel pomModel;

    private PMMLCompiler pmmlCompiler; // necessary for being able to pull mining schema POJOs out of the PMML
    
    private List<PMMLResource> pmmlResources;

    public KieBuilderImpl( File file ) {
    	String fileName = file.getName();
    	if (fileName.endsWith(ResourceType.PMML.getDefaultExtension())) {
    		// Attempt to pre-compile
    	}
        this.srcMfs = new DiskResourceReader( file );
    }

    public KieBuilderImpl( KieFileSystem kieFileSystem ) {
        this( kieFileSystem, null );
    }

    public KieBuilderImpl( KieFileSystem kieFileSystem,
                           ClassLoader classLoader ) {
        this.classLoader = classLoader;
        ResourceReader tmpSrc = ((KieFileSystemImpl)kieFileSystem).asMemoryFileSystem();
        Collection<String> fileNames = tmpSrc.getFileNames();
        if (fileNames != null && !fileNames.isEmpty()) {
        	for (String fn : fileNames) {
        		boolean isPmmlFile = false;
        		String fileName = (fn.startsWith(RESOURCES_ROOT)) ? fn.substring(RESOURCES_ROOT.length()) : fn;
        		if (tmpSrc.isAvailable(fn+".properties")) {
                    Properties prop = new Properties();
                    try {
                        prop.load(new ByteArrayInputStream(tmpSrc.getBytes(fn + ".properties")));
                    } catch (IOException e) {
                        System.out.println("Error loading resource configuration from file: " + fn + ".properties");
                    }
        			ResourceConfiguration rc = ResourceTypeImpl.fromProperties(prop);
        			isPmmlFile = ResourceType.PMML.equals(((ResourceConfigurationImpl) rc).getResourceType());
        		}
        		if (fileName.endsWith(ResourceType.PMML.getDefaultExtension()) || isPmmlFile) {
        			pmmlResources = getPmmlCompiler().precompile(fileName, classLoader, null);
        		}
        	}
        }
        if (pmmlResources != null) {
        	for (PMMLResource res: pmmlResources) {
        		if (res.getKieBaseModel() != null) {
	        		String packageName = res.getPackageName();
	        		String packageDir = packageName.replaceAll("\\.", "/");
	        		Map<String,String> rules = res.getRules();
	        		for (String key: rules.keySet()) {
	        			Resource ruleResource = ResourceFactory.newByteArrayResource(rules.get(key).getBytes()).setResourceType(ResourceType.DRL);
	        			StringBuilder bldr = new StringBuilder();
	        			bldr.append(packageDir).append("/").append(key).append(".drl");
	        			ruleResource.setSourcePath(bldr.toString());
	        			kieFileSystem.write(ruleResource);
	        		}
        		}
        	}
        }
        srcMfs = ( (KieFileSystemImpl) kieFileSystem ).asMemoryFileSystem();
    }

    @Override
    public KieBuilder setDependencies( KieModule... dependencies ) {
        this.kieDependencies = Arrays.asList( dependencies );
        return this;
    }

    @Override
    public KieBuilder setDependencies( Resource... resources ) {
        KieRepositoryImpl kr = (KieRepositoryImpl) KieServices.Factory.get().getRepository();
        List<KieModule> list = new ArrayList<KieModule>();
        for ( Resource res : resources ) {
            InternalKieModule depKieMod = (InternalKieModule) kr.getKieModule( res );
            list.add( depKieMod );
        }
        this.kieDependencies = list;
        return this;
    }

    private PomModel init() {
        KieServices ks = KieServices.Factory.get();

        results = new ResultsImpl();

        // if pomXML is null it will generate a default, using default ReleaseId
        // if pomXml is invalid, it assign pomModel to null
        PomModel pomModel = getPomModel();

        // if kModuleModelXML is null or invalid it will generate a default kModule, with a default kbase name
        buildKieModuleModel();

        if ( pomModel != null ) {
            // creates ReleaseId from build pom
            // If the pom was generated, it will be the same as teh default ReleaseId
            releaseId = pomModel.getReleaseId();

            // add all the pom dependencies to this builder ... not sure this is a good idea (?)
            KieRepositoryImpl repository = (KieRepositoryImpl) ks.getRepository();
            for ( AFReleaseId dep : pomModel.getDependencies( DependencyFilter.COMPILE_FILTER ) ) {
                KieModule depModule = repository.getKieModule( adapt( dep ), pomModel );
                if ( depModule != null ) {
                    addKieDependency( depModule );
                }
            }
        } else {
            // if the pomModel is null it means that the provided pom.xml is invalid so use the default releaseId
            releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        }

        return pomModel;
    }

    private void addKieDependency( KieModule depModule ) {
        if ( kieDependencies == null ) {
            kieDependencies = new ArrayList<KieModule>();
        }
        kieDependencies.add( depModule );
    }

    @Override
    public KieBuilder buildAll() {
        return buildAll( KieModuleKieProject::new, o -> true );
    }

    @Override
    public KieBuilder buildAll( Predicate<String> classFilter ) {
        return buildAll( KieModuleKieProject::new, classFilter );
    }

    public KieBuilder buildAll(BiFunction<InternalKieModule, ClassLoader, KieProject> kprojectSupplier) {
        return buildAll( kprojectSupplier, o -> true );
    }

    public KieBuilder buildAll( BiFunction<InternalKieModule, ClassLoader, KieProject> kprojectSupplier, Predicate<String> classFilter ) {
        PomModel pomModel = init();

        // kModuleModel will be null if a provided pom.xml or kmodule.xml is invalid
        if ( !isBuilt() && kModuleModel != null ) {
            trgMfs = new MemoryFileSystem();
            writePomAndKModule();
            addKBasesFilesToTrg();
            markSource();

            kModule = new MemoryKieModule( adapt( releaseId ),
                                           kModuleModel,
                                           trgMfs );

            if ( kieDependencies != null && !kieDependencies.isEmpty() ) {
                for ( KieModule kieModule : kieDependencies ) {
                    kModule.addKieDependency( (InternalKieModule) kieModule );
                }
            }
            if ( pomModel != null ) {
                kModule.setPomModel( pomModel );
            }

            KieProject kProject = kprojectSupplier.apply( kModule, classLoader );
            for ( ReleaseId unresolvedDep : kModule.getUnresolvedDependencies() ) {
                results.addMessage( Level.ERROR, "pom.xml", "Unresolved dependency " + unresolvedDep );
            }
            
            
            if (pmmlResources != null) {
            	for (PMMLResource resource: pmmlResources) {
            		buildPMMLPojos(kProject);
            	}
            }
            compileJavaClasses( kProject.getClassLoader(), classFilter );

            buildKieProject( kModule, results, kProject, trgMfs );
        }
        return this;
    }
    
    

    
    private void buildPMMLPojos(KieProject kProject) {
    	if (pmmlResources != null) {
    		KieFileSystem javaSource = KieServices.Factory.get().newKieFileSystem();
    		for (PMMLResource resource: pmmlResources) {
    			Map<String,String> pojos = resource.getPojoDefinitions();
    			for (String key: pojos.keySet()) {
    				String javaCode = pojos.get(key);
    				if (javaCode != null && !javaCode.trim().isEmpty()) {
	    				Resource res = ResourceFactory.newByteArrayResource(javaCode.getBytes()).setResourceType(ResourceType.JAVA);
	    				StringBuilder bldr = new StringBuilder(PMML_POJO_ROOT);
	    				bldr.append(key.substring(0, 1).toUpperCase()+key.substring(1)).append(".java");
	    				res.setSourcePath(bldr.toString());
	    				javaSource.write(res);
    				}
    			}
    		}
    		
    		ResourceReader src = ((KieFileSystemImpl)javaSource).asMemoryFileSystem();
            List<String> javaFileNames = getJavaFileNames(src);
            if (javaFileNames != null && !javaFileNames.isEmpty()) {
                ClassLoader classLoader = kProject.getClassLoader();
                KnowledgeBuilderConfigurationImpl kconf = new KnowledgeBuilderConfigurationImpl( classLoader );
                JavaDialectConfiguration javaConf = (JavaDialectConfiguration) kconf.getDialectConfiguration( "java" );
            	compileJavaClasses(javaConf, classLoader, javaFileNames, JAVA_ROOT, src);
            }
    	}
    }
    
    
    private List<String> getJavaFileNames(ResourceReader src) {
    	List<String> javaFileNames = new ArrayList<>();
        for (String fname: src.getFileNames()) {
            if (fname.endsWith(".java")) {
                javaFileNames.add(fname);
            }
        }
    	return javaFileNames;
    }

    void markSource() {
        srcMfs.mark();
    }

    Collection<String> getModifiedResourcesSinceLastMark() {
        return srcMfs.getModifiedResourcesSinceLastMark();
    }

    void updateKieModuleMetaInfo() {
        new KieMetaInfoBuilder( kModule ).writeKieModuleMetaInfo( trgMfs );
    }

    public static String getCompilationCachePath( AFReleaseId releaseId,
                                                  String kbaseName ) {
        return ( (AFReleaseIdImpl) releaseId ).getCompilationCachePathPrefix() + kbaseName.replace( '.', '/' ) + "/kbase.cache";
    }

    public static void buildKieModule( InternalKieModule kModule,
                                       ResultsImpl messages ) {
        buildKieProject( kModule, messages, new KieModuleKieProject( kModule ), null );
    }

    private static void buildKieProject( InternalKieModule kModule,
                                         ResultsImpl messages,
                                         KieProject kProject,
                                         MemoryFileSystem trgMfs ) {
        kProject.init();
        kProject.verify( messages );

        if ( messages.filterMessages( Level.ERROR ).isEmpty() ) {
            if ( trgMfs != null ) {
                new KieMetaInfoBuilder( kModule ).writeKieModuleMetaInfo( trgMfs );
                kProject.writeProjectOutput( trgMfs );
            }
            KieRepository kieRepository = KieServices.Factory.get().getRepository();
            kieRepository.addKieModule( kModule );
            for ( InternalKieModule kDep : kModule.getKieDependencies().values() ) {
                kieRepository.addKieModule( kDep );
            }
        }
    }

    private void addKBasesFilesToTrg() {
        for ( KieBaseModel kieBaseModel : kModuleModel.getKieBaseModels().values() ) {
            addKBaseFilesToTrg( kieBaseModel );
        }
    }

    private void addKBaseFilesToTrg( KieBaseModel kieBase ) {
        for ( String fileName : srcMfs.getFileNames() ) {
            fileName = fileName.replace( File.separatorChar, '/' );
            if ( fileName.startsWith( RESOURCES_ROOT ) && isFileInKieBase( kieBase, fileName ) ) {
                copySourceToTarget( fileName );
            }
        }
    }

    String copySourceToTarget( String fileName ) {
        if ( !fileName.startsWith( RESOURCES_ROOT ) ) {
            return null;
        }
        byte[] bytes = srcMfs.getBytes( fileName );
        String trgFileName = fileName.substring( RESOURCES_ROOT.length() );
        if ( bytes != null ) {
            trgMfs.write( trgFileName, bytes, true );
        } else {
            trgMfs.remove( trgFileName );
        }
        return trgFileName;
    }

    public void setkModule( final MemoryKieModule kModule ) {
        this.kModule = kModule;
    }

    public MemoryKieModule getkModule() {
        return kModule;
    }

    public void setTrgMfs( final MemoryFileSystem trgMfs ) {
        this.trgMfs = trgMfs;
    }

    public MemoryFileSystem getTrgMfs() {
        return trgMfs;
    }

    void cloneKieModuleForIncrementalCompilation() {
        if ( !Arrays.equals( pomXml, getOrGeneratePomXml( srcMfs ) ) ) {
            pomModel = null;
        }
        trgMfs = trgMfs.clone();
        init();
        kModule = kModule.cloneForIncrementalCompilation( adapt( releaseId ), kModuleModel, trgMfs );
    }

    private void addMetaInfBuilder() {
        for ( String fileName : srcMfs.getFileNames()) {
            if ( fileName.startsWith( RESOURCES_ROOT ) && !isKieExtension( fileName ) ) {
                byte[] bytes = srcMfs.getBytes( fileName );
                trgMfs.write( fileName.substring( RESOURCES_ROOT.length() - 1 ),
                              bytes,
                              true );
            }
        }
    }

    private static ResourceType getResourceType( InternalKieModule kieModule,
                                                 String fileName ) {
        return getResourceType( kieModule.getResourceConfiguration( fileName ) );
    }

    private static ResourceType getResourceType( ResourceConfiguration conf ) {
        return conf instanceof ResourceConfigurationImpl ? ( (ResourceConfigurationImpl) conf ).getResourceType() : null;
    }

    public static boolean filterFileInKBase( InternalKieModule kieModule,
                                             KieBaseModel kieBase,
                                             String fileName ) {
        return isFileInKieBase( kieBase, fileName ) &&
                ( isKieExtension( fileName ) || getResourceType( kieModule, fileName ) != null );
    }

    private static boolean isKieExtension(String fileName) {
        return !isJavaSourceFile( fileName ) && ResourceType.determineResourceType(fileName) != null;
    }

    private static boolean isFileInKieBase( KieBaseModel kieBase,
                                            String fileName ) {
        int lastSep = fileName.lastIndexOf( "/" );
        if ( lastSep + 1 < fileName.length() && fileName.charAt( lastSep + 1 ) == '.' ) {
            // skip dot files
            return false;
        }
        if ( kieBase.getPackages().isEmpty() ) {
            return true;
        } else {
            String pkgNameForFile = lastSep > 0 ? fileName.substring( 0, lastSep ) : "";
            if ( pkgNameForFile.startsWith( RESOURCES_ROOT ) ) {
                pkgNameForFile = pkgNameForFile.substring( RESOURCES_ROOT.length() );
            }
            pkgNameForFile = pkgNameForFile.replace( '/', '.' );
            for ( String pkgName : kieBase.getPackages() ) {
                boolean isNegative = pkgName.startsWith( "!" );
                if ( isNegative ) {
                    pkgName = pkgName.substring( 1 );
                }
                if ( pkgName.equals( "*" ) || pkgNameForFile.equals( pkgName ) || pkgNameForFile.endsWith( "." + pkgName ) ) {
                    return !isNegative;
                }
                if ( pkgName.endsWith( ".*" ) ) {
                    String relativePkgNameForFile = getRelativePackageName( pkgNameForFile );
                    String pkgNameNoWildcard = pkgName.substring( 0, pkgName.length() - 2 );
                    if ( relativePkgNameForFile.equals( pkgNameNoWildcard ) || relativePkgNameForFile.startsWith( pkgNameNoWildcard + "." ) ) {
                        return !isNegative;
                    }
                    if ( relativePkgNameForFile.startsWith( kieBase.getName() + "." ) ) {
                        relativePkgNameForFile = relativePkgNameForFile.substring( kieBase.getName().length() + 1 );
                        if ( relativePkgNameForFile.equals( pkgNameNoWildcard ) || relativePkgNameForFile.startsWith( pkgNameNoWildcard + "." ) ) {
                            return !isNegative;
                        }
                    }
                }
            }
            return false;
        }
    }

    private static String getRelativePackageName( String pkgNameForFile ) {
        for ( String root : SUPPORTED_RESOURCES_ROOTS ) {
            if ( pkgNameForFile.startsWith( root ) ) {
                return pkgNameForFile.substring( root.length() );
            }
        }
        return pkgNameForFile;
    }

    @Override
    public Results getResults() {
        if ( !isBuilt() ) {
            buildAll();
        }
        return results;
    }

    @Override
    public KieModule getKieModule() {
        return getKieModule( false );
    }

    @Override
    public KieModule getKieModuleIgnoringErrors() {
        return getKieModule( true );
    }

    private KieModule getKieModule( boolean ignoreErrors ) {
        if ( !isBuilt() ) {
            buildAll();
        }

        if ( !ignoreErrors && ( getResults().hasMessages( Level.ERROR ) || kModule == null ) ) {
            throw new RuntimeException( "Unable to get KieModule, Errors Existed" );
        }
        return kModule;
    }

    private boolean isBuilt() {
        return kModule != null;
    }

    private void buildKieModuleModel() {
        if ( srcMfs.isAvailable( KieModuleModelImpl.KMODULE_SRC_PATH ) ) {
            kModuleModelXml = srcMfs.getBytes( KieModuleModelImpl.KMODULE_SRC_PATH );
            try {
                kModuleModel = KieModuleModelImpl.fromXML( new ByteArrayInputStream( kModuleModelXml ) );
            } catch ( Exception e ) {
                results.addMessage( Level.ERROR,
                                    "kmodule.xml",
                                    "kmodule.xml found, but unable to read\n" + e.getMessage() );
                // Create a default kModuleModel in the event of errors parsing the XML
                kModuleModel = KieServices.Factory.get().newKieModuleModel();
            }
        } else {
            // There's no kmodule.xml, create a default one
            kModuleModel = KieServices.Factory.get().newKieModuleModel();
        }
        
        if (pmmlResources != null && !pmmlResources.isEmpty()) {
        	for (PMMLResource resource : pmmlResources) {
        		if (resource.getKieBaseModel() != null) {
        			((KieModuleModelImpl)kModuleModel).getRawKieBaseModels().put(resource.getKieBaseModel().getName(), resource.getKieBaseModel());
        		}
        	}
        }

        if ( setDefaultsforEmptyKieModule( kModuleModel ) ) {
            kModuleModelXml = kModuleModel.toXML().getBytes( IoUtils.UTF8_CHARSET );
        }
    }

    public static boolean setDefaultsforEmptyKieModule( KieModuleModel kModuleModel ) {
        if ( kModuleModel != null && kModuleModel.getKieBaseModels().isEmpty() ) {
            // would be null if they pass a corrupted kModuleModel
            KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel( "defaultKieBase" ).addPackage( "*" ).setDefault( true );
            kieBaseModel.newKieSessionModel( "defaultKieSession" ).setDefault( true );
            kieBaseModel.newKieSessionModel( "defaultStatelessKieSession" ).setType( KieSessionModel.KieSessionType.STATELESS ).setDefault( true );
            return true;
        }
        return false;
    }

    public PomModel getPomModel() {
        if ( pomModel == null ) {
            pomModel = buildPomModel();
        }
        return pomModel;
    }

    /**
     * This can be used for performance reason to avoid the recomputation of the pomModel when it is already available
     */
    public void setPomModel( PomModel pomModel ) {
        this.pomModel = pomModel;
        if ( srcMfs.isAvailable( "pom.xml" ) ) {
            this.pomXml = srcMfs.getBytes( "pom.xml" );
        }
    }

    private PomModel buildPomModel() {
        pomXml = getOrGeneratePomXml( srcMfs );
        if ( pomXml == null ) {
            // will be null if the provided pom is invalid
            return null;
        }

        try {
            PomModel tempPomModel = PomModel.Parser.parse( "pom.xml",
                                                           new ByteArrayInputStream( pomXml ) );
            validatePomModel( tempPomModel ); // throws an exception if invalid
            return tempPomModel;
        } catch ( Exception e ) {
            results.addMessage( Level.ERROR,
                                "pom.xml",
                                "maven pom.xml found, but unable to read\n" + e.getMessage() );
        }
        return null;
    }

    public static void validatePomModel( PomModel pomModel ) {
        AFReleaseId pomReleaseId = pomModel.getReleaseId();
        if ( StringUtils.isEmpty( pomReleaseId.getGroupId() ) || StringUtils.isEmpty( pomReleaseId.getArtifactId() ) || StringUtils.isEmpty( pomReleaseId.getVersion() ) ) {
            throw new RuntimeException( "Maven pom.properties exists but ReleaseId content is malformed" );
        }
    }

    public static byte[] getOrGeneratePomXml( ResourceReader mfs ) {
        if ( mfs.isAvailable( "pom.xml" ) ) {
            return mfs.getBytes( "pom.xml" );
        } else {
            // There is no pom.xml, and thus no ReleaseId, so generate a pom.xml from the global detault.
            return generatePomXml( KieServices.Factory.get().getRepository().getDefaultReleaseId() ).getBytes( IoUtils.UTF8_CHARSET );
        }
    }

    public void writePomAndKModule() {
        addMetaInfBuilder();

        if ( pomXml != null ) {
            AFReleaseIdImpl g = (AFReleaseIdImpl) releaseId;
            trgMfs.write( g.getPomXmlPath(),
                          pomXml,
                          true );
            trgMfs.write( g.getPomPropertiesPath(),
                          generatePomProperties( releaseId ).getBytes( IoUtils.UTF8_CHARSET ),
                          true );

        }

        if ( kModuleModelXml != null ) {
            trgMfs.write( KieModuleModelImpl.KMODULE_JAR_PATH,
                          kModuleModel.toXML().getBytes( IoUtils.UTF8_CHARSET ),
                          true );
        }
    }

    public static String generatePomXml( AFReleaseId releaseId ) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" );
        sBuilder.append( "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n" );
        sBuilder.append( "    <modelVersion>4.0.0</modelVersion> \n" );

        sBuilder.append( "    <groupId>" );
        sBuilder.append( releaseId.getGroupId() );
        sBuilder.append( "</groupId> \n" );

        sBuilder.append( "    <artifactId>" );
        sBuilder.append( releaseId.getArtifactId() );
        sBuilder.append( "</artifactId> \n" );

        sBuilder.append( "    <version>" );
        sBuilder.append( releaseId.getVersion() );
        sBuilder.append( "</version> \n" );

        sBuilder.append( "    <packaging>jar</packaging> \n" );

        sBuilder.append( "    <name>Default</name> \n" );
        sBuilder.append( "</project>  \n" );

        return sBuilder.toString();
    }

    public static String generatePomProperties( AFReleaseId releaseId ) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "groupId=" );
        sBuilder.append( releaseId.getGroupId() );
        sBuilder.append( "\n" );

        sBuilder.append( "artifactId=" );
        sBuilder.append( releaseId.getArtifactId() );
        sBuilder.append( "\n" );

        sBuilder.append( "version=" );
        sBuilder.append( releaseId.getVersion() );
        sBuilder.append( "\n" );

        return sBuilder.toString();
    }

    private void compileJavaClasses( ClassLoader classLoader, Predicate<String> classFilter ) {
        List<String> classFiles = new ArrayList<String>();
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.endsWith( ".class" ) ) {
                trgMfs.write( fileName,
                              srcMfs.getBytes( fileName ),
                              true );
                classFiles.add( fileName.substring( 0,
                                                    fileName.length() - ".class".length() ) );
            }
        }

        List<String> javaFiles = new ArrayList<String>();
        List<String> javaTestFiles = new ArrayList<String>();
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( isJavaSourceFile( fileName )
                    && noClassFileForGivenSourceFile( classFiles, fileName )
                    && notVetoedByFilter( classFilter, fileName ) ) {
                fileName = fileName.replace( File.separatorChar, '/' );

                if ( !fileName.startsWith( JAVA_ROOT ) && !fileName.startsWith( JAVA_TEST_ROOT ) ) {
                    results.addMessage( Level.WARNING, fileName, "Found Java file out of the Java source folder: \"" + fileName + "\"" );
                } else if ( fileName.substring( JAVA_ROOT.length() ).indexOf( '/' ) < 0 ) {
                    results.addMessage( Level.ERROR, fileName, "A Java class must have a package: " + fileName.substring( JAVA_ROOT.length() ) + " is not allowed" );
                } else {
                    if ( fileName.startsWith( JAVA_ROOT ) ) {
                        javaFiles.add( fileName );
                    } else {
                        javaTestFiles.add( fileName );
                    }
                }
            }
        }

        if ( !javaFiles.isEmpty() || !javaTestFiles.isEmpty() ) {
            KnowledgeBuilderConfigurationImpl kconf = new KnowledgeBuilderConfigurationImpl( classLoader );
            JavaDialectConfiguration javaConf = (JavaDialectConfiguration) kconf.getDialectConfiguration( "java" );
            compileJavaClasses( javaConf, classLoader, javaFiles, JAVA_ROOT );
            compileJavaClasses( javaConf, classLoader, javaTestFiles, JAVA_TEST_ROOT );
        }
    }

    private static boolean notVetoedByFilter( final Predicate<String> classFilter,
                                               final String sourceFileName ) {
        return classFilter.test( sourceFileName );
    }

    private static boolean noClassFileForGivenSourceFile( List<String> classFiles, String sourceFileName ) {
        return !classFiles.contains( sourceFileName.substring( 0, sourceFileName.length() - ".java".length() ) );
    }

    private static boolean isJavaSourceFile( String fileName ) {
        return fileName.endsWith( ".java" );
    }

    private void compileJavaClasses( JavaDialectConfiguration javaConf,
                                     ClassLoader classLoader,
                                     List<String> javaFiles,
                                     String rootFolder,
                                     ResourceReader source) {
        if (!javaFiles.isEmpty()) {
            String[] sourceFiles = javaFiles.toArray(new String[ javaFiles.size() ]);
            JavaCompiler javaCompiler = createCompiler( javaConf, rootFolder );
            CompilationResult res = javaCompiler.compile(sourceFiles, source, trgMfs, classLoader);

            for ( CompilationProblem problem : res.getErrors() ) {
                results.addMessage( problem );
            }
            for ( CompilationProblem problem : res.getWarnings() ) {
                results.addMessage( problem );
            }
        }
    }

    private void compileJavaClasses( JavaDialectConfiguration javaConf,
                                     ClassLoader classLoader,
                                     List<String> javaFiles,
                                     String rootFolder ) {
        if ( !javaFiles.isEmpty() ) {
            String[] sourceFiles = javaFiles.toArray( new String[ javaFiles.size() ] );

            JavaCompiler javaCompiler = createCompiler( javaConf, rootFolder );
            CompilationResult res = javaCompiler.compile( sourceFiles,
                                                          srcMfs,
                                                          trgMfs,
                                                          classLoader );

            for ( CompilationProblem problem : res.getErrors() ) {
                results.addMessage( problem );
            }
            for ( CompilationProblem problem : res.getWarnings() ) {
                results.addMessage( problem );
            }
        }
    }

    private JavaCompiler createCompiler( JavaDialectConfiguration javaConf,
                                         String prefix ) {
        JavaCompiler javaCompiler = JavaCompilerFactory.getInstance().loadCompiler( javaConf );
        if ( javaCompiler instanceof EclipseJavaCompiler ) {
            ( (EclipseJavaCompiler) javaCompiler ).setPrefix( prefix );
        }
        return javaCompiler;
    }
    
    private PMMLCompiler getPmmlCompiler() {
    	if (this.pmmlCompiler == null) {
    		this.pmmlCompiler = PMMLCompilerFactory.getPMMLCompiler();
    	}
    	return this.pmmlCompiler;
    }

    public static String findPomProperties( ZipFile zipFile ) {
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        while ( zipEntries.hasMoreElements() ) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String fileName = zipEntry.getName();
            if ( fileName.endsWith( "pom.properties" ) && fileName.startsWith( "META-INF/maven/" ) ) {
                return fileName;
            }
        }
        return null;
    }

    public static File findPomProperties( java.io.File root ) {
        File mavenRoot = new File( root,
                                   "META-INF/maven" );
        return recurseToPomProperties( mavenRoot );
    }

    public static File recurseToPomProperties( File file ) {
        if ( file.isDirectory() ) {
            for ( java.io.File child : file.listFiles() ) {
                if ( child.isDirectory() ) {
                    File returnedFile = recurseToPomProperties( child );
                    if ( returnedFile != null ) {
                        return returnedFile;
                    }
                } else if ( child.getName().endsWith( "pom.properties" ) ) {
                    return child;
                }
            }
        }
        return null;
    }

    @Override
    public KieBuilderSet createFileSet( String... files ) {
        if ( kieBuilderSet == null ) {
            kieBuilderSet = new KieBuilderSetImpl( this );
        }
        return kieBuilderSet.setFiles( files );
    }

    @Override
    public IncrementalResults incrementalBuild() {
        return new KieBuilderSetImpl( this ).build();
    }
}
