package org.drools.compiler.kie.builder.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.commons.jci.readers.DiskResourceReader;
import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.util.StringUtils;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KieBuilderSet;

import org.kie.internal.io.ResourceTypeImpl;

public class KieBuilderImpl
        implements
        InternalKieBuilder {

    static final String           RESOURCES_ROOT = "src/main/resources/";
    static final String           JAVA_ROOT      = "src/main/java/";
    static final String           JAVA_TEST_ROOT = "src/test/java/";

    private ResultsImpl           results;
    private final ResourceReader  srcMfs;

    private MemoryFileSystem      trgMfs;

    private MemoryKieModule       kModule;

    private PomModel              pomModel;
    private byte[]                pomXml;
    private ReleaseId             releaseId;

    private byte[]                kModuleModelXml;
    private KieModuleModel        kModuleModel;

    private Collection<KieModule> kieDependencies;
    private Collection<ReleaseId> jarDependencies;

    private KieBuilderSetImpl     kieBuilderSet;

    public KieBuilderImpl(File file) {
        this.srcMfs = new DiskResourceReader( file );
    }

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        srcMfs = ((KieFileSystemImpl) kieFileSystem).asMemoryFileSystem();
    }

    public KieBuilder setDependencies(KieModule... dependencies) {
        this.kieDependencies = Arrays.asList(dependencies);
        return this;
    }

    public KieBuilder setDependencies(Resource... resources) {
        KieRepositoryImpl kr = (KieRepositoryImpl) KieServices.Factory.get().getRepository();
        List<KieModule> list = new ArrayList<KieModule>();
        for ( Resource res : resources ) {
            InternalKieModule depKieMod = (InternalKieModule) kr.getKieModule( res );
            list.add( depKieMod );
        }
        this.kieDependencies = list;
        return this;
    }

    private void init() {
        KieServices ks = KieServices.Factory.get();

        results = new ResultsImpl();

        // if pomXML is null it will generate a default, using default ReleaseId
        // if pomXml is invalid, it assign pomModel to null
        buildPomModel();

        // if kModuleModelXML is null it will generate a default kModule, with a default kbase name
        // if kModuleModelXML is  invalid, it will kModule to null
        buildKieModuleModel();

        if ( pomModel != null ) {
            // creates ReleaseId from build pom
            // If the pom was generated, it will be the same as teh default ReleaseId
            releaseId = pomModel.getReleaseId();

            // add all the pom dependencies to this builder ... not sure this is a good idea (?)
            KieRepositoryImpl repository = (KieRepositoryImpl) ks.getRepository();
            for ( ReleaseId dep : pomModel.getDependencies() ) {
                KieModule depModule = repository.getKieModule( dep, pomXml );
                if ( depModule != null ) {
                    addKieDependency( depModule );
                } else {
                    addJarDependency( dep );
                }
            }
        } else {
            // if the pomModel is null it means that the provided pom.xml is invalid so use the default releaseId
            releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        }
    }

    private void addKieDependency(KieModule depModule) {
        if ( kieDependencies == null ) {
            kieDependencies = new ArrayList<KieModule>();
        }
        kieDependencies.add( depModule );
    }

    private void addJarDependency(ReleaseId releaseId) {
        if ( jarDependencies == null ) {
            jarDependencies = new ArrayList<ReleaseId>();
        }
        jarDependencies.add( releaseId );
    }

    public KieBuilder buildAll() {
        init();

        // kModuleModel will be null if a provided pom.xml or kmodule.xml is invalid
        if ( !isBuilt() && kModuleModel != null ) {
            trgMfs = new MemoryFileSystem();
            writePomAndKModule();
            addKBasesFilesToTrg();
            markSource();

            kModule = new MemoryKieModule( releaseId,
                                           kModuleModel,
                                           trgMfs );

            if ( kieDependencies != null && !kieDependencies.isEmpty() ) {
                for ( KieModule kieModule : kieDependencies ) {
                    kModule.addKieDependency( (InternalKieModule) kieModule );
                }
            }
            if (pomModel != null) {
                kModule.setPomModel(pomModel);
            }

            KieModuleKieProject kProject = new KieModuleKieProject( kModule );
            for (ReleaseId unresolvedDep : kModule.getUnresolvedDependencies()) {
                results.addMessage(Level.ERROR, "pom.xml", "Unresolved dependency " + unresolvedDep);
            }

            compileJavaClasses(kProject.getClassLoader());

            if ( buildKieProject( kModule, results, kProject ) ) {
                new KieMetaInfoBuilder(trgMfs, kModule).writeKieModuleMetaInfo();
            }
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
        new KieMetaInfoBuilder(trgMfs, kModule).writeKieModuleMetaInfo();
    }

    public static String getCompilationCachePath(ReleaseId releaseId,
                                                 String kbaseName) {
        return ((ReleaseIdImpl) releaseId).getCompilationCachePathPrefix() + kbaseName.replace( '.', '/' ) + "/kbase.cache";
    }

    public static boolean buildKieModule(InternalKieModule kModule,
                                         ResultsImpl messages ) {
        return buildKieProject(kModule, messages, new KieModuleKieProject( kModule ));
    }

    private static boolean buildKieProject(InternalKieModule kModule, ResultsImpl messages, KieModuleKieProject kProject) {
        kProject.init();
        kProject.verify(messages);

        if ( messages.filterMessages( Level.ERROR ).isEmpty() ) {
            KieServices.Factory.get().getRepository().addKieModule(kModule);
            return true;
        }
        return false;
    }

    private void addKBasesFilesToTrg() {
        for ( KieBaseModel kieBaseModel : kModuleModel.getKieBaseModels().values() ) {
            addKBaseFilesToTrg( kieBaseModel );
        }
    }

    private KieBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModel kieBase,
                                                               Properties properties,
                                                               ClassLoader... classLoaders) {
        KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( properties, classLoaders );
        kbConf.setOption( kieBase.getEqualsBehavior() );
        kbConf.setOption( kieBase.getEventProcessingMode() );
        kbConf.setOption( kieBase.getDeclarativeAgenda() );
        return kbConf;
    }

    private void addKBaseFilesToTrg(KieBaseModel kieBase) {
        for ( String fileName : srcMfs.getFileNames() ) {
            fileName = fileName.replace(File.separatorChar, '/');
            if ( fileName.startsWith( RESOURCES_ROOT ) && isFileInKieBase( kieBase, fileName ) ) {
                copySourceToTarget( fileName );
            }
        }
    }

    String copySourceToTarget(String fileName) {
        if ( !fileName.startsWith(RESOURCES_ROOT) ) {
            return null;
        }
        byte[] bytes = srcMfs.getBytes( fileName );
        String trgFileName = fileName.substring( RESOURCES_ROOT.length() );
        if ( bytes != null ) {
            FormatConverter formatConverter = FormatsManager.get().getConverterFor( trgFileName );
            if ( formatConverter != null ) {
                FormatConversionResult result = formatConverter.convert( trgFileName, bytes );
                trgFileName = result.getConvertedName();
                trgMfs.write( trgFileName, result.getContent(), true );
            } else if ( getResourceType( fileName ) != null ) {
                trgMfs.write( trgFileName, bytes, true );
            }
        } else {
            trgMfs.remove( trgFileName );
        }
        return trgFileName;
    }

    private ResourceType getResourceType(String fileName) {
        if (srcMfs.isAvailable(fileName + ".properties")) {
            // configuration file available
            Properties prop = new Properties();
            try {
                prop.load(new ByteArrayInputStream(srcMfs.getBytes(fileName + ".properties")));
                return getResourceType( ResourceTypeImpl.fromProperties(prop) );
            } catch (IOException e) { }
        }
        return null;
    }

    void cloneKieModuleForIncrementalCompilation() {
        trgMfs = trgMfs.clone();
        init();
        kModule = kModule.cloneForIncrementalCompilation( releaseId, kModuleModel, trgMfs );
    }

    private void addMetaInfBuilder() {
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.startsWith( RESOURCES_ROOT ) && !FormatsManager.isKieExtension( fileName ) ) {
                byte[] bytes = srcMfs.getBytes( fileName );
                trgMfs.write( fileName.substring( RESOURCES_ROOT.length() - 1 ),
                              bytes,
                              true );
            }
        }
    }

    private static ResourceType getResourceType(InternalKieModule kieModule, String fileName) {
        return getResourceType(kieModule.getResourceConfiguration(fileName));
    }

    private static ResourceType getResourceType(ResourceConfiguration conf) {
        return conf instanceof ResourceConfigurationImpl ? ((ResourceConfigurationImpl)conf).getResourceType() : null;
    }

    public static boolean filterFileInKBase(InternalKieModule kieModule, KieBaseModel kieBase, String fileName) {
        return isFileInKieBase( kieBase, fileName ) && (
                FormatsManager.isKieExtension( fileName ) || getResourceType(kieModule, fileName) != null);
    }

    private static boolean isFileInKieBase(KieBaseModel kieBase,
                                           String fileName) {
        if ( kieBase.getPackages().isEmpty() ) {
            return true;
        } else {
            int lastSep = fileName.lastIndexOf( "/" );
            String pkgNameForFile = lastSep > 0 ? fileName.substring( 0, lastSep ) : fileName;
            pkgNameForFile = pkgNameForFile.replace( '/', '.' );
            for ( String pkgName : kieBase.getPackages() ) {
                boolean isNegative = pkgName.startsWith( "!" );
                if ( isNegative ) {
                    pkgName = pkgName.substring( 1 );
                }
                if ( pkgName.equals( "*" ) || pkgNameForFile.endsWith( pkgName ) ||
                     (pkgName.endsWith( ".*" ) && pkgNameForFile.contains( pkgName.substring( 0, pkgName.length() - 2 ) )) ) {
                    return !isNegative;
                }
            }
            return false;
        }
    }

    public Results getResults() {
        if ( !isBuilt() ) {
            buildAll();
        }
        return results;
    }

    public KieModule getKieModule() {
        return getKieModule( false );
    }

    public KieModule getKieModuleIgnoringErrors() {
        return getKieModule( true );
    }

    private KieModule getKieModule(boolean ignoreErrors) {
        if ( !isBuilt() ) {
            buildAll();
        }

        if ( !ignoreErrors && (getResults().hasMessages( Level.ERROR ) || kModule == null) ) {
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
            }
        } else {
            // There's no kmodule.xml, create a defualt one
            kModuleModel = KieServices.Factory.get().newKieModuleModel();
        }

        if ( setDefaultsforEmptyKieModule( kModuleModel ) ) {
            kModuleModelXml = kModuleModel.toXML().getBytes();
        }
    }

    public static boolean setDefaultsforEmptyKieModule(KieModuleModel kModuleModel) {
        if ( kModuleModel != null && kModuleModel.getKieBaseModels().isEmpty() ) {
            // would be null if they pass a corrupted kModuleModel
            KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel( "defaultKieBase" ).addPackage( "*" ).setDefault( true );
            kieBaseModel.newKieSessionModel( "defaultKieSession" ).setDefault( true );
            kieBaseModel.newKieSessionModel( "defaultStatelessKieSession" ).setType( KieSessionModel.KieSessionType.STATELESS ).setDefault( true );
            return true;
        }
        return false;
    }

    private void buildPomModel() {
        pomXml = getOrGeneratePomXml( srcMfs );
        if ( pomXml == null ) {
            // will be null if the provided pom is invalid
            return;
        }

        try {
            PomModel tempPomModel = PomModel.Parser.parse( "pom.xml",
                                                            new ByteArrayInputStream( pomXml ) );
            validatePomModel( tempPomModel ); // throws an exception if invalid
            pomModel = tempPomModel;
        } catch ( Exception e ) {
            results.addMessage( Level.ERROR,
                                "pom.xml",
                                "maven pom.xml found, but unable to read\n" + e.getMessage() );
        }
    }

    public static void validatePomModel(PomModel pomModel) {
        ReleaseId pomReleaseId = pomModel.getReleaseId();
        if ( StringUtils.isEmpty( pomReleaseId.getGroupId() ) || StringUtils.isEmpty( pomReleaseId.getArtifactId() ) || StringUtils.isEmpty( pomReleaseId.getVersion() ) ) {
            throw new RuntimeException( "Maven pom.properties exists but ReleaseId content is malformed" );
        }
    }

    public static byte[] getOrGeneratePomXml(ResourceReader mfs) {
        if ( mfs.isAvailable( "pom.xml" ) ) {
            return mfs.getBytes( "pom.xml" );
        } else {
            // There is no pom.xml, and thus no ReleaseId, so generate a pom.xml from the global detault.
            return generatePomXml( KieServices.Factory.get().getRepository().getDefaultReleaseId() ).getBytes();
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
                          generatePomProperties( releaseId ).getBytes(),
                          true );

        }

        if ( kModuleModelXml != null ) {
            trgMfs.write( KieModuleModelImpl.KMODULE_JAR_PATH,
                          kModuleModel.toXML().getBytes(),
                          true );
        }
    }

    public static String generatePomXml(ReleaseId releaseId) {
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

    public static String generatePomProperties(ReleaseId releaseId) {
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

    private void compileJavaClasses(ClassLoader classLoader) {
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
            if ( fileName.endsWith( ".java" ) && !classFiles.contains( fileName.substring( 0,
                                                                                           fileName.length() - ".java".length() ) ) ) {
                fileName = fileName.replace(File.separatorChar, '/');

                if ( !fileName.startsWith(JAVA_ROOT) && !fileName.startsWith(JAVA_TEST_ROOT) ) {
                    results.addMessage(Level.WARNING, fileName, "Found Java file out of the Java source folder: \"" + fileName + "\"");
                } else if ( fileName.substring(JAVA_ROOT.length()).indexOf('/') < 0 ) {
                    results.addMessage(Level.ERROR, fileName, "A Java class must have a package: " + fileName.substring(JAVA_ROOT.length()) + " is not allowed");
                } else {
                    if (fileName.startsWith(JAVA_ROOT)) {
                        javaFiles.add( fileName );
                    } else {
                        javaTestFiles.add( fileName );
                    }
                }
            }
        }

        compileJavaClasses(classLoader, javaFiles, JAVA_ROOT);
        compileJavaClasses(classLoader, javaTestFiles, JAVA_TEST_ROOT);
    }

    private void compileJavaClasses(ClassLoader classLoader, List<String> javaFiles, String rootFolder) {
        if ( !javaFiles.isEmpty() ) {
            String[] sourceFiles = javaFiles.toArray( new String[javaFiles.size()] );

            EclipseJavaCompiler compiler = createCompiler( rootFolder );
            CompilationResult res = compiler.compile( sourceFiles,
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

    public static String findPomProperties(ZipFile zipFile) {
        Enumeration< ? extends ZipEntry> zipEntries = zipFile.entries();
        while ( zipEntries.hasMoreElements() ) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String fileName = zipEntry.getName();
            if ( fileName.endsWith( "pom.properties" ) && fileName.startsWith( "META-INF/maven/" ) ) {
                return fileName;
            }
        }
        return null;
    }

    public static File findPomProperties(java.io.File root) {
        File mavenRoot = new File( root,
                                   "META-INF/maven" );
        return recurseToPomProperties( mavenRoot );
    }

    public static File recurseToPomProperties(File file) {
        if( file.isDirectory() ) {
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

    private EclipseJavaCompiler createCompiler(String prefix) {
        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        return new EclipseJavaCompiler( settings,
                                        prefix );
    }

    @Override
    public KieBuilderSet createFileSet(String... files) {
        if ( kieBuilderSet == null ) {
            kieBuilderSet = new KieBuilderSetImpl( this );
        }
        return kieBuilderSet.setFiles( files );
    }

    public IncrementalResults incrementalBuild() {
        return new KieBuilderSetImpl( this ).build();
    }
}
