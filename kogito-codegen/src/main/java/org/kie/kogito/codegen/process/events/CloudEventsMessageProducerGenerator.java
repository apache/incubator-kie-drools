package org.kie.kogito.codegen.process.events;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.process.MessageProducerGenerator;

public class CloudEventsMessageProducerGenerator extends MessageProducerGenerator {

    public CloudEventsMessageProducerGenerator(WorkflowProcess process, String modelfqcn, String processfqcn, String messageDataEventClassName, TriggerMetaData trigger) {
        super(process, modelfqcn, processfqcn, messageDataEventClassName, trigger);
    }

    @Override
    protected String getTemplate() {
        return "/class-templates/events/CloudEventsMessageProducerTemplate.java";
    }

    @Override
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
