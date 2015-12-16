/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.guided.dtable.shared.model.adaptors;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

/**
 * Adaptor to use RuleModel class in GuidedDecisionTable
 */
public class Pattern52FactPatternAdaptor extends FactPattern {

    private static final long serialVersionUID = 540l;

    private Pattern52 pattern;

    public Pattern52FactPatternAdaptor() {
    }

    public Pattern52FactPatternAdaptor( final Pattern52 pattern ) {
        PortablePreconditions.checkNotNull( "pattern",
                                            pattern );
        this.pattern = pattern;
    }

    @Override
    public boolean isBound() {
        return pattern.isBound();
    }

    @Override
    public String getBoundName() {
        return pattern.getBoundName();
    }

    @Override
    public String getFactType() {
        return pattern.getFactType();
    }

    @Override
    public boolean isNegated() {
        return pattern.isNegated();
    }

    @Override
    public void setBoundName( final String boundName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNegated( final boolean isNegated ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addConstraint( final FieldConstraint constraint ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeConstraint( final int idx ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldConstraint[] getFieldConstraints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFieldConstraints( final List<FieldConstraint> sortedConstraints ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFactType( final String factType ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWindow( final CEPWindow window ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CEPWindow getWindow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumberOfConstraints() {
        throw new UnsupportedOperationException();
    }

}