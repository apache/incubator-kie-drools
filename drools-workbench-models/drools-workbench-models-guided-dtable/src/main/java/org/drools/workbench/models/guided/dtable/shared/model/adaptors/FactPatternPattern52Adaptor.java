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
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

/**
 * Adaptor to use RuleModel class in GuidedDecisionTable
 */
public class FactPatternPattern52Adaptor extends Pattern52 {

    private static final long serialVersionUID = 540l;

    private IFactPattern fp;

    public FactPatternPattern52Adaptor() {
    }

    public FactPatternPattern52Adaptor( final IFactPattern fp ) {
        PortablePreconditions.checkNotNull( "fp",
                                            fp );
        this.fp = fp;
    }

    @Override
    public String getFactType() {
        return fp.getFactType();
    }

    @Override
    public String getBoundName() {
        if ( fp instanceof FactPattern ) {
            return ( (FactPattern) fp ).getBoundName();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBound() {
        if ( fp instanceof FactPattern ) {
            return ( (FactPattern) fp ).isBound();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNegated() {
        if ( fp instanceof FactPattern ) {
            return ( (FactPattern) fp ).isNegated();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public CEPWindow getWindow() {
        if ( fp instanceof FactPattern ) {
            return ( (FactPattern) fp ).getWindow();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEntryPointName() {
        if ( fp instanceof FromEntryPointFactPattern ) {
            return ( (FromEntryPointFactPattern) fp ).getEntryPointName();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pattern52 clonePattern() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update( Pattern52 other ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFactType( String factType ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBoundName( String boundName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNegated( boolean negated ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ConditionCol52> getChildColumns() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChildColumns( List<ConditionCol52> conditions ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWindow( CEPWindow window ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEntryPointName( String entryPointName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHeader( String header ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHideColumn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHideColumn( boolean hideColumn ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWidth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWidth( int width ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DTCellValue52 getDefaultValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultValue( DTCellValue52 defaultValue ) {
        throw new UnsupportedOperationException();
    }

}