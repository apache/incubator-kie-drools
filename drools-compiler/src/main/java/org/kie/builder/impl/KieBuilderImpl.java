package org.kie.builder.impl;

import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.DiskResourceReader;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.compiler.PackageRegistry;
import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.StringUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.kproject.ReleaseIdImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.TypeMetaInfo;
import org.drools.kproject.xml.MinimalPomParser;
import org.drools.kproject.xml.PomModel;
import org.kie.KieBaseConfiguration;
import org.kie.KieServices;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.InternalKieBuilder;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieBuilderSet;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModule;
import org.kie.builder.Message.Level;
import org.kie.builder.ReleaseId;
import org.kie.builder.Results;
import org.kie.builder.model.KieBaseModel;
import org.kie.builder.model.KieModuleModel;
import org.kie.builder.model.KieSessionModel;
import org.kie.definition.KiePackage;
import org.kie.definition.type.FactType;
import org.kie.io.Resource;
import org.kie.io.ResourceType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    InternalKieBuilder {

    static final String   RESOURCES_ROOT = "src/main/resources/";

    private ResultsImpl           results;
    private final ResourceReader  srcMfs;

    private MemoryFileSystem      trgMfs;

    private MemoryKieModule       kModule;

    private PomModel              pomModel;
    private byte[]                pomXml;
    private ReleaseId releaseId;

    private byte[]                kModuleModelXml;
    private KieModuleModel        kModuleModel;

    private Collection<KieModule> dependencies;

    public KieBuilderImpl(File file) {
        this.srcMfs = new DiskResourceReader( file );
        init();
    }

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        srcMfs = ((KieFileSystemImpl) kieFileSystem).asMemoryFileSystem();
        init();
    }

    public KieBuilder setDependencies(KieModule... dependencies) {        
        this.dependencies = Arrays.asList( dependencies );
        return this;
    }
    
    public KieBuilder setDependencies(Resource... resources) {
        KieRepositoryImpl kr = ( KieRepositoryImpl ) KieServices.Factory.get().getRepository();
        List<KieModule> list = new ArrayList<KieModule>();
        for ( Resource res : resources ) {
            InternalKieModule depKieMod = ( InternalKieModule ) kr.getKieModule( res );
            list.add( depKieMod);
        }
        this.dependencies = list;
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
            KieRepositoryImpl repository = (KieRepositoryImpl)ks.getRepository();
            for (ReleaseId dep : pomModel.getDependencies()) {
                KieModule depModule = repository.getKieModule(dep, pomXml);
                if (depModule != null) {
                    addDependency(depModule);
                }
            }
        }
    }

    private void addDependency(KieModule depModule) {
        if (dependencies == null) {
            dependencies = new ArrayList<KieModule>();
        }
        dependencies.add(depModule);
    }

    public KieBuilder buildAll() {
        // releaseId and kModule will be null if a provided pom.xml or kmodule.xml is invalid
        if ( !isBuilt() && releaseId != null && kModuleModel != null ) {
            trgMfs = new MemoryFileSystem();
            writePomAndKModule();

            compileJavaClasses();
            addKBasesFilesToTrg();

            kModule = new MemoryKieModule( releaseId,
                                           kModuleModel,
                                           trgMfs );

            if ( dependencies != null && !dependencies.isEmpty() ) {
                for ( KieModule kieModule : dependencies ) {
                    kModule.addDependency( (InternalKieModule) kieModule );
                }
            }

            if ( buildKieModule(kModule, results) ) {
                Map<String, TypeDeclaration> typeDeclarations = addTypeDeclarationClassesToTrg();
                writeTypeMetaInfosToTrg(typeDeclarations);
            }
        }
        return this;
    }

    private Map<String, TypeDeclaration> addTypeDeclarationClassesToTrg() {
        Map<String, TypeDeclaration> typeDeclarations = new HashMap<String, TypeDeclaration>();
        KieModuleModel kieModuleModel = kModule.getKieModuleModel();
        for (String kieBaseNames : kieModuleModel.getKieBaseModels().keySet()) {
            KnowledgeBuilderImpl kBuilder = (KnowledgeBuilderImpl)kModule.getKnowledgeBuilderForKieBase(kieBaseNames);
            Map<String, PackageRegistry> pkgRegistryMap = kBuilder.getPackageBuilder().getPackageRegistry();

            for (KiePackage kPkg : kBuilder.getKnowledgePackages()) {
                PackageRegistry pkgRegistry = pkgRegistryMap.get(kPkg.getName());
                JavaDialectRuntimeData runtimeData = (JavaDialectRuntimeData)pkgRegistry.getDialectRuntimeRegistry().getDialectData("java");

                for (FactType factType : kPkg.getFactTypes()) {
                    Class<?> typeClass = ((ClassDefinition) factType).getDefinedClass();
                    TypeDeclaration typeDeclaration = pkgRegistry.getPackage().getTypeDeclaration(typeClass);
                    if (typeDeclaration != null) {
                        typeDeclarations.put(typeClass.getName(), typeDeclaration);
                    }

                    String className = factType.getName();
                    String internalName = className.replace('.', '/') + ".class";
                    byte[] bytes = runtimeData.getStore().get(internalName);
                    trgMfs.write( internalName, bytes, true );
                }
            }
        }
        return typeDeclarations;
    }

    private void writeTypeMetaInfosToTrg(Map<String, TypeDeclaration> typeDeclarations) {
        if (!typeDeclarations.isEmpty()) {
            trgMfs.write( KieModuleModelImpl.KMODULE_INFO_JAR_PATH,
                          TypeMetaInfo.marshallMetaInfos(typeDeclarations).getBytes(),
                          true );
        }
    }

    public static boolean buildKieModule(InternalKieModule kModule, ResultsImpl messages) {
        KieModuleKieProject kProject = new KieModuleKieProject( kModule, null );
        kProject.init();
        kProject.verify(messages);

        if ( messages.filterMessages( Level.ERROR ).isEmpty()) {
            KieServices.Factory.get().getRepository().addKieModule( kModule );
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
        return kbConf;
    }

    private void addKBaseFilesToTrg(KieBaseModel kieBase) {
        for ( String fileName : srcMfs.getFileNames() ) {
            if ( filterFileInKBase(kieBase, fileName) ) {
                copySourceToTarget(fileName);
            }
        }
    }

    void copySourceToTarget(String fileName) {
        byte[] bytes = srcMfs.getBytes( fileName );
        if (bytes != null) {
            trgMfs.write( fileName.substring( RESOURCES_ROOT.length() - 1 ), bytes, true );
        } else {
            trgMfs.remove( fileName.substring( RESOURCES_ROOT.length() - 1 ) );
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

    static boolean filterFileInKBase(KieBaseModel kieBase,
                                      String fileName) {
        if ( !isKieExtension( fileName ) ) {
            return false;
        }
        if ( kieBase.getPackages().isEmpty() ) {
            return isFileInKieBase( fileName, kieBase.getName() );
        }
        return isFileInKiePackages(fileName, kieBase.getPackages());
    }

    private static boolean isFileInKieBase(String fileName, String kBaseName) {
        String pathName = kBaseName.replace( '.', '/' );
        return fileName.startsWith( RESOURCES_ROOT + pathName + "/" ) || fileName.startsWith(pathName + "/");
    }

    private static boolean isFileInKiePackages(String fileName, List<String> pkgNames) {
        int lastSep = fileName.lastIndexOf("/");
        String pkgNameForFile = lastSep > 0 ? fileName.substring(0, lastSep) : fileName;
        pkgNameForFile = pkgNameForFile.replace('/', '.');
        for (String pkgName : pkgNames) {
            boolean isNegative = pkgName.startsWith("!");
            if (isNegative) {
                pkgName = pkgName.substring(1);
            }
            if (pkgName.equals("*") || pkgNameForFile.endsWith(pkgName) ||
                    (pkgName.endsWith(".*") && pkgNameForFile.contains(pkgName.substring(0, pkgName.length()-2))) ) {
                return !isNegative;
            }
        }
        return false;
    }

    static boolean isKieExtension(String fileName) {
        return !fileName.endsWith( ".java" ) && ResourceType.determineResourceType( fileName ) != null;
    }

    public Results getResults() {
        if ( !isBuilt() ) {
            buildAll();
        }
        return results;
    }

    public KieModule getKieModule() {
        return getKieModule(false);
    }

    KieModule getKieModuleIgnoringErrors() {
        return getKieModule(true);
    }

    private KieModule getKieModule(boolean ignoreErrors) {
        if ( !isBuilt() ) {
            buildAll();
        }

        if ( !ignoreErrors && ( getResults().hasMessages(Level.ERROR) || kModule == null ) ) {
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
                results.addMessage(  Level.ERROR,
                                     "kmodule.xml",
                                     "kmodulet.xml found, but unable to read\n" + e.getMessage() );
            }
        } else {
            // There's no kmodule.xml, create a defualt one
            kModuleModel = KieServices.Factory.get().newKieModuleModel();
        }

        if (setDefaultsforEmptyKieModule(kModuleModel)) {
            kModuleModelXml = kModuleModel.toXML().getBytes();
        }
    }

    static boolean setDefaultsforEmptyKieModule(KieModuleModel kModuleModel) {
        if ( kModuleModel != null && kModuleModel.getKieBaseModels().isEmpty() ) {
            // would be null if they pass a corrupted kModuleModel
            KieBaseModel kieBaseModel = kModuleModel.newKieBaseModel("defaultKieBase").addPackage("*").setDefault(true);
            kieBaseModel.newKieSessionModel("defaultKieSession").setDefault(true);
            kieBaseModel.newKieSessionModel("defaultStatelessKieSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(true);
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
            PomModel tempPomModel = MinimalPomParser.parse( "pom.xml",
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
                          generatePomProperties(releaseId).getBytes(),
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
            results.addMessage(  problem );
        }
        for ( CompilationProblem problem : res.getWarnings() ) {
            results.addMessage( problem );
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

    @Override
    public KieBuilderSet createFileSet(String... files) {
        return new KieBuilderSetImpl(this, files);
    }
}
