package org.kie.builder.impl;

import static org.drools.core.util.IoUtils.recursiveListFile;

import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.DiskResourceReader;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.Predicate;
import org.drools.core.util.StringUtils;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.xml.MinimalPomParser;
import org.drools.xml.PomModel;
import org.kie.KieBase;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieServices;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.Message;
import org.kie.builder.Message.Level;
import org.kie.builder.Results;
import org.kie.builder.ResourceType;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.util.ClassLoaderUtil;
import org.kie.util.CompositeClassLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KieBuilderImpl
    implements
    KieBuilder {

    private final ResourceReader srcMfs;

    private MemoryFileSystem     trgMfs;

    private List<Message>        messages;

    private long                 idGenerator = 1L;

    private MemoryKieModules         kieJar;

    private PomModel             pomModel;
    private byte[]               pomXml;
    private GAV                  gav;

    private byte[]               kieProjectXml;
    private KieModuleModel      kieProject;
    
    private Collection<InternalKieModule>   dependencies;

    public KieBuilderImpl(File file) {
        this.srcMfs = new DiskResourceReader( file );
        init();
    }

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        srcMfs = ((KieFileSystemImpl) kieFileSystem).asMemoryFileSystem();
        init();
    }

    private void init() {
        KieFactory kf = KieFactory.Factory.get();

        messages = new ArrayList<Message>();

        // if pomXML is null it will generate a default, using default GAV
        // if pomXml is invalid, it assign pomModel to null
        buildPomModel();

        // if kprojectXML is null it will generate a default kproject, with a default kbase name
        // if kprojectXML is  invalid, it will kieProject to null
        buildKieProject();

        if ( pomModel != null ) {
            // creates GAV from build pom
            // If the pom was generated, it will be the same as teh default GAV 
            gav = kf.newGav( pomModel.getGroupId(),
                             pomModel.getArtifactId(),
                             pomModel.getVersion() );
        }
    }

    public Results build() {
        // gav and kieProject will be null if a provided pom.xml or project.xml is invalid
        if ( !isBuilt() && gav != null && kieProject != null ) {
            trgMfs = new MemoryFileSystem();
            writePomAndKProject();

            kieJar = new MemoryKieModules( gav,
                                       kieProject,
                                       trgMfs );
            
            ClassLoader classLoader = compileJavaClasses();
            addKBasesFilesToTrg( );
            
            //validateKBases();
            
            //kieJar
            if ( !hasResults( Level.ERROR ) ) {
                KieServices.Factory.get().getKieRepository().addKieJar( kieJar );
            }
        }
        return new ResultsImpl( messages,
                                null );
    }

    private void addKBasesFilesToTrg() {
        for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
            addKBaseFilesToTrg( kieBaseModel );            
//            Collection<KnowledgePackage> pkgsCache = addKBaseFilesToTrg( kieBaseModel );
//            if ( pkgsCache != null ) {
//                kieJar.getKnowledgePackageCache().put( kieBaseModel.getName(),
//                                                       pkgsCache );
//            }
        }
    }

    public Collection<KnowledgePackage> buildKieBase(ClassLoader classLoader,
                                                     KieBaseModel kieBase) {
//        KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
//                                                                                                        classLoader );
//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kConf );
//        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();
//        addKBaseFilesToTrg( ckbuilder,
//                               kieBase );
//        
//        if ( kieBase.getIncludes() != null ) {
//            for ( String include : kieBase.getIncludes() ) {
//                addKBaseFilesToTrg( ckbuilder,
//                                       kieProject.getKieBaseModels().get( include ) );
//            }
//        }
//        
//        ckbuilder.build();

//        if ( kbuilder.hasErrors() ) {
//            for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
//                messages.add( new MessageImpl( idGenerator++,
//                                               error ) );
//            }
//            return null;
//        }
//
//        return kbuilder.getKnowledgePackages();
        return null;
    }

    private KnowledgeBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModel kieBase,
                                                                     Properties properties,
                                                                     ClassLoader... classLoaders) {
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration( properties,
                                                                                                classLoaders );
        kbConf.setOption( kieBase.getEqualsBehavior() );
        kbConf.setOption( kieBase.getEventProcessingMode() );
        return kbConf;
    }

