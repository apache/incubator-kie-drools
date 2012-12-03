package org.kie.builder.impl;

import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.DiskResourceReader;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.StringUtils;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.xml.MinimalPomParser;
import org.drools.xml.PomModel;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieServices;
import org.kie.builder.Message.Level;
import org.kie.builder.ResourceType;
import org.kie.builder.Results;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KieBuilderImpl
    implements
    KieBuilder {

    private static final String RESOURCES_ROOT = "src/main/resources/";

    private Messages             messages;
    private final ResourceReader srcMfs;

    private MemoryFileSystem     trgMfs;

    private MemoryKieModules         kieModule;

    private PomModel             pomModel;
    private byte[]               pomXml;
    private GAV                  gav;

    private byte[]               kieProjectXml;
    private KieModuleModel      kieProject;
    
    private Collection<KieModule>   dependencies;

    public KieBuilderImpl(File file) {
        this.srcMfs = new DiskResourceReader( file );
        init();
    }

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        srcMfs = ((KieFileSystemImpl) kieFileSystem).asMemoryFileSystem();
        init();
    }

    public KieBuilder setDependencies(Collection<KieModule> dependencies) {
        this.dependencies = dependencies;
        return this;
    }
    
    
    private void init() {
        KieFactory kf = KieFactory.Factory.get();

        messages = new Messages();

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

            compileJavaClasses();
            addKBasesFilesToTrg();

            kieModule = new MemoryKieModules( gav,
                                              kieProject,
                                              trgMfs );

            if ( dependencies != null && !dependencies.isEmpty() ) {
                Map<GAV, InternalKieModule> modules = new HashMap<GAV, InternalKieModule>();
                for ( KieModule kieModule : dependencies ) {
                    modules.put( kieModule.getGAV(),
                                 (InternalKieModule) kieModule );
                }
                kieModule.setDependencies( modules );
            }

            kieModule.verify( messages );

            if ( !hasResults( Level.ERROR ) ) {
                KieServices.Factory.get().getKieRepository().addKieModule( kieModule );
            }
        }
        return new ResultsImpl( messages.getMessages(),
                                null );
    }

    private void addKBasesFilesToTrg() {
        for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
            addKBaseFilesToTrg( kieBaseModel );            
        }
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

    private void addKBaseFilesToTrg(KieBaseModel kieBase) {
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( filterFileInKBase( kieBase,
                                    fileName ) ) {
                byte[] bytes = srcMfs.getBytes( fileName );
                trgMfs.write( fileName.substring( RESOURCES_ROOT.length() - 1 ),
                              bytes,
                              true );
            }
        }
    }

    private void addMetaInfBuilder() {
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.startsWith( RESOURCES_ROOT ) && !isKieExtension(fileName) ) {
                byte[] bytes = srcMfs.getBytes( fileName );
                trgMfs.write( fileName.substring( RESOURCES_ROOT.length() - 1 ),
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
        return (fileName.startsWith( RESOURCES_ROOT + pathName + "/" ) || fileName.contains( "/" + pathName + "/" ));
    }

    static boolean isKieExtension(String fileName) {
        return fileName.endsWith( ResourceType.DRL.getDefaultExtension() ) ||
               fileName.endsWith( ResourceType.BPMN2.getDefaultExtension() );
    }

    public boolean hasResults(Level... levels) {
        if ( !isBuilt() ) {
            build();
        }
        return !MessageImpl.filterMessages( messages.getMessages(),
                                            levels ).isEmpty();
    }

    public Results getResults(Level... levels) {
        if ( !isBuilt() ) {
            build();
        }
        return new ResultsImpl( MessageImpl.filterMessages( messages.getMessages(),
                                                            levels ),
                                null );
    }

    public Results getResults() {
        if ( !isBuilt() ) {
            build();
        }
        return new ResultsImpl( messages.getMessages(),
                                null );
    }

    public KieModule getKieModule() {
        if ( !isBuilt() ) {
            build();
        }
        if ( hasResults( Level.ERROR ) || kieModule == null ) {
            throw new RuntimeException( "Unable to get KieModule, Errors Existed" );
        }
        return kieModule;
    }

    private boolean isBuilt() {
        return kieModule != null;
    }

    private void buildKieProject() {
        if ( srcMfs.isAvailable( KieModuleModelImpl.KPROJECT_SRC_PATH ) ) {
            kieProjectXml = srcMfs.getBytes( KieModuleModelImpl.KPROJECT_SRC_PATH );
            try {
                kieProject = KieModuleModelImpl.fromXML( new ByteArrayInputStream( kieProjectXml ) );
            } catch ( Exception e ) {
                messages.addMessage(  Level.ERROR,
                                      "kproject.xml",
                                      "kproject.xml found, but unable to read\n" + e.getMessage() );
            }
        } else {
            KieFactory kf = KieFactory.Factory.get();
            kieProject = kf.newKieModuleModel();

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
            messages.addMessage( Level.ERROR,
                                 "pom.xml",
                                 "maven pom.xml found, but unable to read\n" + e.getMessage() );
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

    private void compileJavaClasses() {
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
            return;
        }

        String[] sourceFiles = javaFiles.toArray( new String[javaFiles.size()] );

        EclipseJavaCompiler compiler = createCompiler( "src/main/java/" );
        CompilationResult res = compiler.compile( sourceFiles,
                                                  srcMfs,
                                                  trgMfs );

        for ( CompilationProblem problem : res.getErrors() ) {
            messages.addMessage(  problem );
        }
        for ( CompilationProblem problem : res.getWarnings() ) {
            messages.addMessage( problem );
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

    private EclipseJavaCompiler createCompiler(String prefix) {
        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        return new EclipseJavaCompiler( settings,
                                        prefix );
    }
}
