/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.template.backend.upgrade;

import org.drools.workbench.models.commons.backend.IUpgradeHelper;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;

/**
 * Utility class to support upgrades of the RuleModel model. This implementation
 * ensures additional ConnectiveConstraints fields created for Guvnor v5.2 have
 * reasonable default values.
 */
public class RuleModelUpgradeHelper2
        implements
        IUpgradeHelper<RuleModel, RuleModel> {

    public RuleModel upgrade( RuleModel model ) {
        updateConnectiveConstraints( model );
        return model;
    }

    //Connective Constraints in 5.2.0 were changed to include the Fact and Field to which they relate.
    //While this should not be necessary (as a ConnectiveConstraint is a further constraint on an already
    //known Fact and Field) it became essential as a hack to have ConnectiveConstraints on sub-fields
    //in a Pattern when an Expression is not used. This codes ensures ConnectiveConstraints on legacy
    //repositories have the fields correctly set.
    private void updateConnectiveConstraints( RuleModel model ) {
        for ( IPattern p : model.lhs ) {
            fixConnectiveConstraints( p );
        }
    }

    //Descent into the model
    private void fixConnectiveConstraints( IPattern p ) {
        if ( p instanceof FactPattern) {
            fixConnectiveConstraints((FactPattern) p);
        } else if ( p instanceof CompositeFactPattern) {
            fixConnectiveConstraints( (CompositeFactPattern) p );
        }
    }

    private void fixConnectiveConstraints( FactPattern p ) {
        for ( FieldConstraint fc : p.getFieldConstraints() ) {
            fixConnectiveConstraints( fc );
        }
    }

    private void fixConnectiveConstraints( CompositeFactPattern p ) {
        for ( IPattern sp : p.getPatterns() ) {
            fixConnectiveConstraints( sp );
        }
    }

    private void fixConnectiveConstraints( FieldConstraint fc ) {
        if ( fc instanceof SingleFieldConstraint) {
            fixConnectiveConstraints( (SingleFieldConstraint) fc );
        } else if ( fc instanceof CompositeFieldConstraint) {
            fixConnectiveConstraints( (CompositeFieldConstraint) fc );
        }
    }

    private void fixConnectiveConstraints( SingleFieldConstraint sfc ) {
        if ( sfc.getConnectives() == null ) {
            return;
        }
        for ( ConnectiveConstraint cc : sfc.getConnectives() ) {
            if ( cc.getFieldName() == null ) {
                cc.setFieldName( sfc.getFieldName() );
                cc.setFieldType( sfc.getFieldType() );
            }
        }
    }

    private void fixConnectiveConstraints( CompositeFieldConstraint cfc ) {
        if ( cfc.getConstraints() == null ) {
            return;
        }
        for ( FieldConstraint fc : cfc.getConstraints() ) {
            fixConnectiveConstraints( fc );
        }
    }

}
