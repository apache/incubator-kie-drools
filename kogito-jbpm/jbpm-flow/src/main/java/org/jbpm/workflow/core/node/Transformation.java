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
package org.jbpm.workflow.core.node;

import java.io.Serializable;

public class Transformation implements Serializable {

    private static final long serialVersionUID = 1641905060375832661L;

    private String language;
    private String expression;
    private Object compiledExpression;

    public Transformation(String lang, String expression) {
        this(lang, expression, null);
    }

    public Transformation(String lang, String expression, Object compiledExpression) {
        this.language = lang;
        this.expression = expression;
        this.compiledExpression = compiledExpression;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object getCompiledExpression() {
        return compiledExpression;
    }

    public void setCompiledExpression(Object compliedExpression) {
        this.compiledExpression = compliedExpression;
    }

}
