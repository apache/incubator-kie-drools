/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.base.util.Drools;
import org.drools.model.Model;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.CanonicalKieModuleModel;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.util.maven.support.ReleaseIdImpl;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.Modifier.publicModifier;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.modelcompiler.CanonicalKieModule.getProjectModelClassNameNameWithReleaseId;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class ModelSourceClass {

    private static final String PROJECT_MODEL_SOURCE = CanonicalKieModule.PROJECT_MODEL_CLASS.replace('.', '/') + ".java";

    private final Map<String, List<String>> modelsByKBase;
    private final KieModuleModelMethod modelMethod;
    private final ReleaseId releaseId;
    private final String className;

    public ModelSourceClass(ReleaseId releaseId, Map<String, KieBaseModel> kBaseModels, Map<String, List<String>> modelsByKBase) {
        this(releaseId, kBaseModels, modelsByKBase, false);
    }

    public ModelSourceClass(ReleaseId releaseId, Map<String, KieBaseModel> kBaseModels, Map<String, List<String>> modelsByKBase, boolean useUniqueName) {
        this.releaseId = releaseId;
        this.modelsByKBase = modelsByKBase;
        this.modelMethod = new KieModuleModelMethod( kBaseModels );
        this.className = useUniqueName ? getProjectModelClassNameNameWithReleaseId(releaseId) : CanonicalKieModule.PROJECT_MODEL_CLASS;
    }

    public KieModuleModelMethod getModelMethod() {
        return modelMethod;
    }

    public String getName() {
        return className.replace('.', '/') + ".java";
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
                        "public class " + className.substring( className.lastIndexOf( '.' )+1 ) + " implements " + CanonicalKieModuleModel.class.getCanonicalName() + " {\n" +
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

    void addGetModelsMethod(StringBuilder sb) {
        sb.append(
                "    @Override\n" +
                        "    public java.util.List<Model> getModels() {\n" +
                        "        return java.util.Arrays.asList(" );
        String collected =  modelsByKBase.values().stream().flatMap( List::stream ).distinct()
                .map(element -> "new " + element + "()")
                .collect(Collectors.joining(","));
        sb.append(collected);
        sb.append(
                ");\n" +
                        "    }\n" +
                        "\n");
    }

    void addGetModelForKieBaseMethod(StringBuilder sb) {
        sb.append(
                "    public java.util.List<Model> getModelsForKieBase(String kieBaseName) {\n");
        if (!modelMethod.getKieBaseNames().isEmpty()) {
            sb.append( "        switch (kieBaseName) {\n");
            for (String kBase : modelMethod.getKieBaseNames()) {
                sb.append("            case \"" + kBase + "\": ");
                List<String> models = modelsByKBase.get(kBase);
                String collected = null;
                if (models != null) {
                    collected = models.stream()
                            .map(element -> "new " + element + "()")
                            .collect(Collectors.joining(","));
                }
                sb.append(collected != null && !collected.isEmpty() ?
                                  "return java.util.Arrays.asList( " + collected + " );\n" :
                                  "return getModels();\n");
            }
            sb.append("        }\n");
        }
        sb.append(
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

    public static class KieModuleModelMethod {

        private static final String KMODULE_MODEL_NAME = "kModuleModel";

        private final String kieModuleModelCanonicalName = KieModuleModel.class.getCanonicalName();
        private final Map<String, KieBaseModel> kBaseModels;

        private BlockStmt stmt = new BlockStmt();

        private final Map<String, String> kSessionForkBase = new HashMap<>();
        private final Map<String, BlockStmt> kSessionConfs = new HashMap<>();

        private String defaultKieBaseName;
        private String defaultKieSessionName;
        private String defaultKieStatelessSessionName;

        public KieModuleModelMethod(Map<String, KieBaseModel> kBaseModels) {
            this.kBaseModels = kBaseModels;
            init();
        }

        public String toGetKieModuleModelMethod() {
            MethodDeclaration methodDeclaration = new MethodDeclaration(nodeList(publicModifier()), new ClassOrInterfaceType(null, kieModuleModelCanonicalName), "getKieModuleModel");
            methodDeclaration.setBody(stmt);
            methodDeclaration.addAnnotation( createSimpleAnnotation(Override.class) );
            return methodDeclaration.toString();
        }

        public Collection<String> getKieBaseNames() {
            return kBaseModels.keySet();
        }

        public String getDefaultKieBaseName() {
            return defaultKieBaseName;
        }

        public String getDefaultKieSessionName() {
            return defaultKieSessionName;
        }

        public Map<String, BlockStmt> getkSessionConfs() {
            return kSessionConfs;
        }

        public Map<String, String> getkSessionForkBase() {
            return kSessionForkBase;
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
                this.kieBaseModelName = "kieBaseModel_" + toId(kieBaseModel.getName());
                this.kieBaseModelNameExpr = new NameExpr(kieBaseModelName);
                this.confExpr = new NameExpr("conf");
            }

            void toSourceCode() {
                newBaseModelInstance();
                kieBaseModelDefault();
                eventProcessingType();
                kieBaseModelPackages();
                kieBaseModelIncludes();
                kieBaseSessionsPool();
                kieBaseMutability();
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

            private void kieBaseMutability() {
                createEnum(stmt, kieBaseModelNameExpr, kieBaseModel.getMutability().getClass().getCanonicalName(), kieBaseModel.getMutability().toString(), "setMutability");
                createEnum(confBlock, confExpr, kieBaseModel.getMutability().getClass().getCanonicalName(), kieBaseModel.getMutability().toString(), "setMutability");
            }

            private void kieBaseModelPackages() {
                for (String p : kieBaseModel.getPackages()) {
                    stmt.addStatement(new MethodCallExpr(kieBaseModelNameExpr, "addPackage", nodeList(toStringLiteral(p))));
                }
            }

            private void kieBaseModelIncludes() {
                for (String p : kieBaseModel.getIncludes()) {
                    stmt.addStatement(new MethodCallExpr(kieBaseModelNameExpr, "addInclude", nodeList(toStringLiteral(p))));
                }
            }

            private void kieBaseSessionsPool() {
                if (kieBaseModel.getSessionsPool() != SessionsPoolOption.NO) {
                    int poolSize = kieBaseModel.getSessionsPool().getSize();
                    MethodCallExpr poolExpr = new MethodCallExpr(new NameExpr(SessionsPoolOption.class.getCanonicalName()), "get", nodeList(new IntegerLiteralExpr(poolSize)));
                    stmt.addStatement(new MethodCallExpr(kieBaseModelNameExpr, "setSessionsPool", nodeList(poolExpr)));
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
                this.name = "kieSessionModel_" + toId(kieSessionModel.getName());
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
                MethodCallExpr clockTypeEnum = new MethodCallExpr(type, "get", nodeList(toStringLiteral(kieSessionModel.getClockType().getClockType())));
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
            MethodCallExpr initMethod = new MethodCallExpr(scope, methodName, nodeList(toStringLiteral(parameter)));
            VariableDeclarationExpr var = new VariableDeclarationExpr(new ClassOrInterfaceType(null, type), variableName);
            return new AssignExpr(var, initMethod, AssignExpr.Operator.ASSIGN);
        }
    }
}
