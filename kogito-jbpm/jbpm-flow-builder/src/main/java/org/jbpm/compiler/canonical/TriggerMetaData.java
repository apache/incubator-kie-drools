/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.canonical;

import java.util.Map;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.util.StringUtils;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.Metadata;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.ProcessInstance;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.jbpm.compiler.canonical.AbstractVisitor.KCONTEXT_VAR;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE;
import static org.jbpm.ruleflow.core.Metadata.MESSAGE_TYPE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_TYPE;

public class TriggerMetaData {

    public enum TriggerType {
        ConsumeMessage,
        ProduceMessage,
        Signal
    }
    // name of the trigger derived from message or signal
    private String name;
    // type of the trigger e.g. message, signal, timer...
    private TriggerType type;
    // data type of the event associated with this trigger
    private String dataType;
    // reference in the model of the process the event should be mapped to
    private String modelRef;
    // reference to owner of the trigger usually node
    private String ownerId;
    
    public TriggerMetaData(String name, String type, String dataType, String modelRef, String ownerId) {
        super();
        this.name = name;
        this.type = TriggerType.valueOf(type);
        this.dataType = dataType;
        this.modelRef = modelRef;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public TriggerType getType() {
        return type;
    }
    
    public void setType(TriggerType type) {
        this.type = type;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getModelRef() {
        return modelRef;
    }
    
    public void setModelRef(String modelRef) {
        this.modelRef = modelRef;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public TriggerMetaData validate() {
        if (TriggerType.ConsumeMessage.equals(type) || TriggerType.ProduceMessage.equals(type)) {
        
            if (StringUtils.isEmpty(name) || 
                StringUtils.isEmpty(dataType) ||
                StringUtils.isEmpty(modelRef)) {
                throw new IllegalArgumentException("Message Trigger information is not complete " + this);
            }
        } else if (TriggerType.Signal.equals(type) && StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Signal Trigger information is not complete " + this);         
        }
        
        return this;
    }

    @Override
    public String toString() {
        return "TriggerMetaData [name=" + name + ", type=" + type + ", dataType=" + dataType + ", modelRef=" + modelRef + "]";
    }
    
    public static ObjectCreationExpr buildAction(String signalName, String variable, String scope) {
        return new ObjectCreationExpr(null,
                parseClassOrInterfaceType(SignalProcessInstanceAction.class.getCanonicalName()),
                new NodeList<>(new StringLiteralExpr(signalName), variable != null ? new StringLiteralExpr(variable.replace("\"", "\\\"")) : new CastExpr(
                        parseClassOrInterfaceType(String.class.getCanonicalName()), new NullLiteralExpr()),
                        scope != null ? new StringLiteralExpr(scope) : new CastExpr(
                                parseClassOrInterfaceType(String.class.getCanonicalName()), new NullLiteralExpr())));
    }

    public static LambdaExpr buildLambdaExpr(Node node, ProcessMetaData metadata) {
        Map<String, Object> nodeMetaData = node.getMetaData();
        String messageName = (String) nodeMetaData.get(TRIGGER_REF);
        TriggerMetaData triggerMetaData = new TriggerMetaData(
                messageName,
                (String) nodeMetaData.get(TRIGGER_TYPE),
                (String) nodeMetaData.get(MESSAGE_TYPE),
                (String) nodeMetaData.get(MAPPING_VARIABLE),
                String.valueOf(node.getId()))
                .validate();
        metadata.addTrigger(triggerMetaData);
        NameExpr kExpr = new NameExpr(KCONTEXT_VAR); 
        
        BlockStmt actionBody = new BlockStmt();
        final String objectName = "object";
        final String runtimeName = "runtime";
        final String processName = "process";
        final String piName = "pi";
        NameExpr object = new NameExpr(objectName);
        NameExpr runtime = new NameExpr(runtimeName);
        NameExpr pi = new NameExpr(piName);
        Type objectType = new ClassOrInterfaceType(null, triggerMetaData.getDataType());
        Type processRuntime =  parseClassOrInterfaceType(InternalProcessRuntime.class.getCanonicalName());
        Type kieRuntime =  parseClassOrInterfaceType(InternalKnowledgeRuntime.class.getCanonicalName());
        Type processInstance = parseClassOrInterfaceType(ProcessInstance.class.getCanonicalName());
        AssignExpr objectExpr = new AssignExpr(
                new VariableDeclarationExpr(objectType, objectName),
                new CastExpr(objectType, new MethodCallExpr(kExpr, "getVariable").addArgument(new StringLiteralExpr(
                        triggerMetaData.getModelRef()))),
                Operator.ASSIGN);
        AssignExpr runtimeExpr = new AssignExpr(
                new VariableDeclarationExpr(kieRuntime, runtimeName),
                new CastExpr(kieRuntime, new MethodCallExpr(kExpr, "getKieRuntime")),
                Operator.ASSIGN);
        AssignExpr processExpr = new AssignExpr(
                new VariableDeclarationExpr(processRuntime, processName),
                new CastExpr(processRuntime, new MethodCallExpr(runtime, "getProcessRuntime")),
                Operator.ASSIGN);
        AssignExpr processInstanceAssignment = new AssignExpr(
                new VariableDeclarationExpr(processInstance, piName),
                new MethodCallExpr(new NameExpr("kcontext"), "getProcessInstance"),
                Operator.ASSIGN);
        // add onMessage listener call
        MethodCallExpr listenerMethodCall = new MethodCallExpr(
                new MethodCallExpr(new NameExpr (processName), "getProcessEventSupport"), "fireOnMessage")
                        .addArgument(pi)
                        .addArgument(new MethodCallExpr(kExpr, "getNodeInstance"))
                        .addArgument(runtime)
                        .addArgument(new StringLiteralExpr(messageName)).addArgument(object);
        // add producer call
        MethodCallExpr producerMethodCall = new MethodCallExpr(new NameExpr("producer_" + node.getId()), "produce")
                .addArgument(pi).addArgument(object);
        actionBody.addStatement(objectExpr);
        actionBody.addStatement(runtimeExpr);
        actionBody.addStatement(processInstanceAssignment);
        actionBody.addStatement(processExpr);
        actionBody.addStatement(listenerMethodCall);
        actionBody.addStatement(producerMethodCall);
        return new LambdaExpr(new Parameter(new UnknownType(), KCONTEXT_VAR), actionBody);
    }

    public static LambdaExpr buildCompensationLambdaExpr(String compensationRef) {
        BlockStmt actionBody = new BlockStmt();
        MethodCallExpr getProcessInstance = new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getProcessInstance");
        MethodCallExpr signalEvent = new MethodCallExpr(getProcessInstance, "signalEvent")
                .addArgument(new StringLiteralExpr(Metadata.EVENT_TYPE_COMPENSATION))
                .addArgument(new StringLiteralExpr(compensationRef));
        actionBody.addStatement(signalEvent);
        return new LambdaExpr(
                new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                actionBody
        );
    }
}
