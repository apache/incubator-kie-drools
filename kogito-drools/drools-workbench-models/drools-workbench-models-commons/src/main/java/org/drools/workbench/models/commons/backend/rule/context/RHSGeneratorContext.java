/*
 * Copyright 2013 JBoss Inc
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
package org.drools.workbench.models.commons.backend.rule.context;

import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;

/**
 * RHS DRL generation context object
 */
public class RHSGeneratorContext {

    private Set<String> varsInScope = new HashSet<String>();
    private IAction action;
    private ActionFieldValue afv;
    private RHSGeneratorContext parent;
    private int depth;
    private int offset;
    private boolean hasOutput;
    private boolean hasNonTemplateOutput;

    RHSGeneratorContext() {
    }

    RHSGeneratorContext( final RHSGeneratorContext parent,
                         final IAction action,
                         final int depth,
                         final int offset ) {
        this.parent = parent;
        this.depth = depth;
        this.offset = offset;
        setAction( action );
    }

    private void setAction( final IAction action ) {
        this.action = action;
        final Set<InterpolationVariable> vars = new HashSet<InterpolationVariable>();
        final GeneratorContextRuleModelVisitor visitor = new GeneratorContextRuleModelVisitor( vars );
        visitor.visit( action );
        for ( InterpolationVariable var : vars ) {
            varsInScope.add( var.getVarName() );
        }
        hasNonTemplateOutput = visitor.hasNonTemplateOutput();
    }

    RHSGeneratorContext( final RHSGeneratorContext parent,
                         final ActionFieldValue afv,
                         final int depth,
                         final int offset ) {
        this.parent = parent;
        this.depth = depth;
        this.offset = offset;
        setActionFieldValue( afv );
    }

    private void setActionFieldValue( final ActionFieldValue afv ) {
        this.afv = afv;
        final Set<InterpolationVariable> vars = new HashSet<InterpolationVariable>();
        final GeneratorContextRuleModelVisitor visitor = new GeneratorContextRuleModelVisitor( vars );
        visitor.visit( afv );
        for ( InterpolationVariable var : vars ) {
            varsInScope.add( var.getVarName() );
        }
        hasNonTemplateOutput = visitor.hasNonTemplateOutput();
    }

    public IAction getAction() {
        return action;
    }

    public ActionFieldValue getActionFieldValue() {
        return afv;
    }

    public RHSGeneratorContext getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isHasOutput() {
        return hasOutput;
    }

    public void setHasOutput( boolean hasOutput ) {
        this.hasOutput = hasOutput;
    }

    public Set<String> getVarsInScope() {
        return this.varsInScope;
    }

    public boolean hasNonTemplateOutput() {
        return hasNonTemplateOutput;
    }

}
