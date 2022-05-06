/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.rule.accessor.CompiledInvoker;
import org.drools.core.rule.accessor.Wireable;
import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.instance.NodeInstance;
import org.kie.api.definition.process.Connection;

/**
 * Default implementation of a constraint.
 * 
 */
public class ReturnValueConstraintEvaluator
        implements
        Constraint,
        ConstraintEvaluator,
        Wireable,
        Externalizable {

    private static final long serialVersionUID = 510l;

    private String name;
    private String constraint;
    private int priority;
    private String dialect;
    private String type;
    private boolean isDefault = false;

    public ReturnValueConstraintEvaluator() {
    }

    private ReturnValueEvaluator evaluator;

    @Override
    public String getConstraint() {
        return this.constraint;
    }

    @Override
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    public String getDialect() {
        return dialect;
    }

    @Override
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public void wire(Object object) {
        setEvaluator((ReturnValueEvaluator) object);
    }

    public void setEvaluator(ReturnValueEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public ReturnValueEvaluator getReturnValueEvaluator() {
        return this.evaluator;
    }

    @Override
    public boolean evaluate(NodeInstance instance,
            Connection connection,
            Constraint constraint) {
        Object value;
        try {
            value = this.evaluator.evaluate(ContextFactory.fromNode(instance));
        } catch (Exception e) {
            throw new RuntimeException("unable to execute ReturnValueEvaluator: ",
                    e);
        }
        if (!(value instanceof Boolean)) {
            throw new RuntimeException("Constraints must return boolean values: " + value + " for expression " + constraint);
        }
        return ((Boolean) value).booleanValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.evaluator = (ReturnValueEvaluator) in.readObject();
        this.name = in.readUTF();
        this.constraint = (String) in.readObject();
        this.priority = in.readInt();
        this.dialect = in.readUTF();
        this.type = (String) in.readObject();

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        if (this.evaluator instanceof CompiledInvoker) {
            out.writeObject(null);
        } else {
            out.writeObject(this.evaluator);
        }
        out.writeUTF(this.name);
        out.writeObject(this.constraint);
        out.writeInt(this.priority);
        out.writeUTF(dialect);
        out.writeObject(type);
    }

    @Override
    public void setMetaData(String name, Object value) {
        // Do nothing
    }

    @Override
    public Object getMetaData(String name) {
        return null;
    }

}
