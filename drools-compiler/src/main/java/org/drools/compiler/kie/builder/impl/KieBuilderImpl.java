/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kie.builder.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.io.InternalResource;
import org.drools.io.ResourceConfigurationImpl;
import org.drools.util.IoUtils;
import org.drools.util.PortablePath;
import org.drools.util.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.CompilationErrorsException;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KieBuilderSet;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.base.util.Drools.hasXmlSupport;
import static org.drools.util.StringUtils.codeAwareIndexOf;
import static org.kie.internal.builder.KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration;

public class KieBuilderImpl
        implements
        InternalKieBuilder {

    private static final Logger log = LoggerFactory.getLogger( KieBuilderImpl.class );

    static final String RESOURCES_ROOT = "src/main/resources/";
    static final String RESOURCES_TEST_ROOT = "src/test/resources";
    static final String JAVA_ROOT = "src/main/java/";
    static final String JAVA_TEST_ROOT = "src/test/java/";

    private static final String RESOURCES_ROOT_DOT_SEPARATOR = RESOURCES_ROOT.replace( '/', '.' );
    private static final String SPRING_BOOT_ROOT = "BOOT-INF.classes.";

    private static final PortablePath POM_PATH = PortablePath.of("pom.xml");

    private static final String[] SUPPORTED_RESOURCES_ROOTS = new String[] { RESOURCES_ROOT_DOT_SEPARATOR, SPRING_BOOT_ROOT };

    private ResultsImpl results;
    private final ResourceReader srcMfs;

    private MemoryFileSystem trgMfs;

    private InternalKieModule kModule;

    private byte[] pomXml;
    private ReleaseId releaseId;

    private KieModuleModel kModuleModel;

    private Collection<KieModule> kieDependencies;

    private KieBuilderSetImpl kieBuilderSet;

    private ClassLoader classLoader;

    private PomModel pomModel;

    
    public KieBuilderImpl( File file ) {
        this.srcMfs = new DiskResourceReader( file );
    }

    public KieBuilderImpl( KieFileSystem kieFileSystem ) {
        this( kieFileSystem, null );
    }

    public KieBuilderImpl( KieFileSystem kieFileSystem,
                           ClassLoader classLoader ) {
        this.classLoader = classLoader;
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
        List<KieModule> list = new ArrayList<>();
        for ( Resource res : resources ) {
            InternalKieModule depKieMod = (InternalKieModule) kr.getKieModule( res );
            list.add( depKieMod );
        }
        this.kieDependencies = list;
        return this;
    }

    private PomModel init(PomModel projectPomModel) {
        results = new ResultsImpl();
        final PomModel actualPomModel;
        if (projectPomModel == null) {
            // if pomModel is null it will generate one from pom.xml
            // if pomXml is invalid, it assigns pomModel to null
            actualPomModel = buildPomModel();
        } else {
            actualPomModel = projectPomModel;
        }

        KieServices ks = KieServices.Factory.get();

        // if kModuleModelXML is null or invalid it will generate a default kModule, with a default kbase name
        buildKieModuleModel();

        if ( actualPomModel != null ) {
            // creates ReleaseId from build pom
            // If the pom was generated, it will be the same as teh default ReleaseId
            releaseId = actualPomModel.getReleaseId();

            // add all the pom dependencies to this builder ... not sure this is a good idea (?)
            KieRepositoryImpl repository = (KieRepositoryImpl) ks.getRepository();
            for ( ReleaseId dep : actualPomModel.getDependencies( DependencyFilter.COMPILE_FILTER ) ) {
                KieModule depModule = repository.getKieModule( dep, actualPomModel );
                if ( depModule != null ) {
                    addKieDependency( depModule );
                }
            }
        } else {
            // if the pomModel is null it means that the provided pom.xml is invalid so use the default releaseId
            releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        }

        return actualPomModel;
    }

    private void addKieDependency( KieModule depModule ) {
        if ( kieDependencies == null ) {
            kieDependencies = new ArrayList<>();
        }
        kieDependencies.add( depModule );
    }

    @Override
    public KieBuilder buildAll() {
        return buildAll( KieModuleKieProject::new, o -> true );
    }

    @Override
    public KieBuilder buildAll( Class<? extends ProjectType> projectClass ) {
        if (projectClass == null) {
            return buildAll();
        }
        try {
            BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> kprojectSupplier = getSupplier(projectClass);
            return buildAll( kprojectSupplier, o -> true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException( e );
        }
    }

    private BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> getSupplier(Class<?> canonicalModelKieProjectClass) throws IllegalAccessException, NoSuchFieldException {
        return (BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject>) canonicalModelKieProjectClass.getField("SUPPLIER").get(null);
    }

    @Override
    public KieBuilder buildAll( Predicate<String> classFilter ) {
        return buildAll( KieModuleKieProject::new, classFilter );
    }

    public KieBuilder buildAll( BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> kprojectSupplier, Predicate<String> classFilter ) {
        final PomModel currentProjectPomModel = init(pomModel);

        // kModuleModel will be null if a provided pom.xml or kmodule.xml is invalid
        if ( !isBuilt() && kModuleModel != null ) {
            trgMfs = new MemoryFileSystem();
            writePomAndKModule();
            addKBasesFilesToTrg();
            markSource();

            MemoryKieModule memoryKieModule = new MemoryKieModule( releaseId, kModuleModel, trgMfs );

            if ( kieDependencies != null && !kieDependencies.isEmpty() ) {
                for ( KieModule kieModule : kieDependencies ) {
                    memoryKieModule.addKieDependency( (InternalKieModule) kieModule );
                }
            }
            if ( currentProjectPomModel != null ) {
                memoryKieModule.setPomModel( currentProjectPomModel );
            }

            KieModuleKieProject kProject = kprojectSupplier.apply( memoryKieModule, classLoader );
            for ( ReleaseId unresolvedDep : memoryKieModule.getUnresolvedDependencies() ) {
                results.addMessage( Level.ERROR, "pom.xml", "Unresolved dependency " + unresolvedDep );
            }
            
            compileJavaClasses( kProject.getClassLoader(), classFilter );

            buildKieProject( kProject.createBuildContext(results), kProject, trgMfs );
            kModule = kProject.getInternalKieModule();
        }
        return this;
    }
    
    void markSource() {
        srcMfs.mark();
    }

    Collection<String> getModifiedResourcesSinceLastMark() {
        return srcMfs.getModifiedResourcesSinceLastMark();
    }

    void updateKieModuleMetaInfo() {
        if (hasXmlSupport()) {
            CompilationCacheProvider.get().writeKieModuleMetaInfo(kModule, trgMfs);
        }
    }

    public static String getCompilationCachePath( ReleaseId releaseId,
                                                  String kbaseName ) {
        return ( (ReleaseIdImpl) releaseId ).getCompilationCachePathPrefix() + kbaseName.replace( '.', '/' ) + "/kbase.cache";
    }

    public static void buildKieModule( InternalKieModule kModule,
                                       BuildContext buildContext ) {
        buildKieProject( buildContext, new KieModuleKieProject( kModule ), null );
    }

    private static void buildKieProject( BuildContext buildContext,
                                         KieModuleKieProject kProject,
                                         MemoryFileSystem trgMfs ) {
        kProject.init();
        kProject.verify( buildContext );

        if ( buildContext.getMessages().filterMessages( Level.ERROR ).isEmpty() ) {
            InternalKieModule kModule = kProject.getInternalKieModule();
            if ( trgMfs != null ) {
                if (hasXmlSupport()) {
                    CompilationCacheProvider.get().writeKieModuleMetaInfo( kModule, trgMfs );
                }
                kProject.writeProjectOutput(trgMfs, buildContext);
            }
            KieRepository kieRepository = KieServices.Factory.get().getRepository();
            kieRepository.addKieModule( kModule );
            for ( InternalKieModule kDep : kModule.getKieDependencies().values() ) {
                kieRepository.addKieModule( kDep );
            }
        }
    }

    private void addKBasesFilesToTrg() {
        for ( PortablePath filePath : srcMfs.getFilePaths() ) {
            if ( filePath.startsWith( RESOURCES_ROOT ) ) {
                copySourceToTarget( filePath );
            }
        }
    }

    String copySourceToTarget( PortablePath filePath ) {
        if ( !filePath.startsWith( RESOURCES_ROOT ) ) {
            return null;
        }
        Resource resource = getResource( srcMfs, filePath );
        PortablePath trgFileName = filePath.substring( RESOURCES_ROOT.length() );
        if ( resource != null ) {
            trgMfs.write( trgFileName, resource, true );
        } else {
            trgMfs.remove( trgFileName );
        }
        return trgFileName.asString();
    }

    public void setkModule( final MemoryKieModule kModule ) {
        this.kModule = kModule;
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
        init(pomModel);
        kModule = kModule.cloneForIncrementalCompilation( releaseId, kModuleModel, trgMfs );
    }

    private void addMetaInfBuilder() {
        for ( PortablePath filePath : srcMfs.getFilePaths()) {
            if ( filePath.startsWith( RESOURCES_ROOT ) && !isKieExtension( filePath.asString() ) ) {
                trgMfs.write( filePath.substring( RESOURCES_ROOT.length() ),
                              getResource( srcMfs, filePath ),
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

    public static boolean filterFileInKBase( InternalKieModule kieModule, KieBaseModel kieBase, String fileName, Supplier<InternalResource> file, boolean useFolders ) {
        return isFileInKieBase( kieBase, fileName, file, useFolders ) &&
                ( isKieExtension( fileName ) || getResourceType( kieModule, fileName ) != null );
    }

    private static boolean isKieExtension(String fileName) {
        return !isJavaSourceFile( fileName ) && ResourceType.determineResourceType(fileName) != null;
    }

    private static boolean isFileInKieBase( KieBaseModel kieBase, String fileName, Supplier<InternalResource> file, boolean useFolders ) {
        int lastSep = fileName.lastIndexOf( "/" );
        if ( lastSep + 1 < fileName.length() && fileName.charAt( lastSep + 1 ) == '.' ) {
            // skip dot files
            return false;
        }
        if ( kieBase.getPackages().isEmpty() ) {
            return true;
        } else {
            String folderNameForFile = lastSep > 0 ? fileName.substring( 0, lastSep ) : "";
            String pkgNameForFile = packageNameForFile( fileName, folderNameForFile, !useFolders, file );
            return isPackageInKieBase( kieBase, pkgNameForFile );
        }
    }

    private static String packageNameForFile( String fileName, String folderNameForFile, boolean discoverPackage, Supplier<InternalResource> file ) {
        String packageNameFromFolder = getRelativePackageName(folderNameForFile.replace( '/', '.' ));
        if (discoverPackage) {
            String packageNameForFile = packageNameFromAsset(fileName, file.get());
            if (packageNameForFile != null) {
                packageNameForFile = getRelativePackageName( packageNameForFile );
                if ( !packageNameForFile.equals( packageNameFromFolder ) ) {
                    log.warn( "File '" + fileName + "' is in folder '" + folderNameForFile + "' but declares package '" + packageNameForFile +
                            "'. It is advised to have a correspondance between package and folder names." );
                }
                return packageNameForFile;
            }
        }

        return packageNameFromFolder;
    }

    private static String packageNameFromAsset(String fileName, InternalResource file) {
        if (file == null) {
            return null;
        }
        if (fileName.endsWith( ".drl" )) {
            return packageNameFromDrl( new String(file.getBytes()) );
        }
        if (fileName.endsWith( ".xls" ) || fileName.endsWith( ".xlsx" )) {
            return packageNameFromDtable( file );
        }
        if (fileName.endsWith( ".csv" )) {
            return packageNameFromCsv( file );
        }
        return null;
    }

    private static String packageNameFromDrl(String content) {
        int pkgPos = codeAwareIndexOf( content, "package " );
        if (pkgPos >= 0) {
            pkgPos += "package ".length();
            int semiPos = content.indexOf( ';', pkgPos );
            int breakPos = content.indexOf( '\n', pkgPos );
            int end = semiPos > 0 ? (breakPos > 0 ? Math.min( semiPos, breakPos ) : semiPos) : breakPos;
            if ( end > 0 ) {
                return content.substring( pkgPos, end ).trim();
            }
        }
        return null;
    }

    private static String packageNameFromDtable(InternalResource resource) {
        try {
            String generatedDrl = DecisionTableFactory.loadFromResource( resource, new DecisionTableConfigurationImpl() );
            return packageNameFromDrl( generatedDrl );
        } catch (Exception e) {
            return packageNameFromCsv( resource );
        }
    }

    private static String packageNameFromCsv(InternalResource resource) {
        String content = new String(resource.getBytes());
        int pkgPos = content.indexOf( "RuleSet" );
        if (pkgPos >= 0) {
            pkgPos += "RuleSet ".length();
            for (; !Character.isJavaIdentifierStart( content.charAt( pkgPos ) ); pkgPos++) {
                
            };
            int end = pkgPos+1;
            for (; Character.isLetterOrDigit( content.charAt( end ) ) || content.charAt( end ) == '.'; end++) {
                
            };
            return content.substring( pkgPos, end ).trim();
        }
        return null;
    }

    public static boolean isPackageInKieBase( KieBaseModel kieBaseModel, String pkgName ) {
        for ( String candidatePkg : kieBaseModel.getPackages() ) {
            boolean isNegative = candidatePkg.startsWith( "!" );
            if ( isNegative ) {
                candidatePkg = candidatePkg.substring( 1 );
            }
            if ( candidatePkg.equals( "*" ) || pkgName.equals( candidatePkg ) || pkgName.endsWith( "." + candidatePkg ) ) {
                return !isNegative;
            }
            if ( candidatePkg.endsWith( ".*" ) ) {
                String relativePkgNameForFile = getRelativePackageName( pkgName );
                String pkgNameNoWildcard = candidatePkg.substring( 0, candidatePkg.length() - 2 );
                if ( relativePkgNameForFile.equals( pkgNameNoWildcard ) || relativePkgNameForFile.startsWith( pkgNameNoWildcard + "." ) ) {
                    return !isNegative;
                }
                if ( relativePkgNameForFile.startsWith( kieBaseModel.getName() + "." ) ) {
                    relativePkgNameForFile = relativePkgNameForFile.substring( kieBaseModel.getName().length() + 1 );
                    if ( relativePkgNameForFile.equals( pkgNameNoWildcard ) || relativePkgNameForFile.startsWith( pkgNameNoWildcard + "." ) ) {
                        return !isNegative;
                    }
                }
            }
        }
        return false;
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
    public KieModule getKieModule(Class<? extends ProjectType> projectClass) {
        return getKieModule( false , projectClass);
    }

    @Override
    public KieModule getKieModuleIgnoringErrors() {
        return getKieModule( true );
    }

    private KieModule getKieModule(boolean ignoreErrors, Class<? extends ProjectType> projectClass) {
        if ( !isBuilt() ) {
            buildAll(projectClass);
        }

        if ( !ignoreErrors && ( getResults().hasMessages( Level.ERROR ) || kModule == null ) ) {
            throw new CompilationErrorsException( getResults().getMessages( Level.ERROR ) );
        }
        return kModule;
    }

    private KieModule getKieModule( boolean ignoreErrors ) {
        return getKieModule(ignoreErrors, DrlProject.class);
    }

    private boolean isBuilt() {
        return kModule != null;
    }

    @Override
    public InternalKieBuilder withKModuleModel( KieModuleModel kModuleModel ) {
        this.kModuleModel = kModuleModel;
        return this;
    }

    private void buildKieModuleModel() {
        if (kModuleModel == null) {
            if ( srcMfs.isAvailable( KieModuleModelImpl.KMODULE_SRC_PATH ) ) {
                byte[] kModuleModelXml = srcMfs.getBytes( KieModuleModelImpl.KMODULE_SRC_PATH );
                try {
                    kModuleModel = KieModuleModelImpl.fromXML( new ByteArrayInputStream( kModuleModelXml ) );
                } catch (Exception e) {
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
        }
        setDefaultsforEmptyKieModule( kModuleModel );
    }

    public static void setDefaultsforEmptyKieModule( KieModuleModel kModuleModel ) {
        if ( kModuleModel != null && kModuleModel.getKieBaseModels().isEmpty() ) {
            // would be null if they pass a corrupted kModuleModel
            KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel( "defaultKieBase" ).addPackage( "*" ).setDefault( true );
            kieBaseModel.newKieSessionModel( "defaultKieSession" ).setDefault( true );
            kieBaseModel.newKieSessionModel( "defaultStatelessKieSession" ).setType( KieSessionModel.KieSessionType.STATELESS ).setDefault( true );
        }
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
        if ( srcMfs.isAvailable( POM_PATH ) ) {
            this.pomXml = srcMfs.getBytes( POM_PATH );
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
        ReleaseId pomReleaseId = pomModel.getReleaseId();
        if ( StringUtils.isEmpty( pomReleaseId.getGroupId() ) || StringUtils.isEmpty( pomReleaseId.getArtifactId() ) || StringUtils.isEmpty( pomReleaseId.getVersion() ) ) {
            throw new RuntimeException( "Maven pom.properties exists but ReleaseId content is malformed" );
        }
    }

    public static byte[] getOrGeneratePomXml( ResourceReader mfs ) {
        if ( mfs.isAvailable( POM_PATH ) ) {
            return mfs.getBytes( POM_PATH );
        } else {
            // There is no pom.xml, and thus no ReleaseId, so generate a pom.xml from the global detault.
            return generatePomXml( KieServices.Factory.get().getRepository().getDefaultReleaseId() ).getBytes( IoUtils.UTF8_CHARSET );
        }
    }

    public void writePomAndKModule() {
        addMetaInfBuilder();

        if ( pomXml != null ) {
            ReleaseIdImpl g = (ReleaseIdImpl) releaseId;
            trgMfs.write( g.getPomXmlPath(),
                          pomXml,
                          true );
            trgMfs.write( g.getPomPropertiesPath(),
                          generatePomProperties( releaseId ).getBytes( IoUtils.UTF8_CHARSET ),
                          true );

        }

        if ( kModuleModel != null && hasXmlSupport() ) {
            trgMfs.write( KieModuleModelImpl.KMODULE_JAR_PATH,
                          kModuleModel.toXML().getBytes( IoUtils.UTF8_CHARSET ),
                          true );
        }
    }

    public static String generatePomXml( ReleaseId releaseId ) {
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

    public static String generatePomProperties( ReleaseId releaseId ) {
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
        List<String> classFiles = new ArrayList<>();
        for ( PortablePath filePath : srcMfs.getFilePaths() ) {
            if ( filePath.endsWith( ".class" ) ) {
                trgMfs.write( filePath,
                              getResource( srcMfs, filePath ),
                              true );
                classFiles.add( filePath.substring( 0, filePath.asString().length() - ".class".length() ).asString() );
            }
        }

        List<String> javaFiles = new ArrayList<>();
        List<String> javaTestFiles = new ArrayList<>();
        for ( PortablePath filePath : srcMfs.getFilePaths() ) {
            String fileName = filePath.asString();
            if ( isJavaSourceFile( fileName )
                    && noClassFileForGivenSourceFile( classFiles, fileName )
                    && notVetoedByFilter( classFilter, fileName ) ) {

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
            KnowledgeBuilderConfigurationImpl kconf = newKnowledgeBuilderConfiguration(classLoader).as(KnowledgeBuilderConfigurationImpl.KEY);
            JavaConfiguration javaConf = (JavaConfiguration) kconf.getDialectConfiguration( "java" );
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

    private void compileJavaClasses( JavaConfiguration javaConf,
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
                results.addMessage( new CompilationProblemAdapter( problem ) );
            }
            for ( CompilationProblem problem : res.getWarnings() ) {
                results.addMessage( new CompilationProblemAdapter( problem ) );
            }
        }
    }

    private JavaCompiler createCompiler( JavaConfiguration javaConf, String sourceFolder ) {
        JavaCompiler javaCompiler = JavaCompilerFactory.loadCompiler( javaConf );
        javaCompiler.setSourceFolder( sourceFolder );
        return javaCompiler;
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
        return createFileSet( Level.ERROR, files );
    }

    @Override
    public KieBuilderSet createFileSet( Message.Level minimalLevel, String... files ) {
        if ( kieBuilderSet == null || kieBuilderSet.getMinimalLevel() != minimalLevel ) {
            kieBuilderSet = new KieBuilderSetImpl( this, minimalLevel );
        }
        return kieBuilderSet.setFiles( files );
    }

    @Override
    public IncrementalResults incrementalBuild() {
        return new KieBuilderSetImpl( this ).build();
    }

    private static Resource getResource( ResourceReader resourceReader, PortablePath pResourceName ) {
        if (resourceReader instanceof MemoryFileSystem) {
            return (( MemoryFileSystem ) resourceReader).getResource( pResourceName );
        }
        byte[] bytes = resourceReader.getBytes( pResourceName );
        return bytes != null ? KieServices.get().getResources().newByteArrayResource( bytes ) : null;
    }
}
