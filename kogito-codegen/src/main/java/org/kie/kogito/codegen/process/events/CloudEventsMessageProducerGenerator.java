/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.events;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.TemplateInstantiationException;
import org.kie.kogito.codegen.process.MessageProducerGenerator;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;

/**
 * @deprecated now all messages are cloud events https://issues.redhat.com/browse/KOGITO-3414
 */
@Deprecated
public class CloudEventsMessageProducerGenerator extends MessageProducerGenerator {

    public CloudEventsMessageProducerGenerator(WorkflowProcess process, String modelfqcn, String processfqcn, String messageDataEventClassName, TriggerMetaData trigger) {
        super(process, modelfqcn, processfqcn, messageDataEventClassName, trigger);
    }

    public String generate() {
        CompilationUnit clazz = parse(
                this.getClass().getResourceAsStream("/class-templates/events/CloudEventsMessageProducerTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class).
                orElseThrow(() -> new TemplateInstantiationException(
                        "CloudEventMessageProducer",
                        "CloudEventsMessageProducerTemplate.java",
                        "No class declaration found in template"));
        template.setName(resourceClazzName);

        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, trigger.getDataType()));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("produce")).forEach(md -> md.getParameters().stream().filter(p -> p.getNameAsString().equals(EVENT_DATA_VAR)).forEach(p -> p.setType(trigger.getDataType())));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("configure")).forEach(md -> md.addAnnotation("javax.annotation.PostConstruct"));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("marshall")).forEach(md -> {
            md.getParameters().stream().filter(p -> p.getNameAsString().equals(EVENT_DATA_VAR)).forEach(p -> p.setType(trigger.getDataType()));
            md.findAll(StringLiteralExpr.class).forEach(s -> s.setString(s.getValue().replace("$channel$", trigger.getName())));
            md.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataEventType$", messageDataEventClassName)));
        });

        if (useInjection()) {
            annotator.withApplicationComponent(template);

            FieldDeclaration emitterField = template.findFirst(FieldDeclaration.class)
                    .filter(fd -> fd.getVariables().stream().anyMatch(v -> v.getNameAsString().equals("emitter")))
                    .orElseThrow(() -> new IllegalStateException("Cannot find emitter field in MessageProducerTemplate"));
            annotator.withInjection(emitterField);
            annotator.withOutgoingMessage(emitterField, trigger.getName());
            emitterField.getVariable(0).setType(annotator.emitterType("String"));

            MethodDeclaration produceMethod = template.findAll(MethodDeclaration.class).stream()
                    .filter(md -> md.getNameAsString().equals("produce"))
                    .findFirst().orElseThrow(() -> new IllegalStateException("Cannot find produce methods in MessageProducerTemplate"));

            MethodCallExpr sendMethodCall = new MethodCallExpr(new NameExpr("emitter"), "send");
            annotator.withMessageProducer(
                    sendMethodCall,
                    trigger.getName(),
                    new MethodCallExpr(new ThisExpr(), "marshall")
                            .addArgument(new NameExpr("pi"))
                            .addArgument(new NameExpr(EVENT_DATA_VAR)));

            this.generateProduceMethodBody(produceMethod, sendMethodCall);

            template.findAll(FieldDeclaration.class,
                             fd -> fd.getVariable(0).getNameAsString().equals("useCloudEvents")).forEach(fd -> annotator.withConfigInjection(fd, "kogito.messaging.as-cloudevents"));
        }

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    protected void generateProduceMethodBody(MethodDeclaration produceMethod, MethodCallExpr sendMethodCall) {
        final IfStmt condition = produceMethod.findAll(IfStmt.class)
                .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Condition statement not found in produce method!"));
        condition.getElseStmt().orElseThrow(() -> new IllegalArgumentException("Else statement not found in produce method!"))
                .asBlockStmt().addStatement(sendMethodCall);

        if (sendMethodCall.getArguments().isNonEmpty()) {
            // we can safely call clone since the javaparser library implements it correctly.
            final MethodCallExpr decoratedSend = sendMethodCall.clone();
            final Expression parseArgument = decoratedSend.getArguments().get(0);
            decoratedSend.getArguments().get(0).remove();
            condition.getThenStmt()
                    .asBlockStmt()
                    .addStatement(decoratedSend.addArgument(new MethodCallExpr("decorator.get().decorate").addArgument(parseArgument)));
        }
    }
}
