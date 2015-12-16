/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;

/**
 * LHS DRL generation context object
 */
public class LHSGeneratorContext {

    private Set<String> varsInScope = new HashSet<String>();
    private FieldConstraint fieldConstraint;
    private IPattern pattern;
    private LHSGeneratorContext parent;
    private int depth;
    private int offset;
    private boolean hasOutput;
    private boolean hasNonTemplateOutput;

    LHSGeneratorContext() {
    }

    LHSGeneratorContext( final LHSGeneratorContext parent,
                         final IPattern pattern,
                         final int depth,
                         final int offset ) {
        this.parent = parent;
        this.depth = depth;
        this.offset = offset;
        setPattern( pattern );
    }

    private void setPattern( final IPattern pattern ) {
        this.pattern = pattern;
        this.varsInScope.clear();
        final Set<InterpolationVariable> vars = new HashSet<InterpolationVariable>();
        final GeneratorContextRuleModelVisitor visitor = new GeneratorContextRuleModelVisitor( vars );
        visitor.visit( pattern );
        for ( InterpolationVariable var : vars ) {
            varsInScope.add( var.getVarName() );
        }
        hasNonTemplateOutput = visitor.hasNonTemplateOutput();
    }

    LHSGeneratorContext( final LHSGeneratorContext parent,
                         final FieldConstraint fieldConstraint,
                         final int depth,
                         final int offset ) {
        this.parent = parent;
        this.depth = depth;
        this.offset = offset;
        setFieldConstraint( fieldConstraint );
    }

    private void setFieldConstraint( final FieldConstraint fieldConstraint ) {
        this.fieldConstraint = fieldConstraint;
        this.varsInScope.clear();
        final Set<InterpolationVariable> vars = new HashSet<InterpolationVariable>();
        final GeneratorContextRuleModelVisitor visitor = new GeneratorContextRuleModelVisitor( vars );
        visitor.visit( fieldConstraint );
        for ( InterpolationVariable var : vars ) {
            varsInScope.add( var.getVarName() );
        }
        hasNonTemplateOutput = visitor.hasNonTemplateOutput();
    }

    public IPattern getPattern() {
        return pattern;
    }

    public FieldConstraint getFieldConstraint() {
        return fieldConstraint;
    }

    public LHSGeneratorContext getParent() {
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
        if ( parent != null ) {
            parent.setHasOutput( hasOutput );
        }
    }

    public Set<String> getVarsInScope() {
        return this.varsInScope;
    }

    public boolean hasNonTemplateOutput() {
        return hasNonTemplateOutput;
    }

}
