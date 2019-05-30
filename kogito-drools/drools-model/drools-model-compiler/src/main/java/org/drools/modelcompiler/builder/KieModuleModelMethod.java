package org.drools.modelcompiler.builder;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.Modifier.publicModifier;
import static com.github.javaparser.ast.NodeList.nodeList;

public class KieModuleModelMethod {

    private static final String KMODULE_MODEL_NAME = "kModuleModel";

    private final String kieModuleModelCanonicalName = KieModuleModel.class.getCanonicalName();
    private final Map<String, KieBaseModel> kBaseModels;

    private BlockStmt stmt = new BlockStmt();

    private final Map<String, String> kSessionForkBase = new HashMap<>();
    private final Map<String, BlockStmt> kBaseConfs = new HashMap<>();
    private final Map<String, BlockStmt> kSessionConfs = new HashMap<>();

    private String defaultKieBaseName;
    private String defaultKieSessionName;
    private String defaultKieStatelessSessionName;

    public KieModuleModelMethod(Map<String, KieBaseModel> kBaseModels) {
        this.kBaseModels = kBaseModels;
        init();
    }

    public String getConstructor() {
        StringBuilder sb = new StringBuilder(
                "    private java.util.Map<String, KieBase> kbases = new java.util.HashMap<>();\n" +
                "\n" +
                "    public ProjectRuntime() {\n" +
                "        ProjectModel model = new ProjectModel();\n" +
                "        java.util.Map<String, KieBaseModel> kBaseModelMap = model.getKieModuleModel().getKieBaseModels();\n"
        );
        kBaseModels.keySet().forEach( kBaseName ->
                sb.append( "        kbases.put(\"" + kBaseName + "\", org.drools.modelcompiler.builder.KieBaseBuilder.createKieBaseFromModel( model.getModels(), kBaseModelMap.get( \"" + kBaseName + "\" ) ));\n" ));
        sb.append( "    }\n" );
        return sb.toString();
    }

    public String toGetKieModuleModelMethod() {
        MethodDeclaration methodDeclaration = new MethodDeclaration(nodeList(publicModifier()), new ClassOrInterfaceType(null, kieModuleModelCanonicalName), "getKieModuleModel");
        methodDeclaration.setBody(stmt);
        methodDeclaration.addAnnotation( "Override" );
        return methodDeclaration.toString();
    }

    public String toNewKieSessionMethod() {
        return
                "    @Override\n" +
                "    public KieSession newKieSession() {\n" +
                "        return newKieSession(\"" + defaultKieSessionName + "\");\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public KieSession newKieSession(String sessionName) {\n" +
                "        return newKieSession(sessionName, new org.drools.core.config.StaticRuleConfig(new org.drools.core.config.DefaultRuleEventListenerConfig()));\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public KieSession newKieSession(String sessionName, org.kie.kogito.rules.RuleConfig ruleConfig) {\n" +
                "        return java.util.Optional.ofNullable(getKieBaseForSession(sessionName).newKieSession(getConfForSession(sessionName), null)).map(k -> {\nruleConfig.ruleEventListeners().agendaListeners().forEach( l -> k.addEventListener(l));\n" + 
                "            ruleConfig.ruleEventListeners().ruleRuntimeListeners().forEach( l -> k.addEventListener(l));\n" + 
                "            return k;\n" + 
                "        }).get();" +
                "    }\n";
    }

    public String toGetKieBaseForSessionMethod() {
        StringBuilder sb = new StringBuilder(
                "    private KieBase getKieBaseForSession(String sessionName) {\n" +
                "        switch (sessionName) {\n"
        );

        for (Map.Entry<String, String> entry : kSessionForkBase.entrySet()) {
            sb.append( "            case \"" + entry.getKey() + "\": return kbases.get(\"" + entry.getValue() + "\");\n" );
        }

        sb.append(
                "        }\n" +
                "        return null;\n" +
                "    }\n" );
        return sb.toString();
    }

    public String toKieSessionConfMethod() {
        StringBuilder sb = new StringBuilder(
                "    private org.kie.api.runtime.KieSessionConfiguration getConfForSession(String sessionName) {\n" +
                "        org.drools.core.SessionConfigurationImpl conf = new org.drools.core.SessionConfigurationImpl();\n" +
                "        switch (sessionName) {\n"
        );

        for (Map.Entry<String, BlockStmt> entry : kSessionConfs.entrySet()) {
            sb.append( "            case \"" + entry.getKey() + "\":\n" );
            sb.append( entry.getValue() );
            sb.append( "                break;\n" );
        }

        sb.append(
                "        }\n" +
                "        return conf;\n" +
                "    }\n" );
        return sb.toString();
    }

    private void init() {
        stmt.addStatement(parseStatement(String.format("%s %s = org.kie.api.KieServices.get().newKieModuleModel();", kieModuleModelCanonicalName, KMODULE_MODEL_NAME)));
        kBaseModels.values().forEach( kBaseModel -> new BaseModelGenerator(kBaseModel).toSourceCode() );
        stmt.addStatement(parseStatement(String.format("return %s;", KMODULE_MODEL_NAME)));
    }

    private class BaseModelGenerator {

        private final KieBaseModel kieBaseModel;
        private final String kieBaseModelName;
        private final NameExpr kieBaseModelNameExpr;
        private final NameExpr confExpr;
        private final BlockStmt confBlock = new BlockStmt();

