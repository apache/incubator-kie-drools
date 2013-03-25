/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.models.guided.dtable.backend.util;

import org.drools.guvnor.models.commons.backend.rule.BRDRLPersistence;
import org.drools.guvnor.models.commons.backend.rule.DRLConstraintValueBuilder;
import org.drools.guvnor.models.commons.shared.rule.ActionFieldValue;
import org.drools.guvnor.models.commons.shared.rule.FieldConstraint;
import org.drools.guvnor.models.commons.shared.rule.FreeFormLine;
import org.drools.guvnor.models.commons.shared.rule.IFactPattern;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A specialised implementation of BRDELPersistence that can expand Template
 * Keys to values
 */
public class GuidedDTBRDRLPersistence extends BRDRLPersistence {

    private TemplateDataProvider rowDataProvider;

    private static final Pattern patternTemplateKey = Pattern.compile( "@\\{(.+?)\\}" );

    public GuidedDTBRDRLPersistence( TemplateDataProvider rowDataProvider ) {
        if ( rowDataProvider == null ) {
            throw new NullPointerException( "rowDataProvider cannot be null" );
        }
        this.rowDataProvider = rowDataProvider;
    }

    @Override
    protected LHSPatternVisitor getLHSPatternVisitor( boolean isDSLEnhanced,
                                                      StringBuilder buf,
                                                      String nestedIndentation,
                                                      boolean isNegated ) {
        return new LHSPatternVisitor( isDSLEnhanced,
                                      rowDataProvider,
                                      bindingsPatterns,
                                      bindingsFields,
                                      constraintValueBuilder,
                                      buf,
                                      nestedIndentation,
                                      isNegated );
    }

    @Override
    protected RHSActionVisitor getRHSActionVisitor( boolean isDSLEnhanced,
                                                    StringBuilder buf,
                                                    String indentation ) {
        return new RHSActionVisitor( isDSLEnhanced,
                                     rowDataProvider,
                                     bindingsPatterns,
                                     bindingsFields,
                                     constraintValueBuilder,
                                     buf,
                                     indentation );
    }

    //Substitutes Template Keys for values
    public static class LHSPatternVisitor extends BRDRLPersistence.LHSPatternVisitor {

        private TemplateDataProvider rowDataProvider;

        public LHSPatternVisitor( boolean isDSLEnhanced,
                                  TemplateDataProvider rowDataProvider,
                                  Map<String, IFactPattern> bindingsPatterns,
                                  Map<String, FieldConstraint> bindingsFields,
                                  DRLConstraintValueBuilder constraintValueBuilder,
                                  StringBuilder b,
                                  String indentation,
                                  boolean isPatternNegated ) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   b,
                   indentation,
                   isPatternNegated );
            this.rowDataProvider = rowDataProvider;
        }

        @Override
        protected void buildTemplateFieldValue( int type,
                                                String fieldType,
                                                String value,
                                                StringBuilder buf ) {
            buf.append( " " );
            constraintValueBuilder.buildLHSFieldValue( buf,
                                                       type,
                                                       fieldType,
                                                       rowDataProvider.getTemplateKeyValue( value ) );
            buf.append( " " );
        }

        @Override
        public void visitFreeFormLine( FreeFormLine ffl ) {
            StringBuffer interpolatedResult = new StringBuffer();
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            while ( matcherTemplateKey.find() ) {
                String varName = matcherTemplateKey.group( 1 );
                matcherTemplateKey.appendReplacement( interpolatedResult,
                                                      rowDataProvider.getTemplateKeyValue( varName ) );
            }
            matcherTemplateKey.appendTail( interpolatedResult );

            //Don't update the original FreeFormLine object
            FreeFormLine fflClone = new FreeFormLine();
            fflClone.setText( interpolatedResult.toString() );
            super.visitFreeFormLine( fflClone );
        }

    }

    //Substitutes Template Keys for values
    public static class RHSActionVisitor extends BRDRLPersistence.RHSActionVisitor {

        private TemplateDataProvider rowDataProvider;

        public RHSActionVisitor( boolean isDSLEnhanced,
                                 TemplateDataProvider rowDataProvider,
                                 Map<String, IFactPattern> bindingsPatterns,
                                 Map<String, FieldConstraint> bindingsFields,
                                 DRLConstraintValueBuilder constraintValueBuilder,
                                 StringBuilder b,
                                 String indentation ) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   b,
                   indentation );
            this.rowDataProvider = rowDataProvider;
        }

        @Override
        protected void buildTemplateFieldValue( ActionFieldValue fieldValue,
                                                StringBuilder buf ) {
            constraintValueBuilder.buildRHSFieldValue( buf,
                                                       fieldValue.getType(),
                                                       rowDataProvider.getTemplateKeyValue( fieldValue.getValue() ) );
        }

        @Override
        public void visitFreeFormLine( FreeFormLine ffl ) {

            StringBuffer interpolatedResult = new StringBuffer();
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            while ( matcherTemplateKey.find() ) {
                String varName = matcherTemplateKey.group( 1 );
                matcherTemplateKey.appendReplacement( interpolatedResult,
                                                      rowDataProvider.getTemplateKeyValue( varName ) );
            }
            matcherTemplateKey.appendTail( interpolatedResult );

            //Don't update the original FreeFormLine object
            FreeFormLine fflClone = new FreeFormLine();
            fflClone.setText( interpolatedResult.toString() );
            super.visitFreeFormLine( fflClone );
        }

    }

}
