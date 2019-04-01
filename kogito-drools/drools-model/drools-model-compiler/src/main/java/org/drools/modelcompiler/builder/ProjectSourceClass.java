package org.drools.modelcompiler.builder;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.model.Model;
import org.kie.api.KieBase;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieSession;

public class ProjectSourceClass {

    final KieModuleModelMethod modelMethod;

    public ProjectSourceClass(KieModuleModelMethod modelMethod) {
        this.modelMethod = modelMethod;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "package org.drools.project.model;\n" +
                        "\n" +
                        "import " + Model.class.getCanonicalName()  + ";\n" +
                        "import " + KieBase.class.getCanonicalName()  + ";\n" +
                        "import " + KieBaseModel.class.getCanonicalName()  + ";\n" +
                        "import " + KieSession.class.getCanonicalName()  + ";\n" +
                        "\n" +
                        ( hasCdi() ? "@javax.enterprise.context.ApplicationScoped\n" : "" ) +
                        "public class ProjectRuntime implements org.drools.modelcompiler.KieRuntimeBuilder {\n" +
                        "\n");
        sb.append(modelMethod.getConstructor());
        sb.append("\n");
        sb.append(modelMethod.toNewKieSessionMethod());
        sb.append("\n");
        sb.append(modelMethod.toGetKieBaseForSessionMethod());
        sb.append("\n");
        sb.append(modelMethod.toKieSessionConfMethod());
        sb.append("\n}" );
        return sb.toString();
    }

    private boolean hasCdi() {
        return true;
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(CanonicalModelKieProject.PROJECT_RUNTIME_SOURCE, generate().getBytes());
    }
}
