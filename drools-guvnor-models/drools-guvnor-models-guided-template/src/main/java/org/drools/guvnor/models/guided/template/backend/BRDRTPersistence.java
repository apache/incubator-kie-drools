/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.models.guided.template.backend;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.drools.guvnor.models.commons.backend.rule.BRDRLPersistence;
import org.drools.guvnor.models.commons.backend.rule.BRLPersistence;
import org.drools.guvnor.models.commons.shared.rule.FieldConstraint;
import org.drools.guvnor.models.commons.shared.rule.IFactPattern;
import org.drools.guvnor.models.commons.shared.rule.InterpolationVariable;
import org.drools.guvnor.models.commons.shared.rule.RuleModel;
import org.drools.guvnor.models.guided.template.shared.TemplateModel;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class persists a {@link TemplateModel} to DRL template
 */
public class BRDRTPersistence
        extends BRDRLPersistence {

    private static final Logger log = LoggerFactory.getLogger( BRDRTPersistence.class );
    private static final BRLPersistence INSTANCE = new BRDRTPersistence();

    private BRDRTPersistence() {
        super();
    }

    public static BRLPersistence getInstance() {
        return INSTANCE;
    }

    @Override
    public String marshal( final RuleModel model ) {

        //Build rule
        final String ruleTemplate = marshalRule( model );
        log.debug( "ruleTemplate:\n{}",
                   ruleTemplate );

        final DataProvider dataProvider = chooseDataProvider( model );
        final DataProviderCompiler tplCompiler = new DataProviderCompiler();
        final String generatedDRl = tplCompiler.compile( dataProvider,
                                                         new ByteArrayInputStream( ruleTemplate.getBytes() ) );
        log.debug( "generated drl:\n{}",
                   generatedDRl );

        return generatedDRl;
    }

    protected String marshalRule( final RuleModel model ) {
        boolean isDSLEnhanced = model.hasDSLSentences();
        bindingsPatterns = new HashMap<String, IFactPattern>();
        bindingsFields = new HashMap<String, FieldConstraint>();

        StringBuilder buf = new StringBuilder();

        //Build rule
        this.marshalRuleHeader( model,
                                buf );
        super.marshalMetadata( buf,
                               model );
        super.marshalAttributes( buf,
                                 model );

        buf.append( "\twhen\n" );
        super.marshalLHS( buf,
                          model,
                          isDSLEnhanced );
        buf.append( "\tthen\n" );
        super.marshalRHS( buf,
                          model,
                          isDSLEnhanced );
        this.marshalFooter( buf );
        return buf.toString();
    }

    private DataProvider chooseDataProvider( final RuleModel model ) {
        DataProvider dataProvider;
        TemplateModel tplModel = (TemplateModel) model;
        if ( tplModel.getRowsCount() > 0 ) {
            dataProvider = new ArrayDataProvider( tplModel.getTableAsArray() );
        } else {
            dataProvider = new ArrayDataProvider( generateEmptyIterator( tplModel ) );
        }
        return dataProvider;
    }

    private String[][] generateEmptyIterator( final TemplateModel templateModel ) {
        String[][] rows = new String[ 1 ][];

        InterpolationVariable[] interpolationVariables = templateModel.getInterpolationVariablesList();
        if ( interpolationVariables == null || interpolationVariables.length == 0 ) {
            rows[ 0 ] = new String[]{ "" };
        } else {
            rows[ 0 ] = new String[ interpolationVariables.length ];
            for ( int i = 0; i < interpolationVariables.length; i++ ) {
                rows[ 0 ][ i ] = interpolationVariables[ i ].getVarName() + "_na";
            }
        }
        return rows;
    }

    @Override
    protected void marshalRuleHeader( final RuleModel model,
                                      final StringBuilder buf ) {
        //Append Template header
        TemplateModel templateModel = (TemplateModel) model;
        buf.append( "template header\n" );

        InterpolationVariable[] interpolationVariables = templateModel.getInterpolationVariablesList();
        if ( interpolationVariables.length == 0 ) {
            buf.append( "test_var" ).append( '\n' );
        } else {
            for ( InterpolationVariable var : interpolationVariables ) {
                buf.append( var.getVarName() ).append( '\n' );
            }
        }
        buf.append( "\n" );

        //Append Package header
        super.marshalPackageHeader( model,
                                    buf );

        //Append Template definition
        buf.append( "\ntemplate \"" ).append( super.marshalRuleName( templateModel ) ).append( "\"\n\n" );
        super.marshalRuleHeader( model,
                                 buf );
    }

    @Override
    protected String marshalRuleName( final RuleModel model ) {
        return super.marshalRuleName( model ) + "_@{row.rowNumber}";
    }

    @Override
    protected void marshalFooter( final StringBuilder buf ) {
        super.marshalFooter( buf );
        buf.append( "\nend template" );
    }

}