//    private void addKBaseFileToBuilder(CompositeKnowledgeBuilder ckbuilder,
//                                       KieBaseModel kieBase) {
        private void addKBaseFilesToTrg(KieBaseModel kieBase) {        
        String resourcesRoot = "src/main/resources/";
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( filterFileInKBase( kieBase,
                                    fileName ) ) {
                byte[] bytes = srcMfs.getBytes( fileName );
//                ckbuilder.add( ResourceFactory.newByteArrayResource( srcMfs.getBytes( fileName ) ),
//                               ResourceType.determineResourceType( fileName ) );
                trgMfs.write( fileName.substring( resourcesRoot.length() - 1 ),
                              bytes,
                              true );
            }
        }
    }

    private void addMetaInfBuilder() {
        String resourcesRoot = "src/main/resources/";
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.startsWith( resourcesRoot ) ) {
                byte[] bytes = srcMfs.getBytes( fileName );
                trgMfs.write( fileName.substring( resourcesRoot.length() - 1 ),
                              bytes,
                              true );
            }
        }
    }

    private boolean filterFileInKBase(KieBaseModel kieBase,
                                      String fileName) {
        if ( !isKieExtension( fileName ) ) {
            return false;
        }
        if ( ((KieBaseModelImpl) kieBase).isDefault() ) {
            return true;
        }
        if ( kieBase.getPackages().isEmpty() ) {
            return isFileInKiePackage( fileName,
                                       kieBase.getName() );
        }
        for ( String pkg : kieBase.getPackages() ) {
            if ( isFileInKiePackage( fileName,
                                     pkg ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isFileInKiePackage(String fileName,
                                       String pkgName) {
        String pathName = pkgName.replace( '.',
                                           '/' );
        return (fileName.startsWith( "src/main/resources/" + pathName + "/" ) || fileName.contains( "/" + pathName + "/" ));
    }

    private boolean isKieExtension(String fileName) {
        return fileName.endsWith( ResourceType.DRL.getDefaultExtension() ) ||
               fileName.endsWith( ResourceType.BPMN2.getDefaultExtension() );
    }

    public boolean hasResults(Level... levels) {
        if ( !isBuilt() ) {
            build();
        }
        return !MessageImpl.filterMessages( messages,
                                            levels ).isEmpty();
    }

    public Results getResults(Level... levels) {
        if ( !isBuilt() ) {
            build();
        }
        return new ResultsImpl( MessageImpl.filterMessages( messages,
                                                            levels ),
                                null );
    }

    public Results getResults() {
        if ( !isBuilt() ) {
            build();
        }
        return new ResultsImpl( messages,
                                null );
    }

    public KieModule getKieJar() {
        if ( !isBuilt() ) {
            build();
        }
        if ( hasResults( Level.ERROR ) || kieJar == null ) {
            throw new RuntimeException( "Unable to get KieJar, Errors Existed" );
        }
        return kieJar;
    }

    private boolean isBuilt() {
        return kieJar != null;
    }

    private void buildKieProject() {
        if ( srcMfs.isAvailable( KieModuleModelImpl.KPROJECT_SRC_PATH ) ) {
            kieProjectXml = srcMfs.getBytes( KieModuleModelImpl.KPROJECT_SRC_PATH );
            try {
                kieProject = KieModuleModelImpl.fromXML( new ByteArrayInputStream( kieProjectXml ) );
            } catch ( Exception e ) {
                messages.add( new MessageImpl( idGenerator++,
                                               Level.ERROR,
                                               "kproject.xml",
                                               "kproject.xml found, but unable to read\n" + e.getMessage() ) );
            }
        } else {
            KieFactory kf = KieFactory.Factory.get();
            kieProject = kf.newKieProject();

            ((KieModuleModelImpl) kieProject).newDefaultKieBaseModel();
            kieProjectXml = kieProject.toXML().getBytes();
        }
    }

    public void buildPomModel() {
        pomXml = getOrGeneratePomXml( srcMfs );
        if ( pomXml == null ) {
            // will be null if the provided pom is invalid
            return;
        }

        try {
            PomModel tempPomModel = MinimalPomParser.parse( "pom.xml",
                                                            new ByteArrayInputStream( pomXml ) );
            validatePomModel( tempPomModel ); // throws an exception if invalid
            pomModel = tempPomModel;
        } catch ( Exception e ) {
            messages.add( new MessageImpl( idGenerator++,
                                           Level.ERROR,
                                           "pom.xml",
                                           "maven pom.xml found, but unable to read\n" + e.getMessage() ) );
        }
    }

    public static void validatePomModel(PomModel pomModel) {
        if ( StringUtils.isEmpty( pomModel.getGroupId() ) || StringUtils.isEmpty( pomModel.getArtifactId() ) || StringUtils.isEmpty( pomModel.getVersion() ) ) {
            throw new RuntimeException( "Maven pom.properties exists but GAV content is malformed" );
        }
    }

    public static byte[] getOrGeneratePomXml(ResourceReader mfs) {
        if ( mfs.isAvailable( "pom.xml" ) ) {
            return mfs.getBytes( "pom.xml" );
        } else {
            // There is no pom.xml, and thus no GAV, so generate a pom.xml from the global detault.
            return generatePomXml( KieServices.Factory.get().getKieRepository().getDefaultGAV() ).getBytes();
        }
    }

    public void writePomAndKProject() {
        addMetaInfBuilder();

        if ( pomXml != null ) {
            GAVImpl g = (GAVImpl) gav;
            trgMfs.write( g.getPomXmlPath(),
                          pomXml,
                          true );
            trgMfs.write( g.getPomPropertiesPath(),
                          generatePomProperties( gav ).getBytes(),
                          true );

        }

        if ( kieProjectXml != null ) {
            trgMfs.write( KieModuleModelImpl.KPROJECT_JAR_PATH,
                          kieProject.toXML().getBytes(),
                          true );
        }
    }

    public static String generatePomXml(GAV gav) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" );
        sBuilder.append( "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n" );
        sBuilder.append( "    <modelVersion>4.0.0</modelVersion> \n" );

        sBuilder.append( "    <groupId>" );
        sBuilder.append( gav.getGroupId() );
        sBuilder.append( "</groupId> \n" );

        sBuilder.append( "    <artifactId>" );
        sBuilder.append( gav.getArtifactId() );
        sBuilder.append( "</artifactId> \n" );

        sBuilder.append( "    <version>" );
        sBuilder.append( gav.getVersion() );
        sBuilder.append( "</version> \n" );

        sBuilder.append( "    <packaging>jar</packaging> \n" );

        sBuilder.append( "    <name>Default</name> \n" );
        sBuilder.append( "</project>  \n" );

        return sBuilder.toString();
    }

    public static String generatePomProperties(GAV gav) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append( "groupId=" );
        sBuilder.append( gav.getGroupId() );
        sBuilder.append( "\n" );

        sBuilder.append( "artifactId=" );
        sBuilder.append( gav.getArtifactId() );
        sBuilder.append( "\n" );

        sBuilder.append( "version=" );
        sBuilder.append( gav.getVersion() );
        sBuilder.append( "\n" );

        return sBuilder.toString();
    }

    private ClassLoader compileJavaClasses() {
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
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.endsWith( ".java" ) && !classFiles.contains( fileName.substring( 0,
                                                                                           fileName.length() - ".java".length() ) ) ) {
                javaFiles.add( fileName );
            }
        }
        if ( javaFiles.isEmpty() ) {
            return getCompositeClassLoader();
        }

        String[] sourceFiles = javaFiles.toArray( new String[javaFiles.size()] );

        EclipseJavaCompiler compiler = createCompiler( "src/main/java/" );
        CompilationResult res = compiler.compile( sourceFiles,
                                                  srcMfs,
                                                  trgMfs );

        for ( CompilationProblem problem : res.getErrors() ) {
            messages.add( new MessageImpl( idGenerator++,
                                           problem ) );
        }
        for ( CompilationProblem problem : res.getWarnings() ) {
            messages.add( new MessageImpl( idGenerator++,
                                           problem ) );
        }

        return res.getErrors().length == 0 ? getCompositeClassLoader() : getClass().getClassLoader();
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
        return null;
    }

    private CompositeClassLoader getCompositeClassLoader() {
        CompositeClassLoader ccl = ClassLoaderUtil.getClassLoader( null,
                                                                   getClass(),
                                                                   true );
        ccl.addClassLoader( new ClassUtils.MapClassLoader( trgMfs.getMap(),
                                                           ccl ) );
        return ccl;
    }

    private EclipseJavaCompiler createCompiler(String prefix) {
        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        return new EclipseJavaCompiler( settings,
                                        prefix );
    }

}
