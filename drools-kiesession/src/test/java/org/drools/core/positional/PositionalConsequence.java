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

package org.drools.core.positional;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.consequence.Consequence;
import org.drools.base.rule.consequence.ConsequenceContext;
import org.drools.core.positional.VoidFunctions.VoidFunction1;
import org.drools.core.positional.VoidFunctions.VoidFunction2;
import org.drools.core.positional.VoidFunctions.VoidFunction3;
import org.drools.core.positional.VoidFunctions.VoidFunction4;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PositionalConsequence<T extends ConsequenceContext> implements Consequence<T> {

    String name;

    protected final Declaration[] declarations;

    private VoidFunction1<Object>                         p1;
    private VoidFunction2<Object, Object>                 p2;
    private VoidFunction3<Object, Object, Object>         p3;
    private VoidFunction4<Object, Object, Object, Object> p4;


    private int pIndex;

    public PositionalConsequence(String name, Declaration[] declarations) {
        this.declarations = declarations;
        this.name = name;
    }

    @Override
    public void evaluate(T helper, ValueResolver valueResolver) throws Exception {
        BaseTuple t = helper.getTuple();
        switch (pIndex) {
            case 1: {
                p1.apply(t.getFactHandle().getObject());
                return;
            }
            case 2: {
                p2.apply(t.getParent().getFactHandle().getObject(), t.getFactHandle().getObject());
                return;
            } case 3: {
                BaseTuple    v2 = t.getParent();
                p3.apply(v2.getParent().getFactHandle().getObject(),
                         v2.getFactHandle().getObject(),
                         t.getFactHandle().getObject());
                return;
            } case 4: {
                BaseTuple    v2 = t.getParent();
                BaseTuple    v3 = v2.getParent();
                p4.apply(v2.getParent().getFactHandle().getObject(),
                         v2.getFactHandle().getObject(),
                         v3.getFactHandle().getObject(),
                         t.getFactHandle().getObject());
                return;
            } default:
                throw new RuntimeException("No matching predicate on index: " + pIndex);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

    }


    public int getPIndex() {
        return pIndex;
    }

    public <A> void setFunction(VoidFunction1<A> p1) {
        this.p1 = (VoidFunction1<Object>) p1;
        pIndex = 1;
    }

    public <A, B> void setFunction(VoidFunction2<A, B> p2) {
        this.p2 = (VoidFunction2<Object, Object>) p2;
        pIndex = 2;
    }

    public <A, B, C> void setFunction(VoidFunction3<A, B, C> p3) {
        this.p3 = (VoidFunction3<Object, Object, Object>) p3;
        pIndex = 3;
    }

    public <A, B, C, D> void setFunction(VoidFunction4<A, B, C, D> p4) {
        this.p4 = (VoidFunction4<Object, Object, Object, Object>) p4;
        pIndex = 4;
    }

    @Override
    public PositionalConsequence clone() {
        Declaration[]         clonedDeclrs = Arrays.stream(declarations).map(d -> d.clone()).collect(Collectors.toList()).toArray(new Declaration[0]);
        PositionalConsequence clone        = new PositionalConsequence(name, clonedDeclrs);
        clone.pIndex = pIndex;
        clone.p1 = p1;
        clone.p2 = p2;
        clone.p3 = p3;
        clone.p4 = p4;

        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            clonedDeclarations[i] = declarations[i].clone();
        }

        return clone;
    }

}