        private BaseModelGenerator(KieBaseModel kieBaseModel) {
            this.kieBaseModel = kieBaseModel;
            this.kieBaseModelName = "kieBaseModel_" + kieBaseModel.getName();
            this.kieBaseModelNameExpr = new NameExpr(kieBaseModelName);
            this.confExpr = new NameExpr("conf");
            kBaseConfs.put( kieBaseModel.getName(), this.confBlock );
        }

        void toSourceCode() {
            newBaseModelInstance();
            kieBaseModelDefault();
            eventProcessingType();
            kieBaseModelPackages();
            sessionModels();
        }

        private void newBaseModelInstance() {
            stmt.addStatement(newInstance(KieBaseModel.class.getName(), kieBaseModelName, moduleModelNameExpr(), "newKieBaseModel", kieBaseModel.getName()));
        }

        private void kieBaseModelDefault() {
            if (kieBaseModel.isDefault()) {
                defaultKieBaseName = kieBaseModel.getName();
                stmt.addStatement(new MethodCallExpr(kieBaseModelNameExpr, "setDefault", nodeList(new BooleanLiteralExpr(true))));
            }
        }

        private void eventProcessingType() {
            createEnum(stmt, kieBaseModelNameExpr, kieBaseModel.getEventProcessingMode().getClass().getCanonicalName(), kieBaseModel.getEventProcessingMode().getMode().toUpperCase(), "setEventProcessingMode");
            createEnum(confBlock, confExpr, kieBaseModel.getEventProcessingMode().getClass().getCanonicalName(), kieBaseModel.getEventProcessingMode().getMode().toUpperCase(), "setEventProcessingMode");
        }

        void kieBaseModelPackages() {
            for (String p : kieBaseModel.getPackages()) {
                stmt.addStatement(new MethodCallExpr(kieBaseModelNameExpr, "addPackage", nodeList(new StringLiteralExpr(p))));
            }
        }

        private void sessionModels() {
            kieBaseModel.getKieSessionModels().values().forEach( kSessionModel -> {
                kSessionForkBase.put( kSessionModel.getName(), kieBaseModel.getName() );
                new SessionModelGenerator(kSessionModel, kieBaseModelNameExpr).toSourceCode();
            } );
        }
    }

    private class SessionModelGenerator {
        private KieSessionModel kieSessionModel;
        private NameExpr kieBaseModelNameExpr;
        private String name;
        private NameExpr nameExpr;
        private final NameExpr confExpr;
        private final BlockStmt confBlock = new BlockStmt();

        private SessionModelGenerator(KieSessionModel kieSessionModel, NameExpr kieBaseModelNameExpr) {
            this.kieSessionModel = kieSessionModel;
            this.kieBaseModelNameExpr = kieBaseModelNameExpr;
            this.name = "kieSessionModel_" + kieSessionModel.getName();
            this.nameExpr = new NameExpr(name);
            this.confExpr = new NameExpr("conf");
            kSessionConfs.put( kieSessionModel.getName(), this.confBlock );
        }

        void toSourceCode() {
            newSessionModelInstance();
            sessionDefault();
            setSessionModelType();
            setClockType();
        }

        private void newSessionModelInstance() {
            stmt.addStatement(newInstance(KieSessionModel.class.getName(), name, kieBaseModelNameExpr, "newKieSessionModel", kieSessionModel.getName()));
        }

        private void setSessionModelType() {
            KieSessionModel.KieSessionType type = kieSessionModel.getType();
            createEnum(stmt, nameExpr, type.getClass().getCanonicalName(), type.toString(), "setType");
        }

        private void setClockType() {
            NameExpr type = new NameExpr(kieSessionModel.getClockType().getClass().getCanonicalName());
            MethodCallExpr clockTypeEnum = new MethodCallExpr(type, "get", nodeList(new StringLiteralExpr(kieSessionModel.getClockType().getClockType())));
            stmt.addStatement(new MethodCallExpr(nameExpr, "setClockType", nodeList(clockTypeEnum)));

            confBlock.addStatement(new MethodCallExpr(confExpr, "setOption", nodeList(clockTypeEnum.clone())));
        }

        private void sessionDefault() {
            if (kieSessionModel.isDefault()) {
                if (kieSessionModel.getType() == KieSessionModel.KieSessionType.STATELESS) {
                    defaultKieStatelessSessionName = kieSessionModel.getName();
                } else {
                    defaultKieSessionName = kieSessionModel.getName();
                }
                stmt.addStatement(new MethodCallExpr(nameExpr, "setDefault", nodeList(new BooleanLiteralExpr(true))));
            }
        }
    }

    private void createEnum( BlockStmt stmt, Expression expr, String enumType, String enumName, String enumSetter) {
        String sessionType = enumType + "." + enumName;
        FieldAccessExpr sessionTypeEnum = parseExpression(sessionType);
        stmt.addStatement(new MethodCallExpr(expr, enumSetter, nodeList(sessionTypeEnum)));
    }

    private NameExpr moduleModelNameExpr() {
        return new NameExpr(KMODULE_MODEL_NAME);
    }

    private AssignExpr newInstance( String type, String variableName, NameExpr scope, String methodName, String parameter) {
        MethodCallExpr initMethod = new MethodCallExpr(scope, methodName, nodeList(new StringLiteralExpr(parameter)));
        VariableDeclarationExpr var = new VariableDeclarationExpr(new ClassOrInterfaceType(null, type), variableName);
        return new AssignExpr(var, initMethod, AssignExpr.Operator.ASSIGN);
    }
}
