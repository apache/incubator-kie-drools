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

import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.AFReleaseIdImpl;
import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.commons.jci.readers.DiskResourceReader;
import org.drools.compiler.commons.jci.readers.ResourceReader;
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
import org.kie.internal.builder.conf.GroupDRLsInKieBasesByFolderOption;
import org.kie.internal.jci.CompilationProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kproject.ReleaseIdImpl.adapt;
import static org.drools.core.util.StringUtils.codeAwareIndexOf;

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

    private static final String[] SUPPORTED_RESOURCES_ROOTS = new String[] { RESOURCES_ROOT_DOT_SEPARATOR, SPRING_BOOT_ROOT };

    private ResultsImpl results;
    private final ResourceReader srcMfs;

    private MemoryFileSystem trgMfs;

    private InternalKieModule kModule;

    private byte[] pomXml;
    private AFReleaseId releaseId;

    private byte[] kModuleModelXml;
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
            for ( AFReleaseId dep : actualPomModel.getDependencies( DependencyFilter.COMPILE_FILTER ) ) {
                KieModule depModule = repository.getKieModule( adapt( dep, actualPomModel ), actualPomModel );
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
        try {
            BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> kprojectSupplier =
                    (BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject>) projectClass.getField( "SUPPLIER" ).get( null );
            return buildAll( kprojectSupplier, o -> true );
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException( e );
        }
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

            MemoryKieModule memoryKieModule = new MemoryKieModule( adapt( releaseId ), kModuleModel, trgMfs );

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

            buildKieProject( results, kProject, trgMfs );
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
        new KieMetaInfoBuilder( kModule ).writeKieModuleMetaInfo( trgMfs );
    }

    public static String getCompilationCachePath( AFReleaseId releaseId,
                                                  String kbaseName ) {
        return ( (AFReleaseIdImpl) releaseId ).getCompilationCachePathPrefix() + kbaseName.replace( '.', '/' ) + "/kbase.cache";
    }

    public static void buildKieModule( InternalKieModule kModule,
                                       ResultsImpl messages ) {
        buildKieProject( messages, new KieModuleKieProject( kModule ), null );
    }

    private static void buildKieProject( ResultsImpl messages,
                                         KieModuleKieProject kProject,
                                         MemoryFileSystem trgMfs ) {
        kProject.init();
        kProject.verify( messages );

        if ( messages.filterMessages( Level.ERROR ).isEmpty() ) {
            InternalKieModule kModule = kProject.getInternalKieModule();
            if ( trgMfs != null ) {
                new KieMetaInfoBuilder( kModule ).writeKieModuleMetaInfo( trgMfs );
                kProject.writeProjectOutput(trgMfs, messages);
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
        boolean useFolders = Boolean.valueOf( kModuleModel.getConfigurationProperty( GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME ) );

        for ( String fileName : srcMfs.getFileNames() ) {
            String normalizedName = fileName.replace( File.separatorChar, '/' );
            if ( normalizedName.startsWith( RESOURCES_ROOT ) && isFileInKieBase( kieBase, normalizedName, () -> srcMfs.getBytes( normalizedName ), useFolders ) ) {
                copySourceToTarget( normalizedName );
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
        kModule = ((MemoryKieModule)kModule).cloneForIncrementalCompilation( adapt( releaseId ), kModuleModel, trgMfs );
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

    public static boolean filterFileInKBase( InternalKieModule kieModule, KieBaseModel kieBase, String fileName, Supplier<byte[]> file, boolean useFolders ) {
        return isFileInKieBase( kieBase, fileName, file, useFolders ) &&
                ( isKieExtension( fileName ) || getResourceType( kieModule, fileName ) != null );
    }

    private static boolean isKieExtension(String fileName) {
        return !isJavaSourceFile( fileName ) && ResourceType.determineResourceType(fileName) != null;
    }

    private static boolean isFileInKieBase( KieBaseModel kieBase, String fileName, Supplier<byte[]> file, boolean useFolders ) {
        int lastSep = fileName.lastIndexOf( "/" );
        if ( lastSep + 1 < fileName.length() && fileName.charAt( lastSep + 1 ) == '.' ) {
            // skip dot files
            return false;
        }
        if ( kieBase.getPackages().isEmpty() ) {
            return true;
        } else {
            String folderNameForFile = lastSep > 0 ? fileName.substring( 0, lastSep ) : "";
            int resourcesPos = folderNameForFile.indexOf( RESOURCES_ROOT );
            if (resourcesPos >= 0) {
                folderNameForFile = folderNameForFile.substring( resourcesPos + RESOURCES_ROOT.length() );
            }
            String pkgNameForFile = packageNameForFile( fileName, folderNameForFile, !useFolders, file );
            return isPackageInKieBase( kieBase, pkgNameForFile );
        }
    }

    private static String packageNameForFile( String fileName, String folderNameForFile, boolean discoverPackage, Supplier<byte[]> file ) {
        String packageNameFromFolder = folderNameForFile.replace( '/', '.' );

        if (discoverPackage) {
            String packageNameForFile = packageNameFromAsset(fileName, file);
            if (packageNameForFile != null) {
                if ( !packageNameForFile.equals( packageNameFromFolder ) ) {
                    log.warn( "File '" + fileName + "' is in folder '" + folderNameForFile + "' but declares package '" + packageNameForFile +
                            "'. It is advised to have a correspondance between package and folder names." );
                }
                return packageNameForFile;
            }
        }

        return packageNameFromFolder;
    }

    private static String packageNameFromAsset(String fileName, Supplier<byte[]> file) {
        if (fileName.endsWith( ".drl" )) {
            return packageNameFromDrl( file.get() );
        }
        if (fileName.endsWith( ".xls" ) || fileName.endsWith( ".xlsx" ) || fileName.endsWith( ".csv" )) {
            return packageNameFromDtable( file.get() );
        }
        return null;
    }

    private static String packageNameFromDrl(byte[] bytes) {
        String content = bytes != null ? new String(bytes) : "";
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

    private static String packageNameFromDtable(byte[] bytes) {
        String content = bytes != null ? new String(bytes) : "";
        int pkgPos = content.indexOf( "RuleSet" );
        if (pkgPos >= 0) {
            pkgPos += "RuleSet ".length();
            for (; !Character.isJavaIdentifierStart( content.charAt( pkgPos ) ); pkgPos++);
            int end = pkgPos+1;
            for (; Character.isLetterOrDigit( content.charAt( end ) ) || content.charAt( end ) == '.'; end++);
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
    public KieModule getKieModuleIgnoringErrors() {
        return getKieModule( true );
    }

    private KieModule getKieModule( boolean ignoreErrors ) {
        if ( !isBuilt() ) {
            buildAll();
        }

        if ( !ignoreErrors && ( getResults().hasMessages( Level.ERROR ) || kModule == null ) ) {
            throw new RuntimeException( "Unable to get KieModule, Errors Existed: " + getResults() );
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
        List<String> classFiles = new ArrayList<>();
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.endsWith( ".class" ) ) {
                trgMfs.write( fileName,
                              srcMfs.getBytes( fileName ),
                              true );
                classFiles.add( fileName.substring( 0,
                                                    fileName.length() - ".class".length() ) );
            }
        }

        List<String> javaFiles = new ArrayList<>();
        List<String> javaTestFiles = new ArrayList<>();
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
}
