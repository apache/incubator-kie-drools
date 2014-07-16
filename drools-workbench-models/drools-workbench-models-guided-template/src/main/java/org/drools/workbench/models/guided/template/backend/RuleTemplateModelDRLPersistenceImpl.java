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

package org.drools.workbench.models.guided.template.backend;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.util.IoUtils;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.commons.backend.rule.RuleModelPersistence;
import org.drools.workbench.models.commons.backend.rule.context.LHSGeneratorContext;
import org.drools.workbench.models.commons.backend.rule.context.LHSGeneratorContextFactory;
import org.drools.workbench.models.commons.backend.rule.context.RHSGeneratorContext;
import org.drools.workbench.models.commons.backend.rule.context.RHSGeneratorContextFactory;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.builder.DRLConstraintValueBuilder;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class persists a {@link TemplateModel} to DRL template
 */
public class RuleTemplateModelDRLPersistenceImpl
        extends RuleModelDRLPersistenceImpl {

    private static final Pattern patternTemplateKey = Pattern.compile( "@\\{(.+?)\\}" );

    private static final Logger log = LoggerFactory.getLogger( RuleTemplateModelDRLPersistenceImpl.class );
    private static final RuleModelPersistence INSTANCE = new RuleTemplateModelDRLPersistenceImpl();

    private RuleTemplateModelDRLPersistenceImpl() {
        super();
    }

    public static RuleModelPersistence getInstance() {
        return INSTANCE;
    }

    @Override
    protected LHSPatternVisitor getLHSPatternVisitor( final boolean isDSLEnhanced,
                                                      final StringBuilder buf,
                                                      final String nestedIndentation,
                                                      final boolean isNegated,
                                                      final LHSGeneratorContextFactory generatorContextFactory ) {
        return new LHSPatternVisitor( isDSLEnhanced,
                                      bindingsPatterns,
                                      bindingsFields,
                                      constraintValueBuilder,
                                      generatorContextFactory,
                                      buf,
                                      nestedIndentation,
                                      isNegated );
    }

    @Override
    protected RHSActionVisitor getRHSActionVisitor( final boolean isDSLEnhanced,
                                                    final StringBuilder buf,
                                                    final String indentation,
                                                    final RHSGeneratorContextFactory generatorContextFactory ) {
        return new RHSActionVisitor( isDSLEnhanced,
                                     bindingsPatterns,
                                     bindingsFields,
                                     constraintValueBuilder,
                                     generatorContextFactory,
                                     buf,
                                     indentation );
    }

    public static class LHSPatternVisitor extends RuleModelDRLPersistenceImpl.LHSPatternVisitor {

        public LHSPatternVisitor( final boolean isDSLEnhanced,
                                  final Map<String, IFactPattern> bindingsPatterns,
                                  final Map<String, FieldConstraint> bindingsFields,
                                  final DRLConstraintValueBuilder constraintValueBuilder,
                                  final LHSGeneratorContextFactory generatorContextFactory,
                                  final StringBuilder b,
                                  final String indentation,
                                  final boolean isPatternNegated ) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   generatorContextFactory,
                   b,
                   indentation,
                   isPatternNegated );
        }

        @Override
        public void preGenerateConstraints( LHSGeneratorContext gctx ) {
            buf.append( "@code{hasOutput" + gctx.getDepth() + "_" + gctx.getOffset() + " = false}" );
        }

        @Override
        public void preGenerateNestedConnector( LHSGeneratorContext gctx ) {
            if ( gctx.getVarsInScope().size() > 0 ) {
                buf.append( "@if{(" );
                for ( String var : gctx.getVarsInScope() ) {
                    buf.append( var + " != empty || " );
                }
                buf.delete( buf.length() - 4, buf.length() );

                LHSGeneratorContext parentContext = gctx.getParent();
                if ( parentContext != null ) {
                    Set<String> parentVarsInScope = new HashSet<String>( parentContext.getVarsInScope() );
                    parentVarsInScope.removeAll( gctx.getVarsInScope() );
                    if ( parentVarsInScope.size() > 0 ) {
                        buf.append( ") && !(" );
                        for ( String var : parentVarsInScope ) {
                            buf.append( var + " == empty && " );
                        }
                        buf.delete( buf.length() - 4, buf.length() );
                    }
                }
                buf.append( ") || hasNonTemplateOutput" ).append( gctx.getDepth() + "_" + gctx.getOffset() ).append( "}" );
            } else {
                LHSGeneratorContext parentContext = gctx.getParent();
                if ( parentContext != null ) {
                    Set<String> parentVarsInScope = new HashSet<String>( parentContext.getVarsInScope() );
                    parentVarsInScope.removeAll( gctx.getVarsInScope() );
                    if ( parentVarsInScope.size() > 0 ) {
                        buf.append( "@if{!(" );
                        for ( String var : parentVarsInScope ) {
                            buf.append( var + " == empty || " );
                        }
                        buf.delete( buf.length() - 4, buf.length() );
                        buf.append( ")}" );
                    }
                }
            }
        }

        @Override
        public void postGenerateNestedConnector( LHSGeneratorContext gctx ) {
            if ( gctx.getVarsInScope().size() > 0 ) {
                buf.append( "@end{}" );
            } else {
                LHSGeneratorContext parentContext = gctx.getParent();
                if ( parentContext != null ) {
                    Set<String> parentVarsInScope = new HashSet<String>( parentContext.getVarsInScope() );
                    parentVarsInScope.removeAll( gctx.getVarsInScope() );
                    if ( parentVarsInScope.size() > 0 ) {
                        buf.append( "@end{}" );
                    }
                }
            }
        }

        @Override
        public void preGenerateNestedConstraint( LHSGeneratorContext gctx ) {
            if ( gctx.getVarsInScope().size() > 0 ) {
                buf.append( "@if{!(" );
                for ( String var : gctx.getVarsInScope() ) {
                    buf.append( var + " == empty && " );
                }
                buf.delete( buf.length() - 4, buf.length() );
                buf.append( ") || hasNonTemplateOutput" ).append( gctx.getDepth() + "_" + gctx.getOffset() ).append( "}" );
            }
        }

        @Override
        public void postGenerateNestedConstraint( LHSGeneratorContext gctx ) {
            if ( gctx.getVarsInScope().size() > 0 ) {
                buf.append( "@end{}" );
            }
        }

        @Override
        protected void generateConstraint( final FieldConstraint constr,
                                           LHSGeneratorContext gctx ) {
            boolean generateTemplateCheck = isTemplateKey( constr );

            if ( generateTemplateCheck ) {
                if ( constr instanceof SingleFieldConstraint && ( (SingleFieldConstraint) constr ).getConnectives() != null ) {
                    // if there are connectives, and the first is a template key, then all templates keys must be checked up front
                    // individual connectives, that have template keys, will still need to be checked too.
                    SingleFieldConstraint sconstr = (SingleFieldConstraint) constr;
                    buf.append( "@if{" + ( (SingleFieldConstraint) constr ).getValue() + " != empty" );
                    for ( int j = 0; j < sconstr.getConnectives().length; j++ ) {
                        final ConnectiveConstraint conn = sconstr.getConnectives()[ j ];
                        if ( conn.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_TEMPLATE ) {
                            buf.append( " || " + conn.getValue() + " != empty" );
                        }
                    }
                    buf.append( "}" );
                } else {
                    buf.append( "@if{" + ( (SingleFieldConstraint) constr ).getValue() + " != empty}" );
                }
            }
            super.generateConstraint( constr,
                                      gctx );
            buf.append( "@code{hasOutput" + gctx.getDepth() + "_" + gctx.getOffset() + " = true}" );
            if ( generateTemplateCheck ) {
                buf.append( "@end{}" );
            }
        }

        private boolean isTemplateKey( FieldConstraint nestedConstr ) {
            return nestedConstr instanceof BaseSingleFieldConstraint && ( (BaseSingleFieldConstraint) nestedConstr ).getConstraintValueType() == BaseSingleFieldConstraint.TYPE_TEMPLATE;
        }

        public void generateSeparator( FieldConstraint constr,
                                       LHSGeneratorContext gctx ) {
            if ( !gctx.isHasOutput() ) {
                return;
            }

            boolean generateTemplateCheck = isTemplateKey( constr );
            if ( generateTemplateCheck ) {
                buf.append( "@if{ hasOutput" + gctx.getDepth() + "_" + gctx.getOffset() + "}" );
            }

            preGenerateNestedConnector( gctx );
            if ( gctx.getDepth() == 0 ) {
                buf.append( ", " );
            } else {
                CompositeFieldConstraint cconstr = (CompositeFieldConstraint) gctx.getParent().getFieldConstraint();
                buf.append( cconstr.getCompositeJunctionType() + " " );
            }
            postGenerateNestedConnector( gctx );
            if ( generateTemplateCheck ) {
                buf.append( "@end{}" );
            }
        }

        @Override
        protected void addConnectiveFieldRestriction( final StringBuilder buf,
                                                      final int type,
                                                      final String fieldType,
                                                      String operator,
                                                      final Map<String, String> parameters,
                                                      final String value,
                                                      final ExpressionFormLine expression,
                                                      final LHSGeneratorContext gctx,
                                                      boolean spaceBeforeOperator ) {
            boolean generateTemplateCheck = type == BaseSingleFieldConstraint.TYPE_TEMPLATE;
            if ( generateTemplateCheck ) {
                buf.append( "@if{" + value + " != empty}" );
            }

            if ( generateTemplateCheck && operator.startsWith( "||" ) || operator.startsWith( "&&" ) ) {
                spaceBeforeOperator = false;
                buf.append( "@if{ hasOutput" + gctx.getDepth() + "_" + gctx.getOffset() + "} " );// add space here, due to split operator
                buf.append( operator.substring( 0, 2 ) );
                buf.append( "@end{}" );
                operator = operator.substring( 2 );
            }

            super.addConnectiveFieldRestriction( buf, type, fieldType, operator, parameters, value, expression, gctx, spaceBeforeOperator );

            if ( generateTemplateCheck ) {
                buf.append( "@code{hasOutput" + gctx.getDepth() + "_" + gctx.getOffset() + " = true}" );
                buf.append( "@end{}" );
            }
        }

        @Override
        public void visitFreeFormLine( final FreeFormLine ffl ) {
            if ( ffl.getText() == null ) {
                return;
            }
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            boolean found = matcherTemplateKey.find();
            if ( found ) {
                buf.append( "@if{" );
                boolean addAnd = false;
                while ( found ) {
                    String varName = matcherTemplateKey.group( 1 );
                    if ( addAnd ) {
                        buf.append( " && " );
                    }
                    buf.append( varName + " != empty" );
                    addAnd = true;
                    found = matcherTemplateKey.find();
                }
                buf.append( "}" );
                super.visitFreeFormLine( ffl );
                buf.append( "@end{}" );
            } else {
                // no variables found
                super.visitFreeFormLine( ffl );
            }
        }

        @Override
        public void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern,
                                                          final boolean isSubPattern ) {

            if ( pattern.getRightPattern() instanceof FreeFormLine ) {
                // this allows MVEL to skip the collect, if any vars are empty
                // note this actually duplicates another inner check for the FFL itself
                // @TODO the FFL should get a reference to the parent, so it can avoid this duplication.
                final FreeFormLine ffl = (FreeFormLine) pattern.getRightPattern();
                if ( ffl.getText() == null ) {
                    return;
                }
                final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
                boolean found = matcherTemplateKey.find();
                if ( found ) {
                    buf.append( "@if{" );
                    boolean addAnd = false;
                    while ( found ) {
                        String varName = matcherTemplateKey.group( 1 );
                        if ( addAnd ) {
                            buf.append( " && " );
                        }
                        buf.append( varName + " != empty" );
                        addAnd = true;
                        found = matcherTemplateKey.find();
                    }
                    buf.append( "}" );
                    super.visitFromCollectCompositeFactPattern( pattern,
                                                                isSubPattern );
                    buf.append( "@end{}" );
                    found = matcherTemplateKey.find();
                } else {
                    // no variables found
                    super.visitFromCollectCompositeFactPattern( pattern,
                                                                isSubPattern );
                }
            } else {
                super.visitFromCollectCompositeFactPattern( pattern,
                                                            isSubPattern );
            }
        }
    }

    public static class RHSActionVisitor extends RuleModelDRLPersistenceImpl.RHSActionVisitor {

        public RHSActionVisitor( final boolean isDSLEnhanced,
                                 final Map<String, IFactPattern> bindingsPatterns,
                                 final Map<String, FieldConstraint> bindingsFields,
                                 final DRLConstraintValueBuilder constraintValueBuilder,
                                 final RHSGeneratorContextFactory generatorContextFactory,
                                 final StringBuilder b,
                                 final String indentation ) {
            super( isDSLEnhanced,
                   bindingsPatterns,
                   bindingsFields,
                   constraintValueBuilder,
                   generatorContextFactory,
                   b,
                   indentation );
        }

        @Override
        protected void generateSetMethodCall( final String variableName,
                                              final ActionFieldValue fieldValue ) {
            if ( fieldValue.getNature() == FieldNatureType.TYPE_TEMPLATE ) {
                buf.append( "@if{" + fieldValue.getValue() + " != empty}" );
                super.generateSetMethodCall( variableName,
                                             fieldValue );
                buf.append( "@end{}" );
            } else {
                super.generateSetMethodCall( variableName,
                                             fieldValue );

            }
        }

        @Override
        protected void preGenerateSetMethodCallParameterValue( final ActionFieldValue fieldValue,
                                                               final RHSGeneratorContext gctx ) {
            if ( fieldValue.getNature() == FieldNatureType.TYPE_TEMPLATE ) {
                buf.append( "@if{" + fieldValue.getValue() + " != empty}" );
                buf.append( "@code{hasOutput" + gctx.getOffset() + " = true}" );
                super.preGenerateSetMethodCallParameterValue( fieldValue,
                                                              gctx );
                buf.append( "@end{}" );
            } else {
                buf.append( "@code{hasOutput" + gctx.getOffset() + " = true}" );
                super.preGenerateSetMethodCallParameterValue( fieldValue,
                                                              gctx );
            }
        }

        @Override
        protected void generateModifyMethodCall( final RHSGeneratorContext gctx,
                                                 final ActionFieldValue fieldValue ) {
            if ( fieldValue.getNature() == FieldNatureType.TYPE_TEMPLATE ) {
                buf.append( "@if{" + fieldValue.getValue() + " != empty}" );
                super.generateModifyMethodCall( gctx,
                                                fieldValue );
                buf.append( "@end{}" );

            } else {
                super.generateModifyMethodCall( gctx,
                                                fieldValue );
            }
        }

        @Override
        protected void generateModifyMethodSeparator( final RHSGeneratorContext gctx,
                                                      final ActionFieldValue fieldValue ) {
            if ( gctx.getParent() == null ) {
                return;
            }
            buf.append( "@if{hasOutput" + gctx.getOffset() + " && (" );
            RHSGeneratorContext parent = gctx.getParent();
            while ( parent != null ) {
                buf.append( "hasOutput" + parent.getOffset() + ( parent.getParent() != null ? " || " : "" ) );
                parent = parent.getParent();
            }
            buf.append( ")}" );
            buf.append( ", \n" );
            buf.append( "@end{}" );
        }

        @Override
        public void visitFreeFormLine( FreeFormLine ffl ) {
            if ( ffl.getText() == null ) {
                return;
            }
            final Matcher matcherTemplateKey = patternTemplateKey.matcher( ffl.getText() );
            boolean found = matcherTemplateKey.find();
            if ( found ) {
                buf.append( "@if{" );
                boolean addAnd = false;
                while ( found ) {
                    String varName = matcherTemplateKey.group( 1 );
                    if ( addAnd ) {
                        buf.append( " && " );
                    }
                    buf.append( varName + " != empty" );
                    addAnd = true;
                    found = matcherTemplateKey.find();
                }
                buf.append( "}" );
                super.visitFreeFormLine( ffl );
                buf.append( "@end{}" );
            } else {
                // no variables found
                super.visitFreeFormLine( ffl );
            }
        }
    }

    @Override
    public String marshal( final RuleModel model ) {

        //Build rule
        final String ruleTemplate = marshalRule( model );
        log.debug( "ruleTemplate:\n{}",
                   ruleTemplate );

        log.debug( "generated template:\n{}", ruleTemplate );

        final DataProvider dataProvider = chooseDataProvider( model );
        final DataProviderCompiler tplCompiler = new DataProviderCompiler();
        final String generatedDrl = tplCompiler.compile( dataProvider,
                                                         new ByteArrayInputStream( ruleTemplate.getBytes( IoUtils.UTF8_CHARSET ) ),
                                                         false );

        log.debug( "generated drl:\n{}", generatedDrl );

        return generatedDrl;
    }

    @Override
    protected String marshalRule( final RuleModel model ) {
        boolean isDSLEnhanced = model.hasDSLSentences();
        bindingsPatterns = new HashMap<String, IFactPattern>();
        bindingsFields = new HashMap<String, FieldConstraint>();

        fixActionInsertFactBindings( model.rhs );

        StringBuilder buf = new StringBuilder();
        StringBuilder header = new StringBuilder();
        LHSGeneratorContextFactory lhsGeneratorContextFactory = new LHSGeneratorContextFactory();
        RHSGeneratorContextFactory rhsGeneratorContextFactory = new RHSGeneratorContextFactory();

        //Build rule
        this.marshalRuleHeader( model,
                                header );
        super.marshalMetadata( buf,
                               model );
        super.marshalAttributes( buf,
                                 model );

        buf.append( "\twhen\n" );

        super.marshalLHS( buf,
                          model,
                          isDSLEnhanced,
                          lhsGeneratorContextFactory );
        buf.append( "\tthen\n" );
        super.marshalRHS( buf,
                          model,
                          isDSLEnhanced,
                          rhsGeneratorContextFactory );
        this.marshalFooter( buf );

        for ( LHSGeneratorContext gc : lhsGeneratorContextFactory.getGeneratorContexts() ) {
            header.append( "@code{hasNonTemplateOutput" + gc.getDepth() + "_" + gc.getOffset() + " = " + gc.hasNonTemplateOutput() + "}" );
        }
        for ( RHSGeneratorContext gc : rhsGeneratorContextFactory.getGeneratorContexts() ) {
            header.append( "@code{hasOutput" + gc.getOffset() + " = false}" );
        }

        return header.append( buf ).toString();
    }

    private DataProvider chooseDataProvider( final RuleModel model ) {
        DataProvider dataProvider;
        TemplateModel tplModel = (TemplateModel) model;
        if ( tplModel.getRowsCount() > 0 ) {
            dataProvider = new ArrayDataProvider( tplModel.getTableAsArray() );
        } else {
            dataProvider = generateEmptyIterator();
        }
        return dataProvider;
    }

    private DataProvider generateEmptyIterator() {
        return new DataProvider() {

            public boolean hasNext() {
                return false;
            }

            public String[] next() {
                return new String[ 0 ];
            }
        };
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
