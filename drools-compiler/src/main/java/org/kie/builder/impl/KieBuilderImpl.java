package org.kie.builder.impl;

import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.builder.Message;
import org.kie.builder.Messages;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class KieBuilderImpl implements KieBuilder {

    private final KieFileSystem kieFileSystem;
    private final KieProject kieProject;

    private final List<Message> messages = new ArrayList<Message>();

    private long idGenerator = 1L;

    private boolean isBuilt = false;

    public KieBuilderImpl(KieFileSystem kieFileSystem) {
        this.kieFileSystem = kieFileSystem;
        kieProject = getKieProject();
    }

    public List<Message> build() {
        compile();

        // TODO compile DRLs

        isBuilt = true;
        return messages;
    }

    public boolean hasMessages() {
        if (!isBuilt) {
            build();
        }
        return !messages.isEmpty();
    }

    public Messages getMessages() {
        if (!isBuilt) {
            build();
        }
        return new MessagesImpl(messages);
    }

    public KieJar getKieJar() {
        if (!isBuilt) {
            build();
        }
        throw new UnsupportedOperationException("org.kie.builder.impl.KieBuilderImpl.getKieJar -> TODO");
    }

    private KieProject getKieProject() {
        byte[] bytes = kieFileSystem.read(KieContainer.KPROJECT_RELATIVE_PATH);
        return KieProjectImpl.fromXML(new ByteArrayInputStream(bytes));
    }

    private void compile() {
        MemoryFileSystem mfs = ((KieFileSystemImpl)kieFileSystem).asMemoryFileSystem();
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
