package org.kie.kogito.codegen.rules;

import java.util.Map;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.util.Drools;
import org.drools.model.Model;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.builder.ReleaseId;

import static java.util.stream.Collectors.joining;

public class ModelSourceClass {

    private static final String PROJECT_MODEL_SOURCE = CanonicalKieModule.PROJECT_MODEL_CLASS.replace('.', '/') + ".java";

    private final Map<String, String> modelsByUnit;
    private final KieModuleModelMethod modelMethod;
    private final ReleaseId releaseId;

    public ModelSourceClass(
            ReleaseId releaseId,
            KieModuleModelMethod modelMethod,
            Map<String, String> modelsByUnit) {
        this.releaseId = releaseId;
        this.modelsByUnit = modelsByUnit;
        this.modelMethod = modelMethod;
    }

    public String getName() {
        return PROJECT_MODEL_SOURCE;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "package org.drools.project.model;\n" +
                        "\n" +
                        "import " + Model.class.getCanonicalName()  + ";\n" +
                        "import " + ReleaseId.class.getCanonicalName()  + ";\n" +
                        "import " + ReleaseIdImpl.class.getCanonicalName()  + ";\n" +
                        "\n" +
                        "public class ProjectModel implements org.drools.modelcompiler.CanonicalKieModuleModel {\n" +
                        "\n");

        addGetVersionMethod(sb);
        addGetModelsMethod(sb);
        addGetModelForKieBaseMethod(sb);
        addGetReleaseIdMethod(sb);
        sb.append(modelMethod.toGetKieModuleModelMethod());
        sb.append("\n}" );
        return sb.toString();
    }

    private void addGetVersionMethod(StringBuilder sb) {
        sb.append(
                "    @Override\n" +
                "    public String getVersion() {\n" +
                "        return \"" );
        sb.append( Drools.getFullVersion() );
        sb.append(
                "\";\n" +
                        "    }\n" +
                        "\n");
    }

    private void addGetModelsMethod(StringBuilder sb) {
        sb.append(
                "    @Override\n" +
                "    public java.util.List<Model> getModels() {\n" +
                "        return java.util.Arrays.asList(" );
        sb.append( modelsByUnit.isEmpty() ? "" : modelsByUnit.values().stream().collect( joining("(), new ", "new ", "()") ) );
        sb.append(
                ");\n" +
                "    }\n" +
                "\n");
    }

    private void addGetModelForKieBaseMethod(StringBuilder sb) {
        sb.append(
                "    @Override\n" +
                "    public java.util.List<Model> getModelsForKieBase(String kieBaseName) {\n" +
                "        switch (kieBaseName) {\n"
        );

        for (String kBase : modelMethod.getKieBaseNames()) {
            sb.append( "            case \"" + kBase + "\": " );
            String model = modelsByUnit.get(kBase);
            sb.append( model != null ?
                    "return java.util.Arrays.asList(new " + model + "());\n" :
                    "return getModels();\n" );
        }

        sb.append(
                "        }\n" +
                "        throw new IllegalArgumentException(\"Unknown KieBase: \" + kieBaseName);\n" +
                "    }\n" +
                "\n" );
    }

    private void addGetReleaseIdMethod(StringBuilder sb) {
        sb.append(
                "    @Override\n" +
                "    public ReleaseId getReleaseId() {\n" +
                "        return new ReleaseIdImpl(\"" );
        sb.append( releaseId.getGroupId() ).append( "\", \"" );
        sb.append( releaseId.getArtifactId() ).append( "\", \"" );
        sb.append( releaseId.getVersion() ).append( "\"" );
        sb.append(
                ");\n" +
                        "    }\n");
        sb.append("\n");
    }


}
