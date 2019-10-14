package org.drools.modelcompiler.builder;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.api.KieBase;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.KieRuntimeBuilder;

public class ProjectSourceClass {

    final KieModuleModelMethod modelMethod;
    private String dependencyInjection = "";

    public ProjectSourceClass(KieModuleModelMethod modelMethod) {
        this.modelMethod = modelMethod;
    }

    public ProjectSourceClass withDependencyInjection(String dependencyInjection) {
        this.dependencyInjection = dependencyInjection;
        return this;
    }
    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "package org.drools.project.model;\n" +
                        "\n" +
                        "import " + KieBase.class.getCanonicalName()  + ";\n" +
                        "import " + KieBaseModel.class.getCanonicalName()  + ";\n" +
                        "import " + KieSession.class.getCanonicalName()  + ";\n" +
                        "\n" +
                        dependencyInjection + "\n"+
                        "public class ProjectRuntime implements " + KieRuntimeBuilder.class.getCanonicalName() + " {\n" +
                        "\n");
        sb.append(modelMethod.getConstructor());
        sb.append("\n");
        sb.append(modelMethod.toGetKieBaseMethods());
        sb.append("\n");
        sb.append(modelMethod.toNewKieSessionMethods());
        sb.append("\n");
        sb.append(modelMethod.toGetKieBaseForSessionMethod());
        sb.append("\n");
        sb.append(modelMethod.toKieSessionConfMethod());
        sb.append("\n}" );
        return sb.toString();
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(getName(), generate().getBytes());
    }

    public String getName() {
        return CanonicalModelKieProject.PROJECT_RUNTIME_SOURCE;
    }
}
