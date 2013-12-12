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
package org.drools.workbench.models.guided.dtable.backend.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.util.StringUtils;
import org.drools.workbench.models.commons.backend.rule.DRLConstraintValueBuilder;
import org.drools.workbench.models.commons.backend.rule.GeneratorContext;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;

/**
 * A specialised implementation of BRDELPersistence that can expand Template
 * Keys to values
 */
public class GuidedDTBRDRLPersistence extends RuleModelDRLPersistenceImpl {

    private TemplateDataProvider rowDataProvider;

    private static final Pattern patternTemplateKey = Pattern.compile( "@\\{(.+?)\\}" );

    public GuidedDTBRDRLPersistence( final TemplateDataProvider rowDataProvider ) {
        if ( rowDataProvider == null ) {
            throw new NullPointerException( "rowDataProvider cannot be null" );
        }
        this.rowDataProvider = rowDataProvider;
    }

    @Override
    protected LHSPatternVisitor getLHSPatternVisitor( final boolean isDSLEnhanced,
                                                      final StringBuilder buf,
                                                      final String nestedIndentation,
                                                      final boolean isNegated ) {
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
    protected RHSActionVisitor getRHSActionVisitor( final boolean isDSLEnhanced,
                                                    final StringBuilder buf,
                                                    final String indentation ) {
        return new RHSActionVisitor( isDSLEnhanced,
                                     rowDataProvider,
                                     bindingsPatterns,
                                     bindingsFields,
                                     constraintValueBuilder,
                                     buf,
                                     indentation );
    }

    //Substitutes Template Keys for values
    public static class LHSPatternVisitor extends RuleModelDRLPersistenceImpl.LHSPatternVisitor {

        private TemplateDataProvider rowDataProvider;

        public LHSPatternVisitor( final boolean isDSLEnhanced,
                                  final TemplateDataProvider rowDataProvider,
                                  final Map<String, IFactPattern> bindingsPatterns,
                                  final Map<String, FieldConstraint> bindingsFields,
                                  final DRLConstraintValueBuilder constraintValueBuilder,
                                  final StringBuilder b,
                                  final String indentation,
                                  final boolean isPatternNegated ) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   b,
                   indentation,
                   isPatternNegated );
            this.rowDataProvider = rowDataProvider;
        }

        protected boolean isValidFieldConstraint( final FieldConstraint constr ) {
            if ( constr instanceof SingleFieldConstraint && ( (SingleFieldConstraint) constr ).getConstraintValueType() == BaseSingleFieldConstraint.TYPE_TEMPLATE ) {
                return !StringUtils.isEmpty( rowDataProvider.getTemplateKeyValue( ( (SingleFieldConstraint) constr ).getValue() ) );
            }
            return true;
        }

        @Override
        protected void generateConstraint( final FieldConstraint constr,
                                           GeneratorContext gctx) {
            if ( isValidFieldConstraint( constr ) ) {
                super.generateConstraint( constr,
                                          gctx);
            }
        }

        protected void addConnectiveFieldRestriction( final StringBuilder buf,
                                                      final int type,
                                                      final String fieldType,
                                                      String operator,
                                                      final Map<String, String> parameters,
                                                      final String value,
                                                      final ExpressionFormLine expression,
                                                      GeneratorContext gctx,
                                                      final boolean spaceBeforeOperator   ) {
            boolean generateTemplateCheck = type == BaseSingleFieldConstraint.TYPE_TEMPLATE;
            if ( generateTemplateCheck && !gctx.isHasOutput() && operator.startsWith( "||") || operator.startsWith( "&&")  ) {
                operator = operator.substring(2);
            }
            super.addConnectiveFieldRestriction(buf, type, fieldType, operator, parameters, value, expression, gctx, true);
        }


        @Override
        protected void buildTemplateFieldValue( final int type,
                                                final String fieldType,
                                                final String value,
                                                final StringBuilder buf ) {
            buf.append( " " );
            constraintValueBuilder.buildLHSFieldValue( buf,
                                                       type,
                                                       fieldType,
                                                       rowDataProvider.getTemplateKeyValue( value ) );
            buf.append( " " );
        }

