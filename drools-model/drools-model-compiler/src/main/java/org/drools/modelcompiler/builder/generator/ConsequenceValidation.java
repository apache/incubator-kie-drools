package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceImpl;
import org.drools.model.functions.ScriptBlock;
import org.drools.model.impl.DeclarationImpl;
import org.drools.modelcompiler.consequence.MVELConsequence;
import org.kie.api.builder.Message;

public class ConsequenceValidation {

    private final String packageName;
    private final String consequenceString;
    private final RuleDescr ruleDescr;
    private final List<Variable> variables = new ArrayList<>();

    private String className;

    public ConsequenceValidation(String packageName, String consequenceString, RuleDescr ruleDescr) {
        this.packageName = packageName;
        this.consequenceString = consequenceString;
        this.ruleDescr = ruleDescr;
    }

    public void addVariable(DeclarationSpec d) {
        variables.add(new DeclarationImpl(d.getDeclarationClass(), d.getBindingId()));
    }

    public static class ByteClassLoader extends ClassLoader {

        private final byte[] classBytes;

        public ByteClassLoader(byte[] classBytes) {
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }
    }

    public void validate(MemoryFileSystem mfs, ResultsImpl messages) {
        Class<?> ruleClass;
        try {
            String replace = className.replace(".", "/");
            MemoryFile file = (MemoryFile) mfs.getFile(replace + ".class");
            byte[] ba = mfs.getFileContents(file);
            ruleClass = new ByteClassLoader(ba).findClass(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<DroolsError> droolsErrors = findErrors(ruleClass);
        for (DroolsError error : droolsErrors) {
            messages.addMessage(Message.Level.ERROR, className, "Mvel expression in error" + error.getMessage());
        }
    }

    private List<DroolsError> findErrors(Class<?> ruleClass) {
        KnowledgePackageImpl pkg = new KnowledgePackageImpl(packageName);
        ScriptBlock block = new ScriptBlock(ruleClass, consequenceString);

        ConsequenceImpl consequence = new ConsequenceImpl(block, variables.toArray(new Variable[0]), true, false, "mvel");
        RuleImpl rule = new RuleImpl(ruleDescr.getName());
        org.drools.modelcompiler.RuleContext context = new org.drools.modelcompiler.RuleContext(null, pkg, rule) {
            @Override
            public Collection<InternalKnowledgePackage> getKnowledgePackages() {
                return Collections.singletonList(pkg);
            }

            @Override
            public ClassLoader getClassLoader() {
                return this.getClass().getClassLoader();
            }
        };

        final List<DroolsError> errors = new ArrayList<>();
        try {
            MVELConsequence mvelConsequence = new MVELConsequence(consequence, context);
            mvelConsequence.init();
        } catch (Exception e) {
            errors.add(new RuleBuildError(null, ruleDescr, null, e.getMessage()));
        }
        return errors;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
