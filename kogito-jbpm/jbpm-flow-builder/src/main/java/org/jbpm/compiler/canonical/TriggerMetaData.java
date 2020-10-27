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

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.core.util.StringUtils;
import org.jbpm.ruleflow.core.Metadata;
import org.kie.api.definition.process.Node;

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

    public static LambdaExpr buildLambdaExpr(Node node, ProcessMetaData metadata) {
        Map<String, Object> nodeMetaData = node.getMetaData();
        TriggerMetaData triggerMetaData = new TriggerMetaData(
                (String) nodeMetaData.get(TRIGGER_REF),
                (String) nodeMetaData.get(TRIGGER_TYPE),
                (String) nodeMetaData.get(MESSAGE_TYPE),
                (String) nodeMetaData.get(MAPPING_VARIABLE),
                String.valueOf(node.getId()))
                .validate();
        metadata.addTrigger(triggerMetaData);

        // and add trigger action
        BlockStmt actionBody = new BlockStmt();
        CastExpr variable = new CastExpr(
                new ClassOrInterfaceType(null, triggerMetaData.getDataType()),
                new MethodCallExpr(new NameExpr(KCONTEXT_VAR), "getVariable")
                        .addArgument(new StringLiteralExpr(triggerMetaData.getModelRef())));
        MethodCallExpr producerMethodCall = new MethodCallExpr(new NameExpr("producer_" + node.getId()), "produce").addArgument(new MethodCallExpr(new NameExpr("kcontext"), "getProcessInstance")).addArgument(variable);
        actionBody.addStatement(producerMethodCall);
        return new LambdaExpr(
                new Parameter(new UnknownType(), KCONTEXT_VAR), // (kcontext) ->
                actionBody
        );
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