        @Override
        public void visitFreeFormLine( final FreeFormLine ffl ) {
            StringBuffer interpolatedResult = new StringBuffer();
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            while ( matcherTemplateKey.find() ) {
                String varName = matcherTemplateKey.group( 1 );
                String value = rowDataProvider.getTemplateKeyValue( varName );

                // All vars must be populated for a single FreeFormLine
                if ( StringUtils.isEmpty( value ) ) {
                    return;
                }

                matcherTemplateKey.appendReplacement( interpolatedResult,
                                                      value );
            }
            matcherTemplateKey.appendTail( interpolatedResult );

            //Don't update the original FreeFormLine object
            FreeFormLine fflClone = new FreeFormLine();
            fflClone.setText( interpolatedResult.toString() );
            super.visitFreeFormLine( fflClone );
        }

        public void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern,
                                                          final boolean isSubPattern ) {

            if ( pattern.getRightPattern() instanceof FreeFormLine ) {
                // must skip the collect, if the any variable is empty for the FFL
                final FreeFormLine ffl = (FreeFormLine) pattern.getRightPattern();

                final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
                while ( matcherTemplateKey.find() ) {
                    String varName = matcherTemplateKey.group( 1 );
                    String value = rowDataProvider.getTemplateKeyValue( varName );

                    // All vars must be populated for a single FreeFormLine
                    if ( StringUtils.isEmpty( value ) ) {
                        return;
                    }
                }
            }
            super.visitFromCollectCompositeFactPattern( pattern,
                                                        isSubPattern );

        }

    }

    //Substitutes Template Keys for values
    public static class RHSActionVisitor extends RuleModelDRLPersistenceImpl.RHSActionVisitor {

        private TemplateDataProvider rowDataProvider;

        public RHSActionVisitor( final boolean isDSLEnhanced,
                                 final TemplateDataProvider rowDataProvider,
                                 final Map<String, IFactPattern> bindingsPatterns,
                                 final Map<String, FieldConstraint> bindingsFields,
                                 final DRLConstraintValueBuilder constraintValueBuilder,
                                 final StringBuilder b,
                                 final String indentation ) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   b,
                   indentation );
            this.rowDataProvider = rowDataProvider;
        }

        @Override
        protected void buildTemplateFieldValue( final ActionFieldValue fieldValue,
                                                final StringBuilder buf ) {
            constraintValueBuilder.buildRHSFieldValue( buf,
                                                       fieldValue.getType(),
                                                       rowDataProvider.getTemplateKeyValue( fieldValue.getValue() ) );
        }

        protected boolean isValidFieldConstraint( final ActionFieldValue fieldValue ) {
            if ( fieldValue.getNature() == FieldNatureType.TYPE_TEMPLATE ) {
                return !StringUtils.isEmpty( rowDataProvider.getTemplateKeyValue( fieldValue.getValue() ) );
            }
            return true;
        }

        protected void generateSetMethodCall( final String variableName,
                                              final ActionFieldValue fieldValue ) {
            if ( isValidFieldConstraint( fieldValue ) ) {
                super.generateSetMethodCall( variableName, fieldValue );
            }
        }

        @Override
        public void visitFreeFormLine( final FreeFormLine ffl ) {

            StringBuffer interpolatedResult = new StringBuffer();
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            while ( matcherTemplateKey.find() ) {
                String varName = matcherTemplateKey.group( 1 );
                String value = rowDataProvider.getTemplateKeyValue( varName );

                // All vars must be populated for a single FreeFormLine
                if ( StringUtils.isEmpty( value ) ) {
                    return;
                }
                matcherTemplateKey.appendReplacement( interpolatedResult,
                                                      value );
            }
            matcherTemplateKey.appendTail( interpolatedResult );

            //Don't update the original FreeFormLine object
            FreeFormLine fflClone = new FreeFormLine();
            fflClone.setText( interpolatedResult.toString() );
            super.visitFreeFormLine( fflClone );
        }

    }

}
