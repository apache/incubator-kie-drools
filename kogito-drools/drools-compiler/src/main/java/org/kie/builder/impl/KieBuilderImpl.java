package org.kie.builder.impl;

import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.KieServices;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.Message;
import org.kie.builder.Messages;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieBase;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KieBuilderImpl implements KieBuilder {

    private final KieFileSystem kieFileSystem;
    private final MemoryFileSystem mfs;
    private final KieProject kieProject;

    private final List<Message> messages = new ArrayList<Message>();

    private long idGenerator = 1L;

    private MemoryKieJar kieJar;

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        this.kieFileSystem = kieFileSystem;
        mfs = ((KieFileSystemImpl)kieFileSystem).asMemoryFileSystem();
        kieProject = getKieProject();
    }

    public List<Message> build() {
        if (!isBuilt()) {
            kieJar = new MemoryKieJar(kieProject, mfs);
            compileJavaClasses();
            compileKieFiles();
            KieServices.Factory.get().getKieRepository().addKieJar(kieJar);
        }
        return messages;
    }

    private void compileKieFiles() {
        for (KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values()) {
            KieBase kieBase = buildKieBase(kieBaseModel);
            if (kieBase != null) {
                kieJar.addKieBase(kieBaseModel.getName(), kieBase);
            }
        }
    }

    public KieBase buildKieBase(KieBaseModel kieBase) {
        KnowledgeBuilderConfiguration kConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, getClass().getClassLoader());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kConf);
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();
        addKBaseFileToBuilder(ckbuilder, kieBase);
        if (kieBase.getIncludes() != null) {
            for ( String include : kieBase.getIncludes() ) {
                addKBaseFileToBuilder(ckbuilder, kieProject.getKieBaseModels().get(include));
            }
        }
        ckbuilder.build();

        if (kbuilder.hasErrors()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                messages.add(new MessageImpl(idGenerator++, error));
            }
            return null;
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(getKnowledgeBaseConfiguration(kieBase, null, getClass().getClassLoader()));
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return (KieBase) knowledgeBase;
    }

    private KnowledgeBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModel kieBase, Properties properties, ClassLoader... classLoaders) {
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(properties, classLoaders);
        kbConf.setOption(kieBase.getEqualsBehavior());
        kbConf.setOption(kieBase.getEventProcessingMode());
        return kbConf;
    }

    private void addKBaseFileToBuilder(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBase) {
        for (String fileName : mfs.getFileNames()) {
            if (filterFileInKBase(kieBase.getName(), fileName)) {
                ckbuilder.add( ResourceFactory.newByteArrayResource(mfs.getBytes(fileName)), ResourceType.determineResourceType(fileName) );
            }
        }
    }

    private boolean filterFileInKBase(String kBaseName, String fileName) {
        return (fileName.startsWith(kBaseName + "/") || fileName.contains("/" + kBaseName + "/")) &&
               (fileName.endsWith(ResourceType.DRL.getDefaultExtension()) || fileName.endsWith(ResourceType.BPMN2.getDefaultExtension()));
    }

    public boolean hasMessages() {
        build();
        return !messages.isEmpty();
    }

    public Messages getMessages() {
        build();
        return new MessagesImpl(messages);
    }

    public KieJar getKieJar() {
        build();
        return kieJar;
    }

    private boolean isBuilt() {
        return kieJar != null;
    }

    private KieProject getKieProject() {
        byte[] bytes = kieFileSystem.read(KieContainer.KPROJECT_RELATIVE_PATH);
        return KieProjectImpl.fromXML(new ByteArrayInputStream(bytes));
    }

    private void compileJavaClasses() {
        List<String> javaFiles = new ArrayList<String>();
        for (String fileName : mfs.getFileNames()) {
            if (fileName.endsWith(".java")) {
                javaFiles.add(fileName);
            }
        }
        if (javaFiles.isEmpty()) {
            return;
        }

        String[] sourceFiles = javaFiles.toArray(new String[javaFiles.size()]);

        EclipseJavaCompiler compiler = createCompiler();
        CompilationResult res = compiler.compile( sourceFiles, mfs, mfs );

        for (CompilationProblem problem : res.getErrors()) {
            messages.add(new MessageImpl(idGenerator++, problem));
        }
        for (CompilationProblem problem : res.getWarnings()) {
            messages.add(new MessageImpl(idGenerator++, problem));
        }
    }

    private EclipseJavaCompiler createCompiler() {
        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        return new EclipseJavaCompiler( settings );
    }

}
