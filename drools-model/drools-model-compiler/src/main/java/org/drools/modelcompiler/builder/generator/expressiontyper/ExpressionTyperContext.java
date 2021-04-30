/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.expr.Expression;

import static org.drools.core.util.StringUtils.lcFirstForBean;

public class ExpressionTyperContext {

    private List<String> usedDeclarations = new ArrayList<>();
    private Set<String> reactOnProperties = new HashSet<>();
    private List<Expression> prefixExpresssions = new ArrayList<>();
    private Expression originalExpression;

    private boolean registerPropertyReactivity = true;

    private Optional<Expression> inlineCastExpression = Optional.empty();

    public void addUsedDeclarations(String name) {
        usedDeclarations.add(name);
    }

    public List<String> getUsedDeclarations() {
        return usedDeclarations;
    }

    public void addReactOnProperties(String prop) {
        if (registerPropertyReactivity) {
            reactOnProperties.add( lcFirstForBean( prop ) );
        }
    }

    public Set<String> getReactOnProperties() {
        return reactOnProperties;
    }

    public List<Expression> getPrefixExpresssions() {
        return prefixExpresssions;
    }

    public void addPrefixExpression(int index, Expression prefixExpresssion) {
        prefixExpresssions.add(index, prefixExpresssion);
    }

    public void addPrefixExpression(Expression prefixExpresssion) {
        prefixExpresssions.add(prefixExpresssion);
    }

    public void setRegisterPropertyReactivity( boolean registerPropertyReactivity ) {
        this.registerPropertyReactivity = registerPropertyReactivity;
    }

    public boolean isRegisterPropertyReactivity() {
        return registerPropertyReactivity;
    }

    public Optional<Expression> getInlineCastExpression() {
        return inlineCastExpression;
    }

    public void setInlineCastExpression(Optional<Expression> inlineCastExpression) {
        this.inlineCastExpression = inlineCastExpression;
    }

    public Expression getOriginalExpression() {
        return originalExpression;
    }

    public void setOriginalExpression(Expression originalExpression) {
        this.originalExpression = originalExpression;
    }
}
