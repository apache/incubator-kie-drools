/*
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
package org.jbpm.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AbstractReturnValueEvaluator implements ReturnValueEvaluator, Externalizable {

    protected String dialect;
    protected String expression;
    protected Class<?> type;
    private String root;

    public AbstractReturnValueEvaluator(String dialect, String expression) {
        this(dialect, expression, Object.class, null);
    }

    public AbstractReturnValueEvaluator(String dialect, String expression, Class<?> type, String root) {
        this.dialect = dialect;
        this.expression = expression;
        this.type = type;
        this.root = root;
    }

    @Override
    public String root() {
        return root;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public String dialect() {
        return dialect;
    }

    @Override
    public String expression() {
        return expression;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.expression = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.expression);
    }

    public String toString() {
        return "[" + dialect + "] (" + this.expression + ")";
    }

}
