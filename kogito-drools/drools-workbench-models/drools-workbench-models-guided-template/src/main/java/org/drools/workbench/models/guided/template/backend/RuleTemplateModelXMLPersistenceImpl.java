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

package org.drools.workbench.models.guided.template.backend;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.workbench.models.commons.backend.rule.DSLVariableValuesConverter;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.ExpressionCollection;
import org.drools.workbench.models.datamodel.rule.ExpressionCollectionIndex;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionGlobalVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.backend.upgrade.RuleModelUpgradeHelper1;
import org.drools.workbench.models.guided.template.backend.upgrade.RuleModelUpgradeHelper2;
import org.drools.workbench.models.guided.template.backend.upgrade.RuleModelUpgradeHelper3;
import org.drools.workbench.models.guided.template.backend.upgrade.TemplateModelUpgradeHelper1;
import org.drools.workbench.models.guided.template.shared.TemplateModel;

/**
 * This class persists the rule model to XML and back. This is the 'brl' xml
 * format (Business Rule Language).
 */
public class RuleTemplateModelXMLPersistenceImpl
        implements RuleTemplateModelPersistence {

    protected XStream xt;

    private static final RuleModelUpgradeHelper1 ruleModelUpgrader1 = new RuleModelUpgradeHelper1();
    private static final RuleModelUpgradeHelper2 ruleModelUpgrader2 = new RuleModelUpgradeHelper2();
    private static final RuleModelUpgradeHelper3 ruleModelUpgrader3 = new RuleModelUpgradeHelper3();

    private static final TemplateModelUpgradeHelper1 ruleTemplateModelUpgrader1 = new TemplateModelUpgradeHelper1();

    private static final RuleTemplateModelPersistence INSTANCE = new RuleTemplateModelXMLPersistenceImpl();

    protected RuleTemplateModelXMLPersistenceImpl() {
        this.xt = new XStream( new DomDriver() );

        this.xt.alias( "rule",
                       TemplateModel.class );
        this.xt.alias( "fact",
                       FactPattern.class );
        this.xt.alias( "retract",
                       ActionRetractFact.class );
        this.xt.alias( "assert",
                       ActionInsertFact.class );
        this.xt.alias( "modify",
                       ActionUpdateField.class );
        this.xt.alias( "setField",
                       ActionSetField.class );
        this.xt.alias( "dslSentence",
                       DSLSentence.class );
        this.xt.alias( "compositePattern",
                       CompositeFactPattern.class );
        this.xt.alias( "fromCompositePattern",
                       FromCompositeFactPattern.class );
        this.xt.alias( "fromCollectCompositePattern",
                       FromCollectCompositeFactPattern.class );
        this.xt.alias( "fromAccumulateCompositePattern",
                       FromAccumulateCompositeFactPattern.class );
        this.xt.alias( "metadata",
                       RuleMetadata.class );
        this.xt.alias( "attribute",
                       RuleAttribute.class );

        this.xt.alias( "fieldValue",
                       ActionFieldValue.class );
        this.xt.alias( "connectiveConstraint",
                       ConnectiveConstraint.class );
        this.xt.alias( "fieldConstraint",
                       SingleFieldConstraint.class );

        this.xt.alias( "compositeConstraint",
                       CompositeFieldConstraint.class );

        this.xt.alias( "assertLogical",
                       ActionInsertLogicalFact.class );
        this.xt.alias( "freeForm",
                       FreeFormLine.class );

        this.xt.alias( "addToGlobal",
                       ActionGlobalCollectionAdd.class );
        //Begin ExpressionFormLine
        this.xt.alias( "expression",
                       ExpressionFormLine.class );

        this.xt.alias( "field",
                       ExpressionField.class );

        this.xt.alias( "method",
                       ExpressionMethod.class );

        this.xt.alias( "collection",
                       ExpressionCollection.class );

        this.xt.alias( "collectionIndex",
                       ExpressionCollectionIndex.class );

        this.xt.alias( "text",
                       ExpressionText.class );

        this.xt.alias( "global",
                       ExpressionGlobalVariable.class );

        this.xt.alias( "variable",
                       ExpressionVariable.class );
        //end ExpressionFormLine

        //See https://issues.jboss.org/browse/GUVNOR-1115
        this.xt.aliasPackage( "org.drools.guvnor.client",
                              "org.drools.ide.common.client" );

        this.xt.aliasPackage( "org.drools.guvnor.client.modeldriven.brl",
                              "org.kie.guvnor.datamodel.model" );

        //Legacy DSLSentences have a collection of String values whereas newer persisted models
        //have a collection of DSLVariableValues. See https://issues.jboss.org/browse/GUVNOR-1872
        this.xt.registerLocalConverter( DSLSentence.class,
                                        "values",
                                        new DSLVariableValuesConverter( this.xt.getMapper() ) );

    }

    public static RuleTemplateModelPersistence getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.server.util.RuleModelPersistence#toXML(org.drools.guvnor
     * .client.modeldriven.brl.RuleModel)
     */
    @Override
    public String marshal( final TemplateModel model ) {
        model.putInSync();
        return this.xt.toXML( model );
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.server.util.RuleModelPersistence#toModel(java.lang.String
     * )
     */
    @Override
    public TemplateModel unmarshal( final String xml ) {
        if ( xml == null || xml.trim().length() == 0 ) {
            return createEmptyModel();
        }
        TemplateModel model = (TemplateModel) this.xt.fromXML( xml );

        //Upgrade model changes to legacy artifacts
        ruleModelUpgrader1.upgrade( model );
        ruleModelUpgrader2.upgrade( model );
        ruleModelUpgrader3.upgrade( model );

        ruleTemplateModelUpgrader1.upgrade( model );

        model.putInSync();

        return model;
    }

    protected TemplateModel createEmptyModel() {
        return new TemplateModel();
    }

}
