/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.dtable.backend;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.workbench.models.guided.dtable.backend.util.GuidedDecisionTableUpgradeHelper1;
import org.drools.workbench.models.guided.dtable.backend.util.GuidedDecisionTableUpgradeHelper2;
import org.drools.workbench.models.guided.dtable.backend.util.GuidedDecisionTableUpgradeHelper3;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionInsertFactCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionRetractFactCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionSetFieldCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.AttributeCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ConditionCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.GuidedDecisionTable;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.MetadataCol;

import java.math.BigDecimal;

@SuppressWarnings("deprecation")
public class GuidedDTXMLPersistence {

    private XStream xt;
    private static final GuidedDecisionTableUpgradeHelper1 upgrader1 = new GuidedDecisionTableUpgradeHelper1();
    private static final GuidedDecisionTableUpgradeHelper2 upgrader2 = new GuidedDecisionTableUpgradeHelper2();
    private static final GuidedDecisionTableUpgradeHelper3 upgrader3 = new GuidedDecisionTableUpgradeHelper3();
    private static final GuidedDTXMLPersistence INSTANCE = new GuidedDTXMLPersistence();

    private GuidedDTXMLPersistence() {
        xt = new XStream( new DomDriver() );

        //Legacy model
        xt.alias( "decision-table",
                  GuidedDecisionTable.class );
        xt.alias( "metadata-column",
                  MetadataCol.class );
        xt.alias( "attribute-column",
                  AttributeCol.class );
        xt.alias( "condition-column",
                  ConditionCol.class );
        xt.alias( "set-field-col",
                  ActionSetFieldCol.class );
        xt.alias( "retract-fact-column",
                  ActionRetractFactCol.class );
        xt.alias( "insert-fact-column",
                  ActionInsertFactCol.class );

        //Post 5.2 model
        xt.alias( "decision-table52",
                  GuidedDecisionTable52.class );
        xt.alias( "metadata-column52",
                  MetadataCol52.class );
        xt.alias( "attribute-column52",
                  AttributeCol52.class );
        xt.alias( "condition-column52",
                  ConditionCol52.class );
        xt.alias( "set-field-col52",
                  ActionSetFieldCol52.class );
        xt.alias( "retract-fact-column52",
                  ActionRetractFactCol52.class );
        xt.alias( "insert-fact-column52",
                  ActionInsertFactCol52.class );
        xt.alias( "value",
                  DTCellValue52.class );
        xt.alias( "Pattern52",
        		Pattern52.class );
        
        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasPackage( "org.drools.guvnor.client",
                         "org.drools.ide.common.client");
                         
        //this is for migrating org.drools.ide.common.client.modeldriven.auditlog.AuditLog to org.drools.workbench.models.datamodel.auditlog.AuditLog
		xt.aliasPackage("org.drools.guvnor.client.modeldriven.dt52.auditlog",
				"org.drools.workbench.models.guided.dtable.shared.auditlog");

	    //this is for migrating org.drools.ide.common.client.modeldriven.dt52.auditlog.DecisionTableAuditLogFilter
		//to org.drools.workbench.models.guided.dtable.shared.auditlog.DecisionTableAuditLogFilter
		xt.aliasPackage("org.drools.guvnor.client.modeldriven.dt52",
				" org.drools.workbench.models.guided.dtable.shared.model");
        
        //All numerical values are historically BigDecimal
        xt.alias( "valueNumeric",
                  Number.class,
                  BigDecimal.class );
    }

    public static GuidedDTXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( GuidedDecisionTable52 dt ) {
        return xt.toXML( dt );
    }

    public GuidedDecisionTable52 unmarshal( String xml ) {
        if ( xml == null || xml.trim().equals( "" ) ) {
            return new GuidedDecisionTable52();
        }

        //Upgrade DTModel to new class
        Object model = xt.fromXML( xml );
        GuidedDecisionTable52 newDTModel;
        if ( model instanceof GuidedDecisionTable ) {
            GuidedDecisionTable legacyDTModel = (GuidedDecisionTable) model;
            newDTModel = upgrader1.upgrade( legacyDTModel );
        } else {
            newDTModel = (GuidedDecisionTable52) model;
        }

        //Upgrade RowNumber, Salience and Duration data-types are correct
        newDTModel = upgrader2.upgrade( newDTModel );

        //Upgrade Default Values to typed equivalents
        newDTModel = upgrader3.upgrade( newDTModel );

        return newDTModel;
    }

}
