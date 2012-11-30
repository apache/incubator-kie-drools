package org.kie.builder.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.drools.builder.xml.PomModelParserTest;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.DiskResourceReader;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.kproject.GroupArtifactVersion;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.memory.MemoryFileSystem;
import org.drools.xml.MinimalPomParser;
import org.drools.xml.PomModel;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.Message;
import org.kie.builder.Message.Level;
import org.kie.builder.Messages;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieBase;
import org.kie.util.ClassLoaderUtil;
import org.kie.util.CompositeClassLoader;

import sun.io.MalformedInputException;

public class KieBuilderImpl
    implements
    KieBuilder {

    private final ResourceReader srcMfs;

    private MemoryFileSystem     trgMfs;

    private List<Message>        messages;

    private long                 idGenerator = 1L;

    private MemoryKieJar         kieJar;

    private ClassLoader          classLoader;

    private KieProject           kieProject;
    private byte[]               pomXml;
    private GAV                  gav;
    
    private boolean invalidKieProject;
    private boolean invalidPomXml;

    public KieBuilderImpl(File file) {
        this.srcMfs = new DiskResourceReader( file );
        init();
    }

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        srcMfs = ((KieFileSystemImpl) kieFileSystem).asMemoryFileSystem();
        init();
    }

    public void init() {
        messages   = new ArrayList<Message>();
        kieProject = getKieProject();
        gav = getGAV();
    }

    private GAV getGAV() {
        try {
            PomModel pomModel = getPomModel();
            if ( pomModel == null ) {
                return null;
            }
            
            if ( StringUtils.isEmpty( pomModel.getGroupId()  ) || StringUtils.isEmpty( pomModel.getArtifactId() ) || StringUtils.isEmpty(  pomModel.getVersion()  ) ) {
                throw new MalformedInputException("Maven pom.properties exists but content malformed");          
            }

            KieFactory kf = KieFactory.Factory.get();
            return kf.newGav( pomModel.getGroupId(), pomModel.getArtifactId(), pomModel.getVersion() );            
        } catch ( Exception e ) {
            invalidPomXml = true;
            messages.add( new MessageImpl( idGenerator++,
                                           Level.ERROR,
                                           "pom.xml",
                                           "maven pom.xml found, but unable to read\n" + e.getMessage() ) );
        }
        return null;
    }

    public Messages build() {
        if ( !isBuilt() ) {
            trgMfs = new MemoryFileSystem();
            kieJar = new MemoryKieJar( kieProject,
                                       trgMfs );
            writePomAndKProject();
            compileJavaClasses();
            compileKieFiles();
            if ( !hasMessages( Level.ERROR ) ) {
                KieServices.Factory.get().getKieRepository().addKieJar( kieJar );
            }
        }
        return new MessagesImpl( messages,
                                 null );
    }

    private void compileKieFiles() {
        for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
            KieBase kieBase = buildKieBase( kieBaseModel );
            if ( kieBase != null ) {
                kieJar.addKieBase( kieBaseModel.getName(),
                                   kieBase );
            }
        }
    }

    public KieBase buildKieBase(KieBaseModel kieBase) {
        KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null,
                                                                                                        classLoader );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kConf );
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();
        addKBaseFileToBuilder( ckbuilder,
                               kieBase );
        if ( kieBase.getIncludes() != null ) {
            for ( String include : kieBase.getIncludes() ) {
                addKBaseFileToBuilder( ckbuilder,
                                       kieProject.getKieBaseModels().get( include ) );
            }
        }
        ckbuilder.build();

        if ( kbuilder.hasErrors() ) {
            for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
                messages.add( new MessageImpl( idGenerator++,
                                               error ) );
            }
            return null;
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration( kieBase,
                                                                                                            null,
                                                                                                            classLoader ) );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return (KieBase) knowledgeBase;
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

    private void addKBaseFileToBuilder(CompositeKnowledgeBuilder ckbuilder,
                                       KieBaseModel kieBase) {
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( filterFileInKBase( kieBase.getName(),
                                    fileName ) ) {
                ckbuilder.add( ResourceFactory.newByteArrayResource( srcMfs.getBytes( fileName ) ),
                               ResourceType.determineResourceType( fileName ) );
            }
        }
    }

    private boolean filterFileInKBase(String kBaseName,
                                      String fileName) {
        String pathName = kBaseName.replace( '.',
                                             '/' );
        return (fileName.startsWith( "src/main/resoureces/" + pathName + "/" ) || fileName.contains( "/" + pathName + "/" )) &&
               (fileName.endsWith( ResourceType.DRL.getDefaultExtension() ) || fileName.endsWith( ResourceType.BPMN2.getDefaultExtension() ));
    }

    public boolean hasMessages(Level... levels) {
        build();
        return !MessageImpl.filterMessages( messages,
                                            levels ).isEmpty();
    }

    public Messages getMessages(Level... levels) {
        build();
        return new MessagesImpl( MessageImpl.filterMessages( messages,
                                                             levels ),
                                 null );
    }

    public Messages getMessages() {
        build();
        return new MessagesImpl( messages,
                                 null );
    }

    public KieJar getKieJar() {
        build();
        if ( hasMessages( Level.ERROR ) || kieJar == null ) {
            throw new RuntimeException( "Unable to get KieJar, Errors Existed" );
        }
        return kieJar;
    }

    private boolean isBuilt() {
        return kieJar != null;
    }

    private KieProject getKieProject() {
        byte[] bytes = srcMfs.getBytes( KieContainer.KPROJECT_RELATIVE_PATH );
        
        if ( bytes == null ) {
            bytes = srcMfs.getBytes( KieContainer.KPROJECT_JAR_PATH );
        }
        
        
        if ( bytes != null ) {
            try {
                return KieProjectImpl.fromXML( new ByteArrayInputStream( bytes ) );
            } catch ( Exception e) {
                invalidKieProject = true;  
                messages.add( new MessageImpl( idGenerator++,
                                               Level.ERROR,
                                               "kproject.xml",
                                               "kproject.xml found, but unable to read\n" + e.getMessage() ) );                
            }
        }
        
        return null;
    }
    
    public PomModel getPomModel() {
        pomXml = srcMfs.getBytes( "pom.xml" );       
        if ( pomXml == null) {
            return null;
        }
        
        PomModel pomModel = MinimalPomParser.parse( "pom.xml", new ByteArrayInputStream( pomXml ) );
        return pomModel;
    }
    
    public void writePomAndKProject() {
        KieFactory kf = KieFactory.Factory.get();
        KieRepository kr = KieServices.Factory.get().getKieRepository();
        gav = kr.getDefaultGAV();
        
        if ( !invalidPomXml ) {
            if ( pomXml == null  ) {
                String xml =  generatePomXml(gav);
                trgMfs.write( ((GroupArtifactVersion)gav).toJarPath() + "/pom.xml", xml.getBytes(), true );
            } else {
                trgMfs.write( ((GroupArtifactVersion)gav).toJarPath() + "/pom.xml", srcMfs.getBytes( "pom.xml" ), true );
            }
            String props = generatePomProperties(gav);
            trgMfs.write( ((GroupArtifactVersion)gav).toJarPath() + "/pom.properties", props.getBytes(), true );
        }
        
        if ( !invalidKieProject ) {
            if ( kieProject == null  ) {
                kieProject = kf.newKieProject();
                kieProject.newKieBaseModel( gav.getGroupId() + "." + gav.getArtifactId() );           
            }
            trgMfs.write( "META-INF/kproject.xml", kieProject.toXML().getBytes(), true );            
        }        
    }
   
   public String generatePomXml(GAV gav) {
       StringBuilder sBuilder = new StringBuilder();
       sBuilder.append( "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" );
       sBuilder.append( "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n" );
       sBuilder.append( "    <modelVersion>4.0.0</modelVersion> \n");

       sBuilder.append( "    <groupId>" );
       sBuilder.append( gav.getGroupId() );
       sBuilder.append( "</groupId> \n");

       sBuilder.append( "    <artifactId>" );
       sBuilder.append( gav.getGroupId() );
       sBuilder.append( "</artifactId> \n");
       
       sBuilder.append( "    <version>" );
       sBuilder.append( gav.getGroupId() );
       sBuilder.append( "</version> \n");
       
       sBuilder.append( "    <packaging>jar</packaging> \n");

       sBuilder.append( "    <name>Default</name> \n");
       sBuilder.append( "</project>  \n");      
       
       return sBuilder.toString();
   }
   
   public String generatePomProperties(GAV gav) {
       StringBuilder sBuilder = new StringBuilder();
       sBuilder.append( "groupId=" );
       sBuilder.append( gav.getGroupId() );
       sBuilder.append( "\n");

       sBuilder.append( "artifactId=" );
       sBuilder.append( gav.getGroupId() );
       sBuilder.append( "\n");
       
       sBuilder.append( "version=" );
       sBuilder.append( gav.getGroupId() );
       sBuilder.append( "\n");
       
       return sBuilder.toString();
   }
    
    private void compileJavaClasses() {
        List<String> javaFiles = new ArrayList<String>();
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( fileName.endsWith( ".java" ) ) {
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
            messages.add( new MessageImpl( idGenerator++,
                                           problem ) );
        }
        for ( CompilationProblem problem : res.getWarnings() ) {
            messages.add( new MessageImpl( idGenerator++,
                                           problem ) );
        }

        if ( res.getErrors().length == 0 ) {
            CompositeClassLoader ccl = ClassLoaderUtil.getClassLoader( null,
                                                                       getClass(),
                                                                       true );
            ccl.addClassLoader( new ClassUtils.MapClassLoader( trgMfs.getMap(),
                                                               ccl ) );
            this.classLoader = ccl;
        }
    }

    private EclipseJavaCompiler createCompiler(String prefix) {
        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        return new EclipseJavaCompiler( settings,
                                        prefix );
    }

}
