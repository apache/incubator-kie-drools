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

package org.drools.workbench.models.commons.backend.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.util.ReflectiveVisitor;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.commons.backend.imports.ImportsWriter;
import org.drools.workbench.models.commons.backend.packages.PackageNameParser;
import org.drools.workbench.models.commons.backend.packages.PackageNameWriter;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionExecuteWorkItem;
import org.drools.workbench.models.datamodel.rule.ActionFieldFunction;
import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.ActionWorkItemFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNature;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.HasParameterizedOperator;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.models.datamodel.workitems.HasBinding;
import org.drools.workbench.models.datamodel.workitems.PortableBooleanParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableFloatParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableIntegerParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableObjectParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;

/**
 * This class persists the rule model to DRL and back
 */
public class RuleModelDRLPersistenceImpl
        implements
        RuleModelPersistence {

    private static final String WORKITEM_PREFIX = "wi";

    private static final RuleModelPersistence INSTANCE = new RuleModelDRLPersistenceImpl();

    public static final String DEFAULT_DIALECT = "mvel";

    //This is the default dialect for rules not specifying one explicitly
    protected DRLConstraintValueBuilder constraintValueBuilder = DRLConstraintValueBuilder.getBuilder( DEFAULT_DIALECT );

    //Keep a record of all variable bindings for Actions that depend on them
    protected Map<String, IFactPattern> bindingsPatterns;
    protected Map<String, FieldConstraint> bindingsFields;

    protected RuleModelDRLPersistenceImpl() {
        // register custom evaluators
        new EvaluatorRegistry( getClass().getClassLoader() );
    }

    public static RuleModelPersistence getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.server.util.RuleModelPersistence#marshal(org.drools.guvnor
     * .client.modeldriven.brl.RuleModel)
     */
    public String marshal( final RuleModel model ) {
        return marshalRule( model );
    }

    protected String marshalRule( final RuleModel model ) {
        boolean isDSLEnhanced = model.hasDSLSentences();
        bindingsPatterns = new HashMap<String, IFactPattern>();
        bindingsFields = new HashMap<String, FieldConstraint>();

        fixActionInsertFactBindings( model.rhs );

        StringBuilder buf = new StringBuilder();

        //Build rule
        this.marshalPackageHeader( model,
                                   buf );
        this.marshalRuleHeader( model,
                                buf );
        this.marshalMetadata( buf,
                              model );
        this.marshalAttributes( buf,
                                model );

        buf.append( "\twhen\n" );
        this.marshalLHS( buf,
                         model,
                         isDSLEnhanced,
                         new GeneratorContextFactory() );
        buf.append( "\tthen\n" );
        this.marshalRHS( buf,
                         model,
                         isDSLEnhanced );
        this.marshalFooter( buf );
        return buf.toString();
    }

    protected void fixActionInsertFactBindings( final IAction[] rhs ) {
        final Set<String> existingBindings = extractExistingActionBindings( rhs );
        for ( IAction action : rhs ) {
            if ( action instanceof ActionInsertFact ) {
                final ActionInsertFact aif = (ActionInsertFact) action;
                if ( aif.getFieldValues().length > 0 && aif.getBoundName() == null ) {
                    int idx = 0;
                    String binding = "fact" + idx;
                    while ( existingBindings.contains( binding ) ) {
                        idx++;
                        binding = "fact" + idx;
                    }
                    existingBindings.add( binding );
                    aif.setBoundName( binding );
                }
            }
        }
    }

    private Set<String> extractExistingActionBindings( final IAction[] rhs ) {
        final Set<String> bindings = new HashSet<String>();
        for ( IAction action : rhs ) {
            if ( action instanceof ActionInsertFact ) {
                final ActionInsertFact aif = (ActionInsertFact) action;
                if ( aif.getBoundName() != null ) {
                    bindings.add( aif.getBoundName() );
                }
            }
        }
        return bindings;
    }

    protected void marshalFooter( final StringBuilder buf ) {
        buf.append( "end\n" );
    }

    //Append package name and imports to DRL
    protected void marshalPackageHeader( final RuleModel model,
                                         final StringBuilder buf ) {
        PackageNameWriter.write( buf,
                                 model );
        ImportsWriter.write( buf,
                             model );
    }

    //Append rule header
    protected void marshalRuleHeader( final RuleModel model,
                                      final StringBuilder buf ) {
        buf.append( "rule \"" + marshalRuleName( model ) + "\"" );
        if ( null != model.parentName && model.parentName.length() > 0 ) {
            buf.append( " extends \"" + model.parentName + "\"\n" );
        } else {
            buf.append( '\n' );
        }
    }

    protected String marshalRuleName( final RuleModel model ) {
        return model.name;
    }

    /**
     * Marshal model attributes
     * @param buf
     * @param model
     */
    protected void marshalAttributes( final StringBuilder buf,
                                      final RuleModel model ) {
        boolean hasDialect = false;
        for ( int i = 0; i < model.attributes.length; i++ ) {
            RuleAttribute attr = model.attributes[ i ];

            buf.append( "\t" );
            buf.append( attr );

            buf.append( "\n" );
            if ( attr.getAttributeName().equals( "dialect" ) ) {
                constraintValueBuilder = DRLConstraintValueBuilder.getBuilder( attr.getValue() );
                hasDialect = true;
            }
        }
        // Un comment below for mvel
        if ( !hasDialect ) {
            RuleAttribute attr = new RuleAttribute( "dialect",
                                                    DEFAULT_DIALECT );
            buf.append( "\t" );
            buf.append( attr );
            buf.append( "\n" );
        }
    }

    /**
     * Marshal model metadata
     * @param buf
     * @param model
     */
    protected void marshalMetadata( final StringBuilder buf,
                                    final RuleModel model ) {
        if ( model.metadataList != null ) {
            for ( int i = 0; i < model.metadataList.length; i++ ) {
                buf.append( "\t" ).append( model.metadataList[ i ] ).append( "\n" );
            }
        }
    }

    /**
     * Marshal LHS patterns
     * @param buf
     * @param model
     */
    protected void marshalLHS( final StringBuilder buf,
                               final RuleModel model,
                               final boolean isDSLEnhanced,
                               final GeneratorContextFactory generatorContextFactory ) {
        String indentation = "\t\t";
        String nestedIndentation = indentation;
        boolean isNegated = model.isNegated();

        if ( model.lhs != null ) {
            if ( isNegated ) {
                nestedIndentation += "\t";
                buf.append( indentation );
                buf.append( "not (\n" );
            }
            LHSPatternVisitor visitor = getLHSPatternVisitor( isDSLEnhanced,
                                                              buf,
                                                              nestedIndentation,
                                                              isNegated,
                                                              generatorContextFactory );
            for ( IPattern cond : model.lhs ) {
                visitor.visit( cond );
            }
            if ( model.isNegated() ) {
                //Delete the spurious " and ", added by LHSPatternVisitor.visitFactPattern, when the rule is negated
                buf.delete( buf.length() - 5,
                            buf.length() );
                buf.append( "\n" );
                buf.append( indentation );
                buf.append( ")\n" );
            }
        }
    }

    protected LHSPatternVisitor getLHSPatternVisitor( final boolean isDSLEnhanced,
                                                      final StringBuilder buf,
                                                      final String nestedIndentation,
                                                      final boolean isNegated,
                                                      final GeneratorContextFactory generatorContextFactory ) {
        return new LHSPatternVisitor( isDSLEnhanced,
                                      bindingsPatterns,
                                      bindingsFields,
                                      constraintValueBuilder,
                                      generatorContextFactory,
                                      buf,
                                      nestedIndentation,
                                      isNegated );
    }

    protected void marshalRHS( final StringBuilder buf,
                               final RuleModel model,
                               final boolean isDSLEnhanced ) {
        String indentation = "\t\t";
        if ( model.rhs != null ) {

            //Add boiler-plate for actions operating on Dates
            Map<String, List<ActionFieldValue>> classes = getRHSClassDependencies( model );
            if ( classes.containsKey( DataType.TYPE_DATE ) ) {
                buf.append( indentation );
                buf.append( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"" + System.getProperty( "drools.dateformat" ) + "\");\n" );
            }

            //Add boiler-plate for actions operating on WorkItems
            if ( !getRHSWorkItemDependencies( model ).isEmpty() ) {
                buf.append( indentation );
                buf.append( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();\n" );
            }

            //Marshall the model itself
            RHSActionVisitor actionVisitor = getRHSActionVisitor( isDSLEnhanced,
                                                                  buf,
                                                                  indentation );
            for ( IAction action : model.rhs ) {
                actionVisitor.visit( action );
            }
        }
    }

    protected RHSActionVisitor getRHSActionVisitor( final boolean isDSLEnhanced,
                                                    final StringBuilder buf,
                                                    final String indentation ) {
        return new RHSActionVisitor( isDSLEnhanced,
                                     bindingsPatterns,
                                     bindingsFields,
                                     constraintValueBuilder,
                                     buf,
                                     indentation );
    }

    private Map<String, List<ActionFieldValue>> getRHSClassDependencies( final RuleModel model ) {
        if ( model != null ) {
            RHSClassDependencyVisitor dependencyVisitor = new RHSClassDependencyVisitor();
            for ( IAction action : model.rhs ) {
                dependencyVisitor.visit( action );
            }
            return dependencyVisitor.getRHSClasses();
        }

        Map<String, List<ActionFieldValue>> empty = Collections.emptyMap();
        return empty;
    }

    private List<PortableWorkDefinition> getRHSWorkItemDependencies( final RuleModel model ) {
        if ( model != null ) {
            List<PortableWorkDefinition> workItems = new ArrayList<PortableWorkDefinition>();
            for ( IAction action : model.rhs ) {
                if ( action instanceof ActionExecuteWorkItem ) {
                    workItems.add( ( (ActionExecuteWorkItem) action ).getWorkDefinition() );
                }
            }
            return workItems;
        }

        List<PortableWorkDefinition> empty = Collections.emptyList();
        return empty;
    }

    public static class LHSPatternVisitor extends ReflectiveVisitor {

        protected StringBuilder buf;
        private boolean isDSLEnhanced;
        private boolean isPatternNegated;
        private String indentation;
        private Map<String, IFactPattern> bindingsPatterns;
        private Map<String, FieldConstraint> bindingsFields;
        protected DRLConstraintValueBuilder constraintValueBuilder;
        protected GeneratorContextFactory generatorContextFactory;

        public LHSPatternVisitor( final boolean isDSLEnhanced,
                                  final Map<String, IFactPattern> bindingsPatterns,
                                  final Map<String, FieldConstraint> bindingsFields,
                                  final DRLConstraintValueBuilder constraintValueBuilder,
                                  final GeneratorContextFactory generatorContextFactory,
                                  final StringBuilder b,
                                  final String indentation,
                                  final boolean isPatternNegated ) {
            this.isDSLEnhanced = isDSLEnhanced;
            this.bindingsPatterns = bindingsPatterns;
            this.bindingsFields = bindingsFields;
            this.constraintValueBuilder = constraintValueBuilder;
            this.generatorContextFactory = generatorContextFactory;
            this.indentation = indentation;
            this.isPatternNegated = isPatternNegated;
            buf = b;
        }

        public void visitFactPattern( final FactPattern pattern ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            generateFactPattern( pattern );
            if ( isPatternNegated ) {
                buf.append( " and " );
            }
            buf.append( "\n" );
        }

        public void visitFreeFormLine( final FreeFormLine ffl ) {
            if ( ffl.getText() == null ) {
                return;
            }
            String[] lines = ffl.getText().split( "\\n|\\r\\n" );
            for ( String line : lines ) {
                this.buf.append( indentation );
                if ( isDSLEnhanced ) {
                    buf.append( ">" );
                }
                this.buf.append( line + "\n" );
            }
        }

        public void visitCompositeFactPattern( final CompositeFactPattern pattern ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            if ( CompositeFactPattern.COMPOSITE_TYPE_EXISTS.equals( pattern.getType() ) ) {
                renderCompositeFOL( pattern );
            } else if ( CompositeFactPattern.COMPOSITE_TYPE_NOT.equals( pattern.getType() ) ) {
                renderCompositeFOL( pattern );
            } else if ( CompositeFactPattern.COMPOSITE_TYPE_OR.equals( pattern.getType() ) ) {
                buf.append( "( " );
                if ( pattern.getPatterns() != null ) {
                    for ( int i = 0; i < pattern.getPatterns().length; i++ ) {
                        if ( i > 0 ) {
                            buf.append( " " );
                            buf.append( pattern.getType() );
                            buf.append( " " );
                        }
                        renderSubPattern( pattern,
                                          i );
                    }
                }
                buf.append( " )\n" );
            }
        }

        public void visitFromCompositeFactPattern( final FromCompositeFactPattern pattern ) {
            visitFromCompositeFactPattern( pattern,
                                           false );
        }

        public void visitFromCompositeFactPattern( final FromCompositeFactPattern pattern,
                                                   final boolean isSubPattern ) {
            buf.append( indentation );
            if ( !isSubPattern && isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            if ( pattern.getFactPattern() != null ) {
                generateFactPattern( pattern.getFactPattern() );
            }
            buf.append( " from " );
            renderExpression( pattern.getExpression() );
            buf.append( "\n" );
        }

        public void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern ) {
            visitFromCollectCompositeFactPattern( pattern,
                                                  false );
        }

        public void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern,
                                                          final boolean isSubPattern ) {
            buf.append( indentation );
            if ( !isSubPattern && isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            if ( pattern.getFactPattern() != null ) {
                generateFactPattern( pattern.getFactPattern() );
            }
            buf.append( " from collect ( " );
            if ( pattern.getRightPattern() != null ) {
                if ( pattern.getRightPattern() instanceof FactPattern ) {
                    generateFactPattern( (FactPattern) pattern.getRightPattern() );
                } else if ( pattern.getRightPattern() instanceof FromAccumulateCompositeFactPattern ) {
                    visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) pattern.getRightPattern(),
                                                             isSubPattern );
                } else if ( pattern.getRightPattern() instanceof FromCollectCompositeFactPattern ) {
                    visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) pattern.getRightPattern(),
                                                          isSubPattern );
                } else if ( pattern.getRightPattern() instanceof FromEntryPointFactPattern ) {
                    visitFromEntryPointFactPattern( (FromEntryPointFactPattern) pattern.getRightPattern(),
                                                    isSubPattern );
                } else if ( pattern.getRightPattern() instanceof FromCompositeFactPattern ) {
                    visitFromCompositeFactPattern( (FromCompositeFactPattern) pattern.getRightPattern(),
                                                   isSubPattern );
                } else if ( pattern.getRightPattern() instanceof FreeFormLine ) {
                    visitFreeFormLine( (FreeFormLine) pattern.getRightPattern() );
                } else {
                    throw new IllegalArgumentException( "Unsupported pattern " + pattern.getRightPattern() + " for FROM COLLECT" );
                }
            }
            buf.append( ") \n" );
        }

        public void visitFromAccumulateCompositeFactPattern( final FromAccumulateCompositeFactPattern pattern ) {
            visitFromAccumulateCompositeFactPattern( pattern,
                                                     false );
        }

        public void visitFromAccumulateCompositeFactPattern( final FromAccumulateCompositeFactPattern pattern,
                                                             final boolean isSubPattern ) {
            buf.append( indentation );
            if ( !isSubPattern && isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            if ( pattern.getFactPattern() != null ) {
                generateFactPattern( pattern.getFactPattern() );
            }
            buf.append( " from accumulate ( " );
            if ( pattern.getSourcePattern() != null ) {
                if ( pattern.getSourcePattern() instanceof FactPattern ) {
                    generateFactPattern( (FactPattern) pattern.getSourcePattern() );
                } else if ( pattern.getSourcePattern() instanceof FromAccumulateCompositeFactPattern ) {
                    visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) pattern.getSourcePattern(),
                                                             isSubPattern );
                } else if ( pattern.getSourcePattern() instanceof FromCollectCompositeFactPattern ) {
                    visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) pattern.getSourcePattern(),
                                                          isSubPattern );
                } else if ( pattern.getSourcePattern() instanceof FromEntryPointFactPattern ) {
                    visitFromEntryPointFactPattern( (FromEntryPointFactPattern) pattern.getSourcePattern(),
                                                    isSubPattern );
                } else if ( pattern.getSourcePattern() instanceof FromCompositeFactPattern ) {
                    visitFromCompositeFactPattern( (FromCompositeFactPattern) pattern.getSourcePattern(),
                                                   isSubPattern );
                } else {
                    throw new IllegalArgumentException( "Unsupported pattern " + pattern.getSourcePattern() + " for FROM ACCUMULATE" );
                }
            }
            buf.append( ",\n" );

            if ( pattern.useFunctionOrCode().equals( FromAccumulateCompositeFactPattern.USE_FUNCTION ) ) {
                buf.append( indentation + "\t" );
                buf.append( pattern.getFunction() );
            } else {
                buf.append( indentation + "\tinit( " );
                buf.append( pattern.getInitCode() );
                buf.append( " ),\n" );
                buf.append( indentation + "\taction( " );
                buf.append( pattern.getActionCode() );
                buf.append( " ),\n" );
                if ( pattern.getReverseCode() != null && !pattern.getReverseCode().trim().equals( "" ) ) {
                    buf.append( indentation + "\treverse( " );
                    buf.append( pattern.getReverseCode() );
                    buf.append( " ),\n" );
                }
                buf.append( indentation + "\tresult( " );
                buf.append( pattern.getResultCode() );
                buf.append( " )\n" );
            }
            buf.append( ") \n" );

        }

        public void visitFromEntryPointFactPattern( final FromEntryPointFactPattern pattern ) {
            visitFromEntryPointFactPattern( pattern,
                                            false );
        }

        public void visitFromEntryPointFactPattern( final FromEntryPointFactPattern pattern,
                                                    final boolean isSubPattern ) {
            buf.append( indentation );
            if ( !isSubPattern && isDSLEnhanced ) {
                // adding passthrough markup
                buf.append( ">" );
            }
            if ( pattern.getFactPattern() != null ) {
                generateFactPattern( pattern.getFactPattern() );
            }
            buf.append( " from entry-point \"" + pattern.getEntryPointName() + "\"\n" );
        }

        private void renderCompositeFOL( final CompositeFactPattern pattern ) {
            buf.append( pattern.getType() );
            if ( pattern.getPatterns() != null ) {
                buf.append( " (" );
                for ( int i = 0; i < pattern.getPatterns().length; i++ ) {
                    renderSubPattern( pattern,
                                      i );
                    if ( i != pattern.getPatterns().length - 1 ) {
                        buf.append( " and " );
                    }
                }
                buf.append( ") \n" );
            }
        }

        private void renderSubPattern( final CompositeFactPattern pattern,
                                       final int subIndex ) {
            if ( pattern.getPatterns() == null || pattern.getPatterns().length == 0 ) {
                return;
            }
            IFactPattern subPattern = pattern.getPatterns()[ subIndex ];
            if ( subPattern instanceof FactPattern ) {
                this.generateFactPattern( (FactPattern) subPattern );
            } else if ( subPattern instanceof FromAccumulateCompositeFactPattern ) {
                this.visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) subPattern,
                                                              true );
            } else if ( subPattern instanceof FromCollectCompositeFactPattern ) {
                this.visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) subPattern,
                                                           true );
            } else if ( subPattern instanceof FromCompositeFactPattern ) {
                this.visitFromCompositeFactPattern( (FromCompositeFactPattern) subPattern,
                                                    true );
            } else {
                throw new IllegalStateException( "Unsupported Pattern: " + subPattern.getClass().getName() );
            }
        }

        private void renderExpression( final ExpressionFormLine expression ) {
            buf.append( expression.getText() );
        }

        public void visitDSLSentence( final DSLSentence sentence ) {
            buf.append( indentation );
            buf.append( sentence.interpolate() );
            buf.append( "\n" );
        }

        private void generateFactPattern( final FactPattern pattern ) {
            if ( pattern.isNegated() ) {
                buf.append( "not " );
            } else if ( pattern.isBound() ) {
                bindingsPatterns.put( pattern.getBoundName(),
                                      pattern );
                buf.append( pattern.getBoundName() );
                buf.append( " : " );
            }
            if ( pattern.getFactType() != null ) {
                buf.append( pattern.getFactType() );
            }
            buf.append( "( " );

            // top level constraints
            if ( pattern.getConstraintList() != null ) {
                generateConstraints( pattern );
            }
            buf.append( ")" );

            //Add CEP window definition
            CEPWindow window = pattern.getWindow();
            if ( window.isDefined() ) {
                buf.append( " " );
                buf.append( window.getOperator() );
                buf.append( buildOperatorParameterDRL( window.getParameters() ) );
            }
        }

        private void generateConstraints( final FactPattern pattern ) {
            GeneratorContext gctx = generatorContextFactory.newGeneratorContext();
            preGenerateConstraints( gctx );
            for ( int constraintIndex = 0; constraintIndex < pattern.getFieldConstraints().length; constraintIndex++ ) {
                FieldConstraint constr = pattern.getConstraintList().getConstraints()[ constraintIndex ];
                gctx.setFieldConstraint( constr );
                generateConstraint( constr,
                                    gctx );
            }
        }

        public void preGenerateConstraints( GeneratorContext gctx ) {
            // empty, overriden by rule templates
        }

        public void preGenerateNestedConnector( GeneratorContext gctx ) {
            // empty, overriden by rule templates
        }

        public void postGenerateNestedConnector( GeneratorContext gctx ) {
            // empty, overriden by rule templates
        }

        public void preGenerateNestedConstraint( GeneratorContext gctx ) {
            // empty, overriden by rule templates
        }

        public void postGenerateNestedConstraint( GeneratorContext gctx ) {
            // empty, overriden by rule templates
        }

        public void generateSeparator( FieldConstraint constr,
                                       GeneratorContext gctx ) {
            if ( !gctx.isHasOutput() ) {
                return;
            }
            if ( gctx.getDepth() == 0 ) {
                buf.append( ", " );
            } else {
                CompositeFieldConstraint cconstr = (CompositeFieldConstraint) gctx.getParent().getFieldConstraint();
                buf.append( cconstr.getCompositeJunctionType() + " " );
            }
        }

        /**
         * Recursively process the nested constraints. It will only put brackets
         * in for the ones that aren't at top level. This makes for more
         * readable DRL in the most common cases.
         */
        protected void generateConstraint( final FieldConstraint con,
                                           GeneratorContext gctx ) {
            generateSeparator( con, gctx );
            if ( con instanceof CompositeFieldConstraint ) {
                CompositeFieldConstraint cfc = (CompositeFieldConstraint) con;
                FieldConstraint[] nestedConstraints = cfc.getConstraints();
                if ( nestedConstraints != null ) {
                    GeneratorContext nestedGctx = generatorContextFactory.newChildGeneratorContext( gctx );
                    preGenerateConstraints( nestedGctx );
                    preGenerateNestedConstraint( gctx );
                    if ( gctx.getDepth() > 0 ) {
                        buf.append( "( " );
                    }
                    for ( int nestedConstraintIndex = 0; nestedConstraintIndex < nestedConstraints.length; nestedConstraintIndex++ ) {
                        FieldConstraint nestedConstr = nestedConstraints[ nestedConstraintIndex ];
                        nestedGctx.setFieldConstraint( nestedConstr );
                        generateConstraint( nestedConstr,
                                            nestedGctx );
                    }
                    gctx.setHasOutput( nestedGctx.isHasOutput() );
                    if ( gctx.getDepth() > 0 ) {
                        buf.append( ")" );
                    }
                    postGenerateNestedConstraint( gctx );
                }
            } else {
                generateSingleFieldConstraint( (SingleFieldConstraint) con, gctx );
            }
        }

        private void generateSingleFieldConstraint( final SingleFieldConstraint constr,
                                                    GeneratorContext gctx ) {
            if ( constr.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                buf.append( "eval( " );
                buf.append( constr.getValue() );
                buf.append( " )" );
            } else {
                if ( constr.isBound() ) {
                    bindingsFields.put( constr.getFieldBinding(),
                                        constr );
                    buf.append( constr.getFieldBinding() );
                    buf.append( " : " );
                }

                assertConstraintValue( constr );

                if ( isConstraintComplete( constr ) ) {
                    SingleFieldConstraint parent = (SingleFieldConstraint) constr.getParent();
                    StringBuilder parentBuf = new StringBuilder();
                    while ( parent != null ) {
                        String fieldName = parent.getFieldName();
                        parentBuf.insert( 0,
                                          fieldName + "." );
                        parent = (SingleFieldConstraint) parent.getParent();
                    }
                    buf.append( parentBuf );
                    if ( constr instanceof SingleFieldConstraintEBLeftSide ) {
                        buf.append( ( (SingleFieldConstraintEBLeftSide) constr ).getExpressionLeftSide().getText() );
                    } else {
                        String fieldName = constr.getFieldName();
                        buf.append( fieldName );
                    }

                    Map<String, String> parameters = null;
                    if ( constr instanceof HasParameterizedOperator ) {
                        HasParameterizedOperator hop = constr;
                        parameters = hop.getParameters();
                    }
                    if ( constr.getConnectives() == null ) {
                        generateNormalFieldRestriction( constr, parameters );
                    } else {
                        generateConnectiveFieldRestriction( constr, parameters, gctx );
                    }
                    gctx.setHasOutput( true );
                }
            }
        }

        private void generateNormalFieldRestriction( SingleFieldConstraint constr,
                                                     Map<String, String> parameters ) {
            if ( constr instanceof SingleFieldConstraintEBLeftSide ) {
                SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) constr;
                addFieldRestriction( buf,
                                     sfexp.getConstraintValueType(),
                                     sfexp.getExpressionLeftSide().getGenericType(),
                                     sfexp.getOperator(),
                                     parameters,
                                     sfexp.getValue(),
                                     sfexp.getExpressionValue(),
                                     true );
            } else {
                addFieldRestriction( buf,
                                     constr.getConstraintValueType(),
                                     constr.getFieldType(),
                                     constr.getOperator(),
                                     parameters,
                                     constr.getValue(),
                                     constr.getExpressionValue(),
                                     true );
            }
        }

        private void generateConnectiveFieldRestriction( SingleFieldConstraint constr,
                                                         Map<String, String> parameters,
                                                         GeneratorContext gctx ) {
            GeneratorContext cctx = generatorContextFactory.newChildGeneratorContext( gctx );
            preGenerateConstraints( cctx );
            cctx.setFieldConstraint( constr );
            if ( constr instanceof SingleFieldConstraintEBLeftSide ) {
                SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) constr;
                addConnectiveFieldRestriction( buf,
                                               sfexp.getConstraintValueType(),
                                               sfexp.getExpressionLeftSide().getGenericType(),
                                               sfexp.getOperator(),
                                               parameters,
                                               sfexp.getValue(),
                                               sfexp.getExpressionValue(),
                                               cctx,
                                               true );
            } else {
                addConnectiveFieldRestriction( buf,
                                               constr.getConstraintValueType(),
                                               constr.getFieldType(),
                                               constr.getOperator(),
                                               parameters,
                                               constr.getValue(),
                                               constr.getExpressionValue(),
                                               cctx,
                                               true );
            }

            for ( int j = 0; j < constr.getConnectives().length; j++ ) {
                final ConnectiveConstraint conn = constr.getConnectives()[ j ];

                if ( conn instanceof HasParameterizedOperator ) {
                    HasParameterizedOperator hop = (HasParameterizedOperator) conn;
                    parameters = hop.getParameters();
                }

                addConnectiveFieldRestriction( buf,
                                               conn.getConstraintValueType(),
                                               conn.getFieldType(),
                                               conn.getOperator(),
                                               parameters,
                                               conn.getValue(),
                                               conn.getExpressionValue(),
                                               cctx,
                                               true );
            }
        }

        private void assertConstraintValue( final SingleFieldConstraint sfc ) {
            if ( DataType.TYPE_STRING.equals( sfc.getFieldType() ) ) {
                if ( sfc.getValue() == null ) {
                    sfc.setValue( "" );
                }
            }
        }

        private boolean isConstraintComplete( final SingleFieldConstraint constr ) {
            if ( constr.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE ) {
                return true;
            } else if ( constr instanceof SingleFieldConstraintEBLeftSide ) {
                return true;
            } else if ( constr.getFieldBinding() != null ) {
                return true;
            }
            final String operator = constr.getOperator();
            final String fieldType = constr.getFieldType();
            final String fieldValue = constr.getValue();
            if ( operator == null ) {
                return false;
            }
            if ( operator.equals( "== null" ) || operator.equals( "!= null" ) ) {
                return true;
            }
            if ( DataType.TYPE_STRING.equals( fieldType ) ) {
                return true;
            }

            return !( fieldValue == null || fieldValue.isEmpty() );
        }

        protected void addConnectiveFieldRestriction( final StringBuilder buf,
                                                      final int type,
                                                      final String fieldType,
                                                      final String operator,
                                                      final Map<String, String> parameters,
                                                      final String value,
                                                      final ExpressionFormLine expression,
                                                      GeneratorContext gctx,
                                                      final boolean spaceBeforeOperator ) {
            addFieldRestriction( buf, type, fieldType, operator, parameters, value, expression, spaceBeforeOperator );
        }

        private void addFieldRestriction( final StringBuilder buf,
                                          final int type,
                                          final String fieldType,
                                          final String operator,
                                          final Map<String, String> parameters,
                                          final String value,
                                          final ExpressionFormLine expression,
                                          final boolean spaceBeforeOperator ) {
            if ( operator == null ) {
                return;
            }

            if ( spaceBeforeOperator ) {
                buf.append( " " );
            }
            buf.append( operator );

            if ( parameters != null && parameters.size() > 0 ) {
                buf.append( buildOperatorParameterDRL( parameters ) );
            }

            switch ( type ) {
                case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                    buildReturnValueFieldValue( value,
                                                buf );
                    break;
                case BaseSingleFieldConstraint.TYPE_LITERAL:
                    buildLiteralFieldValue( operator,
                                            type,
                                            fieldType,
                                            value,
                                            buf );
                    break;
                case BaseSingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE:
                    buildExpressionFieldValue( expression,
                                               buf );
                    break;
                case BaseSingleFieldConstraint.TYPE_TEMPLATE:
                    buildTemplateFieldValue( type,
                                             fieldType,
                                             value,
                                             buf );
                    break;
                case BaseSingleFieldConstraint.TYPE_ENUM:
                    buildEnumFieldValue( operator,
                                         type,
                                         fieldType,
                                         value,
                                         buf );
                    break;
                default:
                    buildDefaultFieldValue( operator,
                                            value,
                                            buf );
            }
        }

        protected void buildReturnValueFieldValue( final String value,
                                                   final StringBuilder buf ) {
            buf.append( " " );
            buf.append( "( " );
            buf.append( value );
            buf.append( " )" );
            buf.append( " " );
        }

        protected StringBuilder buildOperatorParameterDRL( final Map<String, String> parameters ) {
            String className = parameters.get( SharedConstants.OPERATOR_PARAMETER_GENERATOR );
            if ( className == null ) {
                throw new IllegalStateException( "Implementation of 'org.kie.guvnor.guided.server.util.OperatorParameterDRLBuilder' undefined. Unable to build Operator Parameter DRL." );
            }

            try {
                OperatorParameterDRLBuilder builder = (OperatorParameterDRLBuilder) Class.forName( className ).newInstance();
                return builder.buildDRL( parameters );
            } catch ( ClassNotFoundException cnfe ) {
                throw new IllegalStateException( "Unable to generate Operator DRL using class '" + className + "'.",
                                                 cnfe );
            } catch ( IllegalAccessException iae ) {
                throw new IllegalStateException( "Unable to generate Operator DRL using class '" + className + "'.",
                                                 iae );
            } catch ( InstantiationException ie ) {
                throw new IllegalStateException( "Unable to generate Operator DRL using class '" + className + "'.",
                                                 ie );
            }

        }

        protected void buildLiteralFieldValue( final String operator,
                                               final int type,
                                               final String fieldType,
                                               final String value,
                                               final StringBuilder buf ) {
            if ( OperatorsOracle.operatorRequiresList( operator ) ) {
                populateValueList( buf,
                                   type,
                                   fieldType,
                                   value );
            } else {
                if ( !operator.equals( "== null" ) && !operator.equals( "!= null" ) ) {
                    buf.append( " " );
                    constraintValueBuilder.buildLHSFieldValue( buf,
                                                               type,
                                                               fieldType,
                                                               value );
                }
            }
            buf.append( " " );
        }

        private void populateValueList( final StringBuilder buf,
                                        final int type,
                                        final String fieldType,
                                        final String value ) {
            String workingValue = value.trim();
            if ( workingValue.startsWith( "(" ) ) {
                workingValue = workingValue.substring( 1 );
            }
            if ( workingValue.endsWith( ")" ) ) {
                workingValue = workingValue.substring( 0,
                                                       workingValue.length() - 1 );
            }
            final String[] values = workingValue.split( "," );
            buf.append( " ( " );
            for ( String v : values ) {
                v = v.trim();
                if ( v.startsWith( "\"" ) ) {
                    v = v.substring( 1 );
                }
                if ( v.endsWith( "\"" ) ) {
                    v = v.substring( 0,
                                     v.length() - 1 );
                }
                constraintValueBuilder.buildLHSFieldValue( buf,
                                                           type,
                                                           fieldType,
                                                           v );
                buf.append( ", " );
            }
            buf.delete( buf.length() - 2,
                        buf.length() );
            buf.append( " )" );
        }

        protected void buildExpressionFieldValue( final ExpressionFormLine expression,
                                                  final StringBuilder buf ) {
            if ( expression != null ) {
                buf.append( " " );
                buf.append( expression.getText() );
                buf.append( " " );
            }
        }

        protected void buildTemplateFieldValue( final int type,
                                                final String fieldType,
                                                final String value,
                                                final StringBuilder buf ) {
            buf.append( " " );
            constraintValueBuilder.buildLHSFieldValue( buf,
                                                       type,
                                                       fieldType,
                                                       "@{" + value + "}" );
            buf.append( " " );
        }

        private void buildEnumFieldValue( final String operator,
                                          final int type,
                                          final String fieldType,
                                          final String value,
                                          final StringBuilder buf ) {

            if ( OperatorsOracle.operatorRequiresList( operator ) ) {
                populateValueList( buf,
                                   type,
                                   fieldType,
                                   value );
            } else {
                if ( !operator.equals( "== null" ) && !operator.equals( "!= null" ) ) {
                    buf.append( " " );
                    constraintValueBuilder.buildLHSFieldValue( buf,
                                                               type,
                                                               fieldType,
                                                               value );
                }
            }
            buf.append( " " );
        }

        protected void buildDefaultFieldValue( final String operator,
                                               final String value,
                                               final StringBuilder buf ) {
            if ( !operator.equals( "== null" ) && !operator.equals( "!= null" ) ) {
                buf.append( " " );
                buf.append( value );
            }
            buf.append( " " );
        }

    }

    public static class RHSActionVisitor extends ReflectiveVisitor {

        protected StringBuilder buf;
        private boolean isDSLEnhanced;
        private String indentation;
        //        private int idx = 0;
        private Map<String, IFactPattern> bindingsPatterns;
        private Map<String, FieldConstraint> bindingsFields;
        protected DRLConstraintValueBuilder constraintValueBuilder;

        //Keep a record of Work Items that are instantiated for Actions that depend on them
        private Set<String> instantiatedWorkItems;

        public RHSActionVisitor( final boolean isDSLEnhanced,
                                 final Map<String, IFactPattern> bindingsPatterns,
                                 final Map<String, FieldConstraint> bindingsFields,
                                 final DRLConstraintValueBuilder constraintValueBuilder,
                                 final StringBuilder b,
                                 final String indentation ) {
            this.isDSLEnhanced = isDSLEnhanced;
            this.bindingsPatterns = bindingsPatterns;
            this.bindingsFields = bindingsFields;
            this.constraintValueBuilder = constraintValueBuilder;
            this.indentation = indentation;
            this.instantiatedWorkItems = new HashSet<String>();
            buf = b;
        }

        public void visitActionInsertFact( final ActionInsertFact action ) {
            this.generateInsertCall( action,
                                     false );
        }

        public void visitActionInsertLogicalFact( final ActionInsertLogicalFact action ) {
            this.generateInsertCall( action,
                                     true );
        }

        public void visitFreeFormLine( final FreeFormLine ffl ) {
            if ( ffl.getText() == null ) {
                return;
            }
            String[] lines = ffl.getText().split( "\\n|\\r\\n" );
            for ( String line : lines ) {
                this.buf.append( indentation );
                if ( isDSLEnhanced ) {
                    buf.append( ">" );
                }
                this.buf.append( line + "\n" );
            }
        }

        private void generateInsertCall( final ActionInsertFact action,
                                         final boolean isLogic ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            final String binding = action.getBoundName();
            if ( action.getFieldValues().length == 0 && binding == null ) {
                buf.append( ( isLogic ) ? "insertLogical( new " : "insert( new " );

                buf.append( action.getFactType() );
                buf.append( "() );\n" );
            } else {
                buf.append( action.getFactType() );
                buf.append( " " + binding );
                buf.append( " = new " );
                buf.append( action.getFactType() );
                buf.append( "();\n" );
                generateSetMethodCalls( binding,
                                        action.getFieldValues() );

                buf.append( indentation );
                if ( isDSLEnhanced ) {
                    buf.append( ">" );
                }
                if ( isLogic ) {
                    buf.append( "insertLogical( " );
                    buf.append( binding );
                    buf.append( " );\n" );
                } else {
                    buf.append( "insert( " );
                    buf.append( binding );

                    buf.append( " );\n" );
                }
            }
        }

        public void visitActionUpdateField( final ActionUpdateField action ) {
            this.visitActionSetField( action );
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( "update( " );
            buf.append( action.getVariable() );
            buf.append( " );\n" );
        }

        public void visitActionGlobalCollectionAdd( final ActionGlobalCollectionAdd add ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( add.getGlobalName() + ".add( " + add.getFactName() + " );\n" );
        }

        public void visitActionRetractFact( final ActionRetractFact action ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( "retract( " );
            buf.append( action.getVariableName() );
            buf.append( " );\n" );
        }

        public void visitDSLSentence( final DSLSentence sentence ) {
            buf.append( indentation );
            buf.append( sentence.interpolate() );
            buf.append( "\n" );
        }

        public void visitActionExecuteWorkItem( final ActionExecuteWorkItem action ) {
            String wiName = action.getWorkDefinition().getName();
            String wiImplName = WORKITEM_PREFIX + wiName;

            instantiatedWorkItems.add( wiName );

            buf.append( indentation );
            buf.append( "org.drools.core.process.instance.impl.WorkItemImpl " );
            buf.append( wiImplName );
            buf.append( " = new org.drools.core.process.instance.impl.WorkItemImpl();\n" );
            buf.append( indentation );
            buf.append( wiImplName );
            buf.append( ".setName( \"" );
            buf.append( wiName );
            buf.append( "\" );\n" );
            for ( PortableParameterDefinition ppd : action.getWorkDefinition().getParameters() ) {
                makeWorkItemParameterDRL( ppd,
                                          wiImplName );
            }
            buf.append( indentation );
            buf.append( "wim.internalExecuteWorkItem( " );
            buf.append( wiImplName );
            buf.append( " );\n" );
        }

        private void makeWorkItemParameterDRL( final PortableParameterDefinition ppd,
                                               final String wiImplName ) {
            boolean makeParameter = true;

            //Only add bound parameters if their binding exists (i.e. the corresponding column has a value or - for Limited Entry - is true)
            if ( ppd instanceof HasBinding ) {
                HasBinding hb = (HasBinding) ppd;
                if ( hb.isBound() ) {
                    String binding = hb.getBinding();
                    makeParameter = isBindingValid( binding );
                }
            }
            if ( makeParameter ) {
                buf.append( indentation );
                buf.append( wiImplName );
                buf.append( ".getParameters().put( \"" );
                buf.append( ppd.getName() );
                buf.append( "\", " );
                buf.append( ppd.asString() );
                buf.append( " );\n" );
            }
        }

        private boolean isBindingValid( final String binding ) {
            if ( bindingsPatterns.containsKey( binding ) ) {
                return true;
            }
            if ( bindingsFields.containsKey( binding ) ) {
                return true;
            }
            return false;
        }

        public void visitActionSetField( final ActionSetField action ) {
            if ( action instanceof ActionCallMethod ) {
                this.generateSetMethodCallsMethod( (ActionCallMethod) action,
                                                   action.getFieldValues() );
            } else {
                this.generateSetMethodCalls( action.getVariable(),
                                             action.getFieldValues() );
            }
        }

        private void generateSetMethodCalls( final String variableName,
                                             final ActionFieldValue[] fieldValues ) {
            for ( int i = 0; i < fieldValues.length; i++ ) {
                generateSetMethodCall( variableName,
                                       fieldValues[ i ] );
            }
        }

        protected void generateSetMethodCall( final String variableName,
                                              final ActionFieldValue fieldValue ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( variableName );

            if ( fieldValue instanceof ActionFieldFunction ) {
                buf.append( "." );
                buf.append( fieldValue.getField() );
            } else {
                buf.append( ".set" );
                buf.append( Character.toUpperCase( fieldValue.getField().charAt( 0 ) ) );
                buf.append( fieldValue.getField().substring( 1 ) );
            }
            buf.append( "( " );
            generateSetMethodCallParameterValue( buf,
                                                 fieldValue );
            buf.append( " );\n" );
        }

        private void generateSetMethodCallParameterValue( final StringBuilder buf,
                                                          final ActionFieldValue fieldValue ) {
            if ( fieldValue.isFormula() ) {
                buildFormulaFieldValue( fieldValue,
                                        buf );
            } else if ( fieldValue.getNature() == FieldNatureType.TYPE_TEMPLATE ) {
                buildTemplateFieldValue( fieldValue,
                                         buf );
            } else if ( fieldValue instanceof ActionWorkItemFieldValue ) {
                buildWorkItemFieldValue( (ActionWorkItemFieldValue) fieldValue,
                                         buf );
            } else {
                buildDefaultFieldValue( fieldValue,
                                        buf );
            }
        }

        protected void buildFormulaFieldValue( final ActionFieldValue fieldValue,
                                               final StringBuilder buf ) {
            buf.append( fieldValue.getValue() );
        }

        protected void buildTemplateFieldValue( final ActionFieldValue fieldValue,
                                                final StringBuilder buf ) {
            constraintValueBuilder.buildRHSFieldValue( buf,
                                                       fieldValue.getType(),
                                                       "@{" + fieldValue.getValue() + "}" );
        }

        protected void buildWorkItemFieldValue( final ActionWorkItemFieldValue afv,
                                                final StringBuilder buf ) {
            if ( instantiatedWorkItems.contains( afv.getWorkItemName() ) ) {
                buf.append( "(" );
                buf.append( afv.getWorkItemParameterClassName() );
                buf.append( ") " );
                buf.append( WORKITEM_PREFIX );
                buf.append( afv.getWorkItemName() );
                buf.append( ".getResult( \"" );
                buf.append( afv.getWorkItemParameterName() );
                buf.append( "\" )" );
            } else {
                buf.append( "null" );
            }
        }

        protected void buildDefaultFieldValue( final ActionFieldValue fieldValue,
                                               final StringBuilder buf ) {
            constraintValueBuilder.buildRHSFieldValue( buf,
                                                       fieldValue.getType(),
                                                       fieldValue.getValue() );
        }

        private void generateSetMethodCallsMethod( final ActionCallMethod action,
                                                   final FieldNature[] fieldValues ) {
            buf.append( indentation );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( action.getVariable() );
            buf.append( "." );

            buf.append( action.getMethodName() );

            buf.append( "( " );
            boolean isFirst = true;
            for ( int i = 0; i < fieldValues.length; i++ ) {
                ActionFieldFunction valueFunction = (ActionFieldFunction) fieldValues[ i ];
                if ( isFirst == true ) {
                    isFirst = false;
                } else {
                    buf.append( ", " );
                }

                if ( valueFunction.isFormula() ) {
                    buf.append( valueFunction.getValue() );
                } else if ( valueFunction.getNature() == FieldNatureType.TYPE_VARIABLE ) {
                    buf.append( valueFunction.getValue() );
                } else {
                    buildDefaultFieldValue( valueFunction,
                                            buf );
                }
            }
            buf.append( " );\n" );

        }
    }

    public static class RHSClassDependencyVisitor extends ReflectiveVisitor {

        private Map<String, List<ActionFieldValue>> classes = new HashMap<String, List<ActionFieldValue>>();

        public void visitFreeFormLine( FreeFormLine ffl ) {
            //Do nothing other than preventing ReflectiveVisitor recording an error
        }

        public void visitActionGlobalCollectionAdd( final ActionGlobalCollectionAdd add ) {
            //Do nothing other than preventing ReflectiveVisitor recording an error
        }

        public void visitActionRetractFact( final ActionRetractFact action ) {
            //Do nothing other than preventing ReflectiveVisitor recording an error
        }

        public void visitDSLSentence( final DSLSentence sentence ) {
            //Do nothing other than preventing ReflectiveVisitor recording an error
        }

        public void visitActionInsertFact( final ActionInsertFact action ) {
            getClasses( action.getFieldValues() );
        }

        public void visitActionInsertLogicalFact( final ActionInsertLogicalFact action ) {
            getClasses( action.getFieldValues() );
        }

        public void visitActionUpdateField( final ActionUpdateField action ) {
            getClasses( action.getFieldValues() );
        }

        public void visitActionSetField( final ActionSetField action ) {
            getClasses( action.getFieldValues() );
        }

        public void visitActionExecuteWorkItem( final ActionExecuteWorkItem action ) {
            //Do nothing other than preventing ReflectiveVisitor recording an error
        }

        public Map<String, List<ActionFieldValue>> getRHSClasses() {
            return classes;
        }

        private void getClasses( ActionFieldValue[] fieldValues ) {
            for ( ActionFieldValue afv : fieldValues ) {
                String type = afv.getType();
                List<ActionFieldValue> afvs = classes.get( type );
                if ( afvs == null ) {
                    afvs = new ArrayList<ActionFieldValue>();
                    classes.put( type,
                                 afvs );
                }
                afvs.add( afv );
            }
        }

    }

    /**
     * @see RuleModelPersistence#unmarshal(String, PackageDataModelOracle)
     */
    public RuleModel unmarshal( final String str,
                                final PackageDataModelOracle dmo ) {
        if ( str == null || str.isEmpty() ) {
            return new RuleModel();
        }
        return getRuleModel( preprocessDRL( str ).registerGlobals( dmo, null ), dmo );
    }

    public RuleModel unmarshalUsingDSL( final String str,
                                        final List<String> globals,
                                        final PackageDataModelOracle dmo,
                                        final String... dsls ) {
        if ( str == null || str.isEmpty() ) {
            return new RuleModel();
        }
        return getRuleModel( parseDSLs( preprocessDRL( str ), dsls ).registerGlobals( dmo, globals ), dmo );
    }

    private ExpandedDRLInfo parseDSLs( ExpandedDRLInfo expandedDRLInfo,
                                       String[] dsls ) {
        for ( String dsl : dsls ) {
            for ( String line : dsl.split( "\n" ) ) {
                String dslPattern = line.trim();
                if ( dslPattern.length() > 0 ) {
                    if ( dslPattern.startsWith( "[when]" ) ) {
                        expandedDRLInfo.lhsDslPatterns.add( extractDslPattern( dslPattern.substring( "[when]".length() ) ) );
                    } else if ( dslPattern.startsWith( "[then]" ) ) {
                        expandedDRLInfo.rhsDslPatterns.add( extractDslPattern( dslPattern.substring( "[then]".length() ) ) );
                    } else if ( dslPattern.startsWith( "[]" ) ) {
                        String pattern = extractDslPattern( dslPattern.substring( "[]".length() ) );
                        expandedDRLInfo.lhsDslPatterns.add( pattern );
                        expandedDRLInfo.rhsDslPatterns.add( pattern );
                    }
                }
            }
        }
        return expandedDRLInfo;
    }

    private String extractDslPattern( String line ) {
        return line.substring( 0, line.indexOf( '=' ) ).trim();
    }

    private RuleModel getRuleModel( ExpandedDRLInfo expandedDRLInfo,
                                    PackageDataModelOracle dmo ) {
        //De-serialize model
        RuleDescr ruleDescr = parseDrl( expandedDRLInfo );
        RuleModel model = new RuleModel();
        model.name = ruleDescr.getName();
        model.parentName = ruleDescr.getParentName();

        Map<String, AnnotationDescr> annotations = ruleDescr.getAnnotations();
        if ( annotations != null ) {
            for ( AnnotationDescr annotation : annotations.values() ) {
                model.addMetadata( new RuleMetadata( annotation.getName(), annotation.getValuesAsString() ) );
            }
        }

        //De-serialize Package name
        final String packageName = PackageNameParser.parsePackageName( expandedDRLInfo.plainDrl );
        model.setPackageName( packageName );

        //De-serialize imports
        final Imports imports = ImportsParser.parseImports( expandedDRLInfo.plainDrl );
        for ( Import item : imports.getImports() ) {
            model.getImports().addImport( item );
        }

        boolean isJavaDialect = parseAttributes( model,
                                                 ruleDescr.getAttributes() );
        Map<String, String> boundParams = parseLhs( model, ruleDescr.getLhs(), expandedDRLInfo, dmo );
        parseRhs( model,
                  expandedDRLInfo.consequence != null ? expandedDRLInfo.consequence : (String) ruleDescr.getConsequence(),
                  isJavaDialect,
                  boundParams,
                  expandedDRLInfo,
                  dmo );
        return model;
    }

    private ExpandedDRLInfo preprocessDRL( String str ) {
        boolean hasDsl = false;
        StringBuilder drl = new StringBuilder();
        String thenLine = null;
        List<String> lhsStatements = new ArrayList<String>();
        List<String> rhsStatements = new ArrayList<String>();
        int statementsWithoutParanthesis = 0;

        String[] lines = str.split( "\n" );
        RuleSection ruleSection = RuleSection.HEADER;
        int lhsParenthesisBalance = 0;

        for ( String line : lines ) {
            if ( ruleSection == RuleSection.HEADER ) {
                drl.append( line ).append( "\n" );
                if ( line.contains( "when" ) ) {
                    ruleSection = RuleSection.LHS;
                }
                continue;
            }
            if ( ruleSection == RuleSection.LHS && line.contains( "then" ) ) {
                thenLine = line;
                ruleSection = RuleSection.RHS;
                continue;
            }
            if ( line.trim().startsWith( ">" ) ) {
                hasDsl = true;
            } else if ( line.indexOf( '(' ) < 0 ) {
                statementsWithoutParanthesis++;
            }
            if ( ruleSection == RuleSection.LHS ) {
                if ( lhsParenthesisBalance == 0 ) {
                    lhsStatements.add( line );
                } else {
                    String oldLine = lhsStatements.remove( lhsStatements.size() - 1 );
                    lhsStatements.add( oldLine + " " + line );
                }
                lhsParenthesisBalance += paranthesisBalance( line );
            } else {
                rhsStatements.add( line );
            }
        }

        hasDsl |= statementsWithoutParanthesis == lhsStatements.size() + rhsStatements.size();

        return createExpandedDRLInfo( hasDsl, drl, thenLine, lhsStatements, rhsStatements );
    }

    private int paranthesisBalance( String str ) {
        int balance = 0;
        for ( char ch : str.toCharArray() ) {
            if ( ch == '(' ) {
                balance++;
            } else if ( ch == ')' ) {
                balance--;
            }
        }
        return balance;
    }

    private ExpandedDRLInfo createExpandedDRLInfo( boolean hasDsl,
                                                   StringBuilder drl,
                                                   String thenLine,
                                                   List<String> lhsStatements,
                                                   List<String> rhsStatements ) {
        if ( !hasDsl ) {
            return processFreeFormStatement( drl, thenLine, lhsStatements, rhsStatements );
        }

        ExpandedDRLInfo expandedDRLInfo = new ExpandedDRLInfo( hasDsl );
        int lineCounter = -1;
        for ( String statement : lhsStatements ) {
            lineCounter++;
            String trimmed = statement.trim();
            if ( trimmed.startsWith( ">" ) ) {
                drl.append( trimmed.substring( 1 ) ).append( "\n" );
            } else {
                expandedDRLInfo.dslStatementsInLhs.put( lineCounter, trimmed );
            }
        }

        drl.append( thenLine ).append( "\n" );

        lineCounter = -1;
        for ( String statement : rhsStatements ) {
            lineCounter++;
            String trimmed = statement.trim();
            if ( trimmed.endsWith( "end" ) ) {
                trimmed = trimmed.substring( 0, trimmed.length() - 3 ).trim();
            }
            if ( trimmed.length() > 0 ) {
                if ( trimmed.startsWith( ">" ) ) {
                    drl.append( trimmed.substring( 1 ) ).append( "\n" );
                } else {
                    expandedDRLInfo.dslStatementsInRhs.put( lineCounter, trimmed );
                }
            }
        }

        expandedDRLInfo.plainDrl = drl.toString();

        return expandedDRLInfo;
    }

    private ExpandedDRLInfo processFreeFormStatement( StringBuilder drl,
                                                      String thenLine,
                                                      List<String> lhsStatements,
                                                      List<String> rhsStatements ) {
        ExpandedDRLInfo expandedDRLInfo = new ExpandedDRLInfo( false );

        int lineCounter = -1;
        for ( String statement : lhsStatements ) {
            lineCounter++;
            if ( isValidLHSStatement( statement ) ) {
                drl.append( statement ).append( "\n" );
            } else {
                expandedDRLInfo.freeFormStatementsInLhs.put( lineCounter, statement );
            }
        }

        drl.append( thenLine ).append( "\n" );

        expandedDRLInfo.consequence = "";
        lineCounter = -1;
        for ( String statement : rhsStatements ) {
            String trimmed = statement.trim();
            if ( trimmed.endsWith( "end" ) ) {
                trimmed = trimmed.substring( 0, trimmed.length() - 3 );
            }
            if ( trimmed.length() > 0 ) {
                expandedDRLInfo.consequence += ( trimmed + "\n" );
            }
            drl.append( statement ).append( "\n" );
        }

        expandedDRLInfo.plainDrl = drl.toString();
        return expandedDRLInfo;
    }

    private boolean isValidLHSStatement( String lhs ) {
        // TODO: How to identify a non valid (free form) lhs statement?
        return lhs.indexOf( '(' ) >= 0 || lhs.indexOf( ':' ) >= 0;
    }

    private enum RuleSection {HEADER, LHS, RHS}

    private static class ExpandedDRLInfo {

        private final boolean hasDsl;
        private String plainDrl;
        private String consequence;

        private Map<Integer, String> dslStatementsInLhs;
        private Map<Integer, String> dslStatementsInRhs;

        private Map<Integer, String> freeFormStatementsInLhs;

        private List<String> lhsDslPatterns;
        private List<String> rhsDslPatterns;

        private Set<String> globals = new HashSet<String>();

        private ExpandedDRLInfo( boolean hasDsl ) {
            this.hasDsl = hasDsl;
            dslStatementsInLhs = new HashMap<Integer, String>();
            dslStatementsInRhs = new HashMap<Integer, String>();
            freeFormStatementsInLhs = new HashMap<Integer, String>();
            lhsDslPatterns = new ArrayList<String>();
            rhsDslPatterns = new ArrayList<String>();
        }

        public boolean hasGlobal( String name ) {
            return globals.contains( name );
        }

        public ExpandedDRLInfo registerGlobals( PackageDataModelOracle dmo,
                                                List<String> globalStatements ) {
            if ( globalStatements != null ) {
                for ( String globalStatement : globalStatements ) {
                    String identifier = getIdentifier( globalStatement );
                    if ( identifier != null ) {
                        globals.add( identifier );
                    }
                }
            }
            Map<String, String> globalsFromDmo = dmo != null ? dmo.getPackageGlobals() : null;
            if ( globalsFromDmo != null ) {
                globals.addAll( globalsFromDmo.keySet() );
            }
            return this;
        }

        private String getIdentifier( String globalStatement ) {
            globalStatement = globalStatement.trim();
            if ( !globalStatement.startsWith( "global" ) ) {
                return null;
            }
            int lastSpace = globalStatement.lastIndexOf( ' ' );
            if ( lastSpace < 0 ) {
                return null;
            }
            String identifier = globalStatement.substring( lastSpace + 1 );
            if ( identifier.endsWith( ";" ) ) {
                identifier = identifier.substring( 0, identifier.length() - 1 );
            }
            return identifier;
        }

        public ExpandedDRLInfo registerGlobalDescrs( List<GlobalDescr> globalDescrs ) {
            if ( globalDescrs != null ) {
                for ( GlobalDescr globalDescr : globalDescrs ) {
                    globals.add( globalDescr.getIdentifier() );
                }
            }
            return this;
        }
    }

    private RuleDescr parseDrl( ExpandedDRLInfo expandedDRLInfo ) {
        DrlParser drlParser = new DrlParser();
        PackageDescr packageDescr;
        try {
            packageDescr = drlParser.parse( true, expandedDRLInfo.plainDrl );
        } catch ( DroolsParserException e ) {
            throw new RuntimeException( e );
        }
        expandedDRLInfo.registerGlobalDescrs( packageDescr.getGlobals() );
        return packageDescr.getRules().get( 0 );
    }

    private boolean parseAttributes( RuleModel m,
                                     Map<String, AttributeDescr> attributes ) {
        boolean isJavaDialect = false;
        for ( Map.Entry<String, AttributeDescr> entry : attributes.entrySet() ) {
            String name = entry.getKey();
            String value = normalizeAtributeValue( entry.getValue().getValue().trim() );
            RuleAttribute ruleAttribute = new RuleAttribute( name, value );
            m.addAttribute( ruleAttribute );
            isJavaDialect |= name.equals( "dialect" ) && value.equals( "java" );
        }
        return isJavaDialect;
    }

    private String normalizeAtributeValue( String value ) {
        if ( value.startsWith( "[" ) ) {
            value = value.substring( 1, value.length() - 1 ).trim();
        }
        if ( value.startsWith( "\"" ) ) {
            StringBuilder sb = new StringBuilder();
            String[] split = value.split( "," );
            sb.append( stripQuotes( split[ 0 ].trim() ) );
            for ( int i = 1; i < split.length; i++ ) {
                sb.append( ", " ).append( stripQuotes( split[ i ].trim() ) );
            }
            value = sb.toString();
        }
        return value;
    }

    private String stripQuotes( String value ) {
        if ( value.startsWith( "\"" ) ) {
            value = value.substring( 1, value.length() - 1 ).trim();
        }
        return value;
    }

    private Map<String, String> parseLhs( RuleModel m,
                                          AndDescr lhs,
                                          ExpandedDRLInfo expandedDRLInfo,
                                          PackageDataModelOracle dmo ) {
        Map<String, String> boundParams = new HashMap<String, String>();
        int lineCounter = -1;
        for ( BaseDescr descr : lhs.getDescrs() ) {
            lineCounter = parseNonDrlInLhs( m, expandedDRLInfo, lineCounter );
            IPattern pattern = parseBaseDescr( m, descr, boundParams, dmo );
            if ( pattern != null ) {
                m.addLhsItem( pattern );
            }
        }
        parseNonDrlInLhs( m, expandedDRLInfo, lineCounter );
        return boundParams;
    }

    private int parseNonDrlInLhs( RuleModel m,
                                  ExpandedDRLInfo expandedDRLInfo,
                                  int lineCounter ) {
        lineCounter++;
        lineCounter = parseDslInLhs( m, expandedDRLInfo, lineCounter );
        lineCounter = parseFreeForm( m, expandedDRLInfo, lineCounter );
        return lineCounter;
    }

    private int parseDslInLhs( RuleModel m,
                               ExpandedDRLInfo expandedDRLInfo,
                               int lineCounter ) {
        if ( expandedDRLInfo.hasDsl ) {
            String dslLine = expandedDRLInfo.dslStatementsInLhs.get( lineCounter );
            while ( dslLine != null ) {
                m.addLhsItem( toDSLSentence( expandedDRLInfo.lhsDslPatterns, dslLine ) );
                dslLine = expandedDRLInfo.dslStatementsInLhs.get( ++lineCounter );
            }
        }
        return lineCounter;
    }

    private int parseFreeForm( RuleModel m,
                               ExpandedDRLInfo expandedDRLInfo,
                               int lineCounter ) {
        String freeForm = expandedDRLInfo.freeFormStatementsInLhs.get( lineCounter );
        while ( freeForm != null ) {
            FreeFormLine ffl = new FreeFormLine();
            ffl.setText( freeForm );
            m.addLhsItem( ffl );
            freeForm = expandedDRLInfo.freeFormStatementsInLhs.get( ++lineCounter );
        }
        return lineCounter;
    }

    private IPattern parseBaseDescr( RuleModel m,
                                     BaseDescr descr,
                                     Map<String, String> boundParams,
                                     PackageDataModelOracle dmo ) {
        if ( descr instanceof PatternDescr ) {
            return parsePatternDescr( m, (PatternDescr) descr, boundParams, dmo );
        } else if ( descr instanceof AndDescr ) {
            AndDescr andDescr = (AndDescr) descr;
            return parseBaseDescr( m, andDescr.getDescrs().get( 0 ), boundParams, dmo );
        } else if ( descr instanceof EvalDescr ) {
            FreeFormLine freeFormLine = new FreeFormLine();
            freeFormLine.setText( "eval( " + ( (EvalDescr) descr ).getContent() + " )" );
            return freeFormLine;
        } else if ( descr instanceof ConditionalElementDescr ) {
            return parseExistentialElementDescr( m, (ConditionalElementDescr) descr, boundParams, dmo );
        }
        return null;
    }

    private IFactPattern parsePatternDescr( RuleModel m,
                                            PatternDescr pattern,
                                            Map<String, String> boundParams,
                                            PackageDataModelOracle dmo ) {
        if ( pattern.getSource() != null ) {
            return parsePatternSource( m, pattern, pattern.getSource(), boundParams, dmo );
        }
        return getFactPattern( m, pattern, boundParams, dmo );
    }

    private FactPattern getFactPattern( RuleModel m,
                                        PatternDescr pattern,
                                        Map<String, String> boundParams,
                                        PackageDataModelOracle dmo ) {
        String type = pattern.getObjectType();
        FactPattern factPattern = new FactPattern( getSimpleFactType( type,
                                                                      dmo ) );
        if ( pattern.getIdentifier() != null ) {
            String identifier = pattern.getIdentifier();
            factPattern.setBoundName( identifier );
            boundParams.put( identifier, type );
        }

        parseConstraint( m,
                         factPattern,
                         pattern.getConstraint(),
                         boundParams,
                         dmo );

        for ( BehaviorDescr behavior : pattern.getBehaviors() ) {
            if ( behavior.getText().equals( "window" ) ) {
                CEPWindow window = new CEPWindow();
                window.setOperator( "over window:" + behavior.getSubType() );
                window.setParameter( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator",
                                     "org.drools.workbench.models.commons.backend.rule.CEPWindowOperatorParameterDRLBuilder" );
                List<String> params = behavior.getParameters();
                if ( params != null ) {
                    int i = 1;
                    for ( String param : params ) {
                        window.setParameter( "" + i++, param );
                    }
                }
                factPattern.setWindow( window );
            }
        }
        return factPattern;
    }

    private IFactPattern parsePatternSource( RuleModel m,
                                             PatternDescr pattern,
                                             PatternSourceDescr patternSource,
                                             Map<String, String> boundParams,
                                             PackageDataModelOracle dmo ) {
        if ( patternSource instanceof AccumulateDescr ) {
            AccumulateDescr accumulate = (AccumulateDescr) patternSource;
            FromAccumulateCompositeFactPattern fac = new FromAccumulateCompositeFactPattern();
            fac.setSourcePattern( parseBaseDescr( m, accumulate.getInput(), boundParams, dmo ) );
            fac.setFactPattern( new FactPattern( pattern.getObjectType() ) );
            for ( AccumulateDescr.AccumulateFunctionCallDescr func : accumulate.getFunctions() ) {
                String funcName = func.getFunction();
                boolean first = true;
                StringBuilder sb = new StringBuilder();
                for ( String param : func.getParams() ) {
                    if ( first ) {
                        first = false;
                    } else {
                        sb.append( ", " );
                    }
                    sb.append( param );
                }
                fac.setFunction( funcName + "(" + sb + ")" );
                break;
            }
            return fac;
        } else if ( patternSource instanceof CollectDescr ) {
            CollectDescr collect = (CollectDescr) patternSource;
            FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
            fac.setRightPattern( parseBaseDescr( m, collect.getInputPattern(), boundParams, dmo ) );
            fac.setFactPattern( new FactPattern( pattern.getObjectType() ) );
            return fac;
        } else if ( patternSource instanceof EntryPointDescr ) {
            EntryPointDescr entryPoint = (EntryPointDescr) patternSource;
            FromEntryPointFactPattern fep = new FromEntryPointFactPattern();
            fep.setEntryPointName( entryPoint.getText() );
            fep.setFactPattern( getFactPattern( m, pattern, boundParams, dmo ) );
            return fep;
        } else if ( patternSource instanceof FromDescr ) {
            FromDescr from = (FromDescr) patternSource;
            FromCompositeFactPattern fcfp = new FromCompositeFactPattern();
            FactPattern factPattern = new FactPattern( pattern.getObjectType() );
            fcfp.setFactPattern( factPattern );
            ExpressionFormLine expression = new ExpressionFormLine();
            fcfp.setExpression( expression );

            String dataSource = from.getDataSource().toString();
            String[] splitSource = dataSource.split( "\\." );
            ModelField[] fields = null;
            for ( int i = 0; i < splitSource.length; i++ ) {
                String sourcePart = splitSource[ i ];
                if ( i == 0 ) {
                    String type = boundParams.get( sourcePart );
                    expression.appendPart( new ExpressionVariable( sourcePart, type, DataType.TYPE_NUMERIC ) );
                    fields = findFields( dmo, m, type );
                } else {
                    ModelField modelField = null;
                    for ( ModelField field : fields ) {
                        if ( field.getName().equals( sourcePart ) ) {
                            modelField = field;
                            break;
                        }
                    }
                    expression.appendPart( new ExpressionField( sourcePart, modelField.getClassName(), modelField.getType() ) );
                    fields = findFields( dmo, m, modelField.getClassName() );
                }
            }

            return fcfp;
        }
        throw new RuntimeException( "Unknown pattern source " + patternSource );
    }

    private CompositeFactPattern parseExistentialElementDescr( RuleModel m,
                                                               ConditionalElementDescr conditionalDescr,
                                                               Map<String, String> boundParams,
                                                               PackageDataModelOracle dmo ) {
        CompositeFactPattern comp = conditionalDescr instanceof NotDescr ?
                new CompositeFactPattern( CompositeFactPattern.COMPOSITE_TYPE_NOT ) :
                conditionalDescr instanceof OrDescr ?
                        new CompositeFactPattern( CompositeFactPattern.COMPOSITE_TYPE_OR ) :
                        new CompositeFactPattern( CompositeFactPattern.COMPOSITE_TYPE_EXISTS );
        addPatternToComposite( m, conditionalDescr, comp, boundParams, dmo );
        IFactPattern[] patterns = comp.getPatterns();
        return patterns != null && patterns.length > 0 ? comp : null;
    }

    private void addPatternToComposite( RuleModel m,
                                        ConditionalElementDescr conditionalDescr,
                                        CompositeFactPattern comp,
                                        Map<String, String> boundParams,
                                        PackageDataModelOracle dmo ) {
        for ( Object descr : conditionalDescr.getDescrs() ) {
            if ( descr instanceof PatternDescr ) {
                comp.addFactPattern( parsePatternDescr( m, (PatternDescr) descr, boundParams, dmo ) );
            } else if ( descr instanceof ConditionalElementDescr ) {
                addPatternToComposite( m, (ConditionalElementDescr) descr, comp, boundParams, dmo );
            }
        }
    }

    private void parseConstraint( RuleModel m,
                                  FactPattern factPattern,
                                  ConditionalElementDescr constraint,
                                  Map<String, String> boundParams,
                                  PackageDataModelOracle dmo ) {
        for ( BaseDescr descr : constraint.getDescrs() ) {
            if ( descr instanceof ExprConstraintDescr ) {
                ExprConstraintDescr exprConstraint = (ExprConstraintDescr) descr;
                Expr expr = parseExpr( exprConstraint.getExpression(), boundParams, dmo );
                factPattern.addConstraint( expr.asFieldConstraint( m, factPattern ) );
            }
        }
    }

    private static String findOperator( String expr ) {
        final Set<String> potentialOperators = new HashSet<String>();
        for ( Operator op : Operator.getAllOperators() ) {
            if ( op.isNegated() ) {
                if ( expr.contains( " not " + op.getOperatorString() ) ) {
                    return "not " + op.getOperatorString();
                }
            }
            if ( expr.contains( op.getOperatorString() ) ) {
                potentialOperators.add( op.getOperatorString() );
            }
        }
        String operator = "";
        if ( !potentialOperators.isEmpty() ) {
            for ( String potentialOperator : potentialOperators ) {
                if ( potentialOperator.length() > operator.length() ) {
                    operator = potentialOperator;
                }
            }
        }
        if ( !operator.isEmpty() ) {
            return operator;
        }

        if ( expr.contains( "not in" ) ) {
            return "not in";
        }
        if ( expr.contains( " in" ) ) {
            return "in";
        }
        return null;
    }

    private static final String[] NULL_OPERATORS = new String[]{ "== null", "!= null" };

    private static String findNullOrNotNullOperator( String expr ) {
        for ( String op : NULL_OPERATORS ) {
            if ( expr.contains( op ) ) {
                return op;
            }
        }
        return null;
    }

    private void parseRhs( RuleModel m,
                           String rhs,
                           boolean isJavaDialect,
                           Map<String, String> boundParams,
                           ExpandedDRLInfo expandedDRLInfo,
                           PackageDataModelOracle dmo ) {
        PortableWorkDefinition pwd = null;
        Map<String, List<String>> setStatements = new HashMap<String, List<String>>();
        Map<String, Integer> setStatementsPosition = new HashMap<String, Integer>();
        Map<String, String> factsType = new HashMap<String, String>();

        int lineCounter = -1;
        String[] lines = rhs.split( "\n" );
        for ( String line : lines ) {
            lineCounter++;
            if ( expandedDRLInfo.hasDsl ) {
                String dslLine = expandedDRLInfo.dslStatementsInRhs.get( lineCounter );
                while ( dslLine != null ) {
                    m.addRhsItem( toDSLSentence( expandedDRLInfo.rhsDslPatterns, dslLine ) );
                    dslLine = expandedDRLInfo.dslStatementsInRhs.get( ++lineCounter );
                }
            }
            line = line.trim();
            if ( line.startsWith( "insertLogical" ) ) {
                String fact = unwrapParenthesis( line );
                String type = getStatementType( fact, factsType );
                if ( type != null ) {
                    ActionInsertLogicalFact action = new ActionInsertLogicalFact( type );
                    m.addRhsItem( action );
                    if ( factsType.containsKey( fact ) ) {
                        addSettersToAction( setStatements, fact, action, isJavaDialect );
                    }
                }
            } else if ( line.startsWith( "insert" ) ) {
                String fact = unwrapParenthesis( line );
                String type = getStatementType( fact, factsType );
                if ( type != null ) {
                    ActionInsertFact action = new ActionInsertFact( type );
                    m.addRhsItem( action );
                    if ( factsType.containsKey( fact ) ) {
                        action.setBoundName( fact );
                        addSettersToAction( setStatements, fact, action, isJavaDialect );
                    }
                }
            } else if ( line.startsWith( "update" ) ) {
                String variable = unwrapParenthesis( line );
                ActionUpdateField action = new ActionUpdateField();
                action.setVariable( variable );
                m.addRhsItem( action );
                addSettersToAction( setStatements, variable, action, isJavaDialect );
            } else if ( line.startsWith( "retract" ) ) {
                String variable = unwrapParenthesis( line );
                m.addRhsItem( new ActionRetractFact( variable ) );
            } else if ( line.startsWith( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem" ) ) {
                ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
                pwd = new PortableWorkDefinition();
                pwd.setName( "WorkItem" );
                awi.setWorkDefinition( pwd );
                m.addRhsItem( awi );
            } else if ( line.startsWith( "wiWorkItem.getParameters().put" ) ) {
                String statement = line.substring( "wiWorkItem.getParameters().put".length() );
                statement = unwrapParenthesis( statement );
                int commaPos = statement.indexOf( ',' );
                String name = statement.substring( 0, commaPos ).trim();
                String value = statement.substring( commaPos + 1 ).trim();
                pwd.addParameter( buildPortableParameterDefinition( name, value, boundParams ) );
            } else if ( line.startsWith( "wim.internalExecuteWorkItem" ) || line.startsWith( "wiWorkItem.setName" ) ) {
                // ignore
            } else {
                int dotPos = line.indexOf( '.' );
                int argStart = line.indexOf( '(' );
                if ( dotPos > 0 && argStart > dotPos ) {
                    String variable = line.substring( 0, dotPos ).trim();
                    if ( isJavaIdentifier( variable ) ) {
                        String methodName = line.substring( dotPos + 1, argStart ).trim();
                        if ( isJavaIdentifier( methodName ) ) {
                            if ( getSettedField( methodName ) != null ) {
                                List<String> setters = setStatements.get( variable );
                                if ( setters == null ) {
                                    setters = new ArrayList<String>();
                                    setStatements.put( variable, setters );
                                }
                                setStatementsPosition.put( variable, lineCounter );
                                setters.add( line );
                            } else if ( methodName.equals( "add" ) && expandedDRLInfo.hasGlobal( variable ) ) {
                                String factName = line.substring( argStart + 1, line.lastIndexOf( ')' ) ).trim();
                                ActionGlobalCollectionAdd actionGlobalCollectionAdd = new ActionGlobalCollectionAdd();
                                actionGlobalCollectionAdd.setGlobalName( variable );
                                actionGlobalCollectionAdd.setFactName( factName );
                                m.addRhsItem( actionGlobalCollectionAdd );
                            } else {
                                ActionCallMethod acm = new ActionCallMethod();
                                acm.setMethodName( methodName );
                                acm.setVariable( variable );
                                acm.setState( 1 );
                                m.addRhsItem( acm );
                                String[] params = unwrapParenthesis( line ).split( "," );

                                MethodInfo methodInfo = null;
                                String variableType = boundParams.get( variable );
                                if ( variableType != null ) {
                                    List<MethodInfo> methods = getMethodInfosForType( m, dmo, variableType );
                                    if ( methods != null ) {
                                        for ( MethodInfo method : methods ) {
                                            if ( method.getName().equals( methodName ) && method.getParams().size() == params.length ) {
                                                methodInfo = method;
                                                break;
                                            }
                                        }
                                    }
                                }

                                int i = 0;
                                for ( String param : params ) {
                                    param = param.trim();
                                    if ( param.length() == 0 ) {
                                        continue;
                                    }
                                    String dataType = methodInfo == null ?
                                            inferDataType( param, isJavaDialect ) :
                                            methodInfo.getParams().get( i++ );
                                    ActionFieldFunction actionFiled = new ActionFieldFunction( null, adjustParam( dataType, param, isJavaDialect ), dataType );
                                    actionFiled.setNature( inferFieldNature( param, boundParams ) );
                                    acm.addFieldValue( actionFiled );
                                }
                            }
                            continue;
                        }
                    }
                }

                int eqPos = line.indexOf( '=' );
                boolean addFreeFormLine = line.trim().length() > 0;
                if ( eqPos > 0 ) {
                    String field = line.substring( 0, eqPos ).trim();
                    if ( "java.text.SimpleDateFormat sdf".equals( field ) || "org.drools.core.process.instance.WorkItemManager wim".equals( field ) ) {
                        addFreeFormLine = false;
                    }
                    String[] split = field.split( " " );
                    if ( split.length == 2 ) {
                        factsType.put( split[ 1 ], split[ 0 ] );
                        addFreeFormLine &= !isInsertedFact( lines, lineCounter, split[ 1 ] );
                    }
                }
                if ( addFreeFormLine ) {
                    FreeFormLine ffl = new FreeFormLine();
                    ffl.setText( line );
                    m.addRhsItem( ffl );
                }
            }
        }

        for ( Map.Entry<String, List<String>> entry : setStatements.entrySet() ) {
            ActionSetField action = new ActionSetField( entry.getKey() );
            addSettersToAction( entry.getValue(), action, isJavaDialect );
            m.addRhsItem( action, setStatementsPosition.get( entry.getKey() ) );
        }

        if ( expandedDRLInfo.hasDsl ) {
            String dslLine = expandedDRLInfo.dslStatementsInRhs.get( ++lineCounter );
            while ( dslLine != null ) {
                m.addRhsItem( toDSLSentence( expandedDRLInfo.rhsDslPatterns, dslLine ) );
                dslLine = expandedDRLInfo.dslStatementsInRhs.get( ++lineCounter );
            }
        }
    }

    private int inferFieldNature( String param,
                                  Map<String, String> boundParams ) {
        if ( param.startsWith( "sdf.parse" ) ) {
            return FieldNatureType.TYPE_UNDEFINED;
        }
        if ( boundParams.keySet().contains( param ) ) {
            return FieldNatureType.TYPE_VARIABLE;
        }
        if ( param.startsWith( "\"" ) ) {
            return FieldNatureType.TYPE_LITERAL;
        }
        if ( param.contains( "+" ) || param.contains( "-" ) || param.contains( "*" ) || param.contains( "/" ) ) {
            return FieldNatureType.TYPE_FORMULA;
        }
        return FieldNatureType.TYPE_UNDEFINED;
    }

    private List<MethodInfo> getMethodInfosForType( RuleModel m,
                                                    PackageDataModelOracle dmo,
                                                    String variableType ) {
        List<MethodInfo> methods = dmo.getProjectMethodInformation().get( variableType );
        if ( methods == null ) {
            for ( String imp : m.getImports().getImportStrings() ) {
                if ( imp.endsWith( "." + variableType ) ) {
                    methods = dmo.getProjectMethodInformation().get( imp );
                    if ( methods != null ) {
                        break;
                    }
                }
            }
        }
        return methods;
    }

    private boolean isInsertedFact( String[] lines,
                                    int lineCounter,
                                    String fact ) {
        for ( int i = lineCounter; i < lines.length; i++ ) {
            String line = lines[ i ].trim();
            if ( line.startsWith( "insert" ) ) {
                if ( fact.equals( unwrapParenthesis( line ) ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    private DSLSentence toDSLSentence( List<String> dslPatterns,
                                       String dslLine ) {
        DSLSentence dslSentence = new DSLSentence();
        for ( String dslPattern : dslPatterns ) {
            //A DSL Pattern can contain Regex itself, for example "When the ages is less than {num:1?[0-9]?[0-9]}"
            String regex = dslPattern.replaceAll( "\\{\\s*[\\:\\[\\]\\?\\*\\+\\-\\.\\^\\$\\|\\(\\)\\w]+\\s*\\}", "(.*)" );
            Matcher m = Pattern.compile( regex ).matcher( dslLine );
            if ( m.matches() ) {
                dslSentence.setDefinition( dslPattern );
                for ( int i = 0; i < m.groupCount(); i++ ) {
                    dslSentence.getValues().get( i ).setValue( m.group( i + 1 ) );
                }
                return dslSentence;
            }
        }
        dslSentence.setDefinition( dslLine );
        return dslSentence;
    }

    private PortableParameterDefinition buildPortableParameterDefinition( String name,
                                                                          String value,
                                                                          Map<String, String> boundParams ) {
        PortableParameterDefinition paramDef;
        String type = boundParams.get( value );
        if ( type != null ) {
            if ( type.equals( "Boolean" ) ) {
                paramDef = new PortableBooleanParameterDefinition();
            } else if ( type.equals( "String" ) ) {
                paramDef = new PortableStringParameterDefinition();
            } else if ( type.equals( "Float" ) ) {
                paramDef = new PortableBooleanParameterDefinition();
            } else if ( type.equals( "Integer" ) ) {
                paramDef = new PortableIntegerParameterDefinition();
            } else {
                paramDef = new PortableObjectParameterDefinition();
            }
            ( (HasBinding) paramDef ).setBinding( value );
        } else if ( value.equals( "true" ) || value.equals( "false" ) || value.equals( "Boolean.TRUE" ) || value.equals( "Boolean.FALSE" ) ) {
            paramDef = new PortableBooleanParameterDefinition();
            boolean b = value.equals( "true" ) || value.equals( "Boolean.TRUE" );
            ( (PortableBooleanParameterDefinition) paramDef ).setValue( b );
        } else if ( value.startsWith( "\"" ) ) {
            paramDef = new PortableStringParameterDefinition();
            ( (PortableStringParameterDefinition) paramDef ).setValue( value.substring( 1, value.length() - 1 ) );
        } else if ( Character.isDigit( value.charAt( 0 ) ) ) {
            if ( value.endsWith( "f" ) ) {
                paramDef = new PortableFloatParameterDefinition();
                ( (PortableFloatParameterDefinition) paramDef ).setValue( Float.parseFloat( value ) );
            } else {
                paramDef = new PortableIntegerParameterDefinition();
                ( (PortableIntegerParameterDefinition) paramDef ).setValue( Integer.parseInt( value ) );
            }
        } else {
            throw new RuntimeException( "Unknown parameter " + value );
        }
        paramDef.setName( name.substring( 1, name.length() - 1 ) );
        return paramDef;
    }

    private void addSettersToAction( Map<String, List<String>> setStatements,
                                     String variable,
                                     ActionFieldList action,
                                     boolean isJavaDialect ) {
        addSettersToAction( setStatements.remove( variable ), action, isJavaDialect );
    }

    private void addSettersToAction( List<String> setters,
                                     ActionFieldList action,
                                     boolean isJavaDialect ) {
        if ( setters != null ) {
            for ( String statement : setters ) {
                int dotPos = statement.indexOf( '.' );
                int argStart = statement.indexOf( '(' );
                String methodName = statement.substring( dotPos + 1, argStart ).trim();
                String field = getSettedField( methodName );
                String value = unwrapParenthesis( statement );
                String dataType = inferDataType( value, isJavaDialect );
                action.addFieldValue( buildFieldValue( isJavaDialect, field, value, dataType ) );
            }
        }
    }

    private ActionFieldValue buildFieldValue( boolean isJavaDialect,
                                              String field,
                                              String value,
                                              String dataType ) {
        if ( value.contains( "wiWorkItem.getResult" ) ) {
            field = field.substring( 0, 1 ).toUpperCase() + field.substring( 1 );
            String wiParam = field.substring( "Results".length() );
            if ( wiParam.equals( "BooleanResult" ) ) {
                return new ActionWorkItemFieldValue( field, DataType.TYPE_BOOLEAN, "WorkItem", wiParam, Boolean.class.getName() );
            } else if ( wiParam.equals( "StringResult" ) ) {
                return new ActionWorkItemFieldValue( field, DataType.TYPE_STRING, "WorkItem", wiParam, String.class.getName() );
            } else if ( wiParam.equals( "IntegerResult" ) ) {
                return new ActionWorkItemFieldValue( field, DataType.TYPE_NUMERIC_INTEGER, "WorkItem", wiParam, Integer.class.getName() );
            } else if ( wiParam.equals( "FloatResult" ) ) {
                return new ActionWorkItemFieldValue( field, DataType.TYPE_NUMERIC_FLOAT, "WorkItem", wiParam, Float.class.getName() );
            }
        }
        ActionFieldValue fieldValue = new ActionFieldValue( field, adjustParam( dataType, value, isJavaDialect ), dataType );
        if ( dataType == DataType.TYPE_COLLECTION || dataType == DataType.TYPE_NUMERIC ) {
            fieldValue.setNature( FieldNatureType.TYPE_FORMULA );
        }
        return fieldValue;
    }

    private boolean isJavaIdentifier( String name ) {
        if ( name == null || name.length() == 0 || !Character.isJavaIdentifierStart( name.charAt( 0 ) ) ) {
            return false;
        }
        for ( int i = 1; i < name.length(); i++ ) {
            if ( !Character.isJavaIdentifierPart( name.charAt( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    private String inferDataType( String param,
                                  boolean isJavaDialect ) {
        if ( param.startsWith( "sdf.parse(\"" ) ) {
            return DataType.TYPE_DATE;
        } else if ( param.startsWith( "\"" ) ) {
            return DataType.TYPE_STRING;
        } else if ( param.equals( "true" ) || param.equals( "false" ) ) {
            return DataType.TYPE_BOOLEAN;
        } else if ( param.endsWith( "B" ) || ( isJavaDialect && param.startsWith( "new java.math.BigDecimal" ) ) ) {
            return DataType.TYPE_NUMERIC_BIGDECIMAL;
        } else if ( param.endsWith( "I" ) || ( isJavaDialect && param.startsWith( "new java.math.BigInteger" ) ) ) {
            return DataType.TYPE_NUMERIC_BIGINTEGER;
        } else if ( param.startsWith( "[" ) && param.endsWith( "]" ) ) {
            return DataType.TYPE_COLLECTION;
        }
        return DataType.TYPE_NUMERIC;
    }

    private String adjustParam( String dataType,
                                String param,
                                boolean isJavaDialect ) {
        if ( dataType == DataType.TYPE_DATE ) {
            return param.substring( "sdf.parse(\"".length(), param.length() - 2 );
        } else if ( dataType == DataType.TYPE_STRING ) {
            return param.substring( 1, param.length() - 1 );
        } else if ( dataType == DataType.TYPE_NUMERIC_BIGDECIMAL || dataType == DataType.TYPE_NUMERIC_BIGINTEGER ) {
            if ( isJavaDialect ) {
                return param.substring( "new java.math.BigDecimal(\"".length(), param.length() - 2 );
            } else {
                return param.substring( 0, param.length() - 1 );
            }
        }
        return param;
    }

    private String getSettedField( String methodName ) {
        if ( methodName.length() > 3 && methodName.startsWith( "set" ) ) {
            String field = methodName.substring( 3 );
            if ( Character.isUpperCase( field.charAt( 0 ) ) ) {
                return field.substring( 0, 1 ).toLowerCase() + field.substring( 1 );
            }
        }
        return null;
    }

    private static String unwrapParenthesis( String s ) {
        int start = s.indexOf( '(' );
        int end = s.lastIndexOf( ')' );
        return s.substring( start + 1, end ).trim();
    }

    private String getStatementType( String fact,
                                     Map<String, String> factsType ) {
        String type = null;
        if ( fact.startsWith( "new " ) ) {
            String inserted = fact.substring( 4 ).trim();
            if ( inserted.endsWith( "()" ) ) {
                type = inserted.substring( 0, inserted.length() - 2 ).trim();
            }
        } else {
            type = factsType.get( fact );
        }
        return type;
    }

    private Expr parseExpr( String expr,
                            Map<String, String> boundParams,
                            PackageDataModelOracle dmo ) {
        List<String> splittedExpr = splitExpression( expr );
        if ( splittedExpr.size() == 1 ) {
            String singleExpr = splittedExpr.get( 0 );
            if ( singleExpr.startsWith( "(" ) ) {
                return parseExpr( singleExpr.substring( 1 ), boundParams, dmo );
            } else if ( singleExpr.startsWith( "eval" ) ) {
                return new EvalExpr( unwrapParenthesis( singleExpr ) );
            } else {
                return new SimpleExpr( singleExpr, boundParams, dmo );
            }
        }
        ComplexExpr complexExpr = new ComplexExpr( splittedExpr.get( 1 ) );
        for ( int i = 0; i < splittedExpr.size(); i += 2 ) {
            complexExpr.subExprs.add( parseExpr( splittedExpr.get( i ), boundParams, dmo ) );
        }
        return complexExpr;
    }

    private enum SplitterState {
        START, EXPR, PIPE, OR, AMPERSAND, AND, NESTED
    }

    private List<String> splitExpression( String expr ) {
        List<String> splittedExpr = new ArrayList<String>();
        int nestingLevel = 0;
        SplitterState status = SplitterState.START;

        StringBuilder sb = new StringBuilder();
        for ( char ch : expr.toCharArray() ) {
            switch ( status ) {
                case START:
                    if ( ch == '(' ) {
                        status = SplitterState.NESTED;
                        nestingLevel++;
                    } else {
                        status = SplitterState.EXPR;
                        sb.append( ch );
                    }
                    break;
                case EXPR:
                    if ( ch == '|' ) {
                        status = SplitterState.PIPE;
                    } else if ( ch == '&' ) {
                        status = SplitterState.AMPERSAND;
                    } else {
                        sb.append( ch );
                    }
                    break;
                case PIPE:
                    if ( ch == '|' ) {
                        status = SplitterState.OR;
                    } else {
                        status = SplitterState.EXPR;
                        sb.append( '|' ).append( ch );
                    }
                    break;
                case AMPERSAND:
                    if ( ch == '&' ) {
                        status = SplitterState.AND;
                    } else {
                        status = SplitterState.EXPR;
                        sb.append( '&' ).append( ch );
                    }
                    break;
                case OR:
                case AND:
                    if ( ch == '=' || ch == '!' || ch == '<' || ch == '>' ) {
                        status = SplitterState.EXPR;
                        sb.append( status == SplitterState.AND ? "&& " : "|| " );
                        sb.append( ch );
                    } else if ( Character.isJavaIdentifierStart( ch ) ) {
                        String currentExpr = sb.toString().trim();
                        if ( currentExpr.length() > 0 ) {
                            splittedExpr.add( currentExpr );
                        }
                        splittedExpr.add( status == SplitterState.AND ? "&&" : "||" );
                        status = SplitterState.EXPR;
                        sb = new StringBuilder();
                        sb.append( ch );
                    } else if ( ch == '(' ) {
                        String currentExpr = sb.toString().trim();
                        if ( currentExpr.length() > 0 ) {
                            splittedExpr.add( currentExpr );
                        }
                        splittedExpr.add( status == SplitterState.AND ? "&&" : "||" );
                        status = SplitterState.NESTED;
                        nestingLevel++;
                        sb = new StringBuilder();
                        sb.append( ch );
                    }
                    break;
                case NESTED:
                    if ( ch == '(' ) {
                        nestingLevel++;
                        sb.append( ch );
                    } else if ( ch == ')' ) {
                        nestingLevel--;
                        if ( nestingLevel == 0 ) {
                            String currentExpr = sb.toString().trim();
                            if ( currentExpr.length() > 0 ) {
                                splittedExpr.add( "(" + currentExpr );
                            }
                            status = SplitterState.EXPR;
                            sb = new StringBuilder();
                        } else {
                            sb.append( ch );
                        }
                    } else {
                        sb.append( ch );
                    }
                    break;
            }
        }
        String currentExpr = sb.toString().trim();
        if ( currentExpr.length() > 0 ) {
            splittedExpr.add( currentExpr );
        }
        return splittedExpr;
    }

    private static String getSimpleFactType( String className,
                                             PackageDataModelOracle dmo ) {
        for ( String type : dmo.getProjectModelFields().keySet() ) {
            if ( type.equals( className ) ) {
                return type.substring( type.lastIndexOf( "." ) + 1 );
            }
        }
        return className;
    }

    private interface Expr {

        FieldConstraint asFieldConstraint( RuleModel m,
                                           FactPattern factPattern );
    }

    private static class SimpleExpr implements Expr {

        private final String expr;
        private final Map<String, String> boundParams;
        private final PackageDataModelOracle dmo;

        private SimpleExpr( String expr,
                            Map<String, String> boundParams,
                            PackageDataModelOracle dmo ) {
            this.expr = expr;
            this.boundParams = boundParams;
            this.dmo = dmo;
        }

        public FieldConstraint asFieldConstraint( RuleModel m,
                                                  FactPattern factPattern ) {
            String fieldName = expr;

            String value = null;
            String operator = findNullOrNotNullOperator( expr );
            if ( operator != null ) {
                int opPos = expr.indexOf( operator );
                fieldName = expr.substring( 0,
                                            opPos ).trim();
            } else {
                operator = findOperator( expr );
                if ( operator != null ) {
                    int opPos = expr.indexOf( operator );
                    fieldName = expr.substring( 0,
                                                opPos ).trim();
                    value = expr.substring( opPos + operator.length(),
                                            expr.length() ).trim();
                }
            }

            return createFieldConstraint( m,
                                          factPattern,
                                          fieldName,
                                          value,
                                          operator,
                                          fieldName.contains( "." ) );
        }

        private SingleFieldConstraint createNullCheckFieldConstraint( RuleModel m,
                                                                      FactPattern factPattern,
                                                                      String fieldName ) {
            return createFieldConstraint( m,
                                          factPattern,
                                          fieldName,
                                          null,
                                          null,
                                          true );
        }

        private SingleFieldConstraint createFieldConstraint( RuleModel m,
                                                             FactPattern factPattern,
                                                             String fieldName,
                                                             String value,
                                                             String operator,
                                                             boolean isExpression ) {
            String operatorParams = null;
            if ( value != null && value.startsWith( "[" ) ) {
                int endSquare = value.indexOf( ']' );
                operatorParams = value.substring( 1, endSquare ).trim();
                value = value.substring( endSquare + 1 ).trim();
            }

            SingleFieldConstraint fieldConstraint = isExpression ?
                    createExpressionBuilderConstraint( m,
                                                       factPattern,
                                                       fieldName,
                                                       operator,
                                                       value ) :
                    createSingleFieldConstraint( m,
                                                 factPattern,
                                                 fieldName,
                                                 operator,
                                                 value );

            if ( operatorParams != null ) {
                int i = 0;
                for ( String param : operatorParams.split( "," ) ) {
                    ( (BaseSingleFieldConstraint) fieldConstraint ).setParameter( "" + i++, param.trim() );
                }
                ( (BaseSingleFieldConstraint) fieldConstraint ).setParameter( "org.drools.workbench.models.commons.backend.rule.visibleParameterSet", "" + i );
                ( (BaseSingleFieldConstraint) fieldConstraint ).setParameter( "org.drools.workbench.models.commons.backend.rule.operatorParameterGenerator",
                                                                              "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder" );
            }

            if ( fieldName.equals( "this" ) && ( operator == null || operator.equals( "!= null" ) ) ) {
                fieldConstraint.setFieldType( DataType.TYPE_THIS );
            }
            fieldConstraint.setFactType( factPattern.getFactType() );

            ModelField field = findField( findFields( m, factPattern.getFactType() ),
                                          fieldConstraint.getFieldName() );

            if ( field != null ) {
                fieldConstraint.setFieldType( field.getType() );
            }
            return fieldConstraint;
        }

        private SingleFieldConstraint createExpressionBuilderConstraint( RuleModel m,
                                                                         FactPattern factPattern,
                                                                         String fieldName,
                                                                         String operator,
                                                                         String value ) {
            // TODO: we should find a way to know when the expression uses a getter and in this case create a plain SingleFieldConstraint
            //int dotPos = fieldName.lastIndexOf('.');
            //SingleFieldConstraint con = createSingleFieldConstraint(dotPos > 0 ? fieldName.substring(dotPos+1) : fieldName, operator, value);

            SingleFieldConstraint con = createSingleFieldConstraintEBLeftSide( m,
                                                                               factPattern,
                                                                               fieldName,
                                                                               operator,
                                                                               value );

            for ( FieldConstraint fieldConstraint : factPattern.getFieldConstraints() ) {
                if ( fieldConstraint instanceof SingleFieldConstraint ) {
                    SingleFieldConstraint sfc = (SingleFieldConstraint) fieldConstraint;
                    if ( sfc.getOperator().equals( "!= null" ) ) {
                        int parentPos = fieldName.indexOf( sfc.getFieldName() + "." );
                        if ( parentPos >= 0 && !fieldName.substring( parentPos + sfc.getFieldName().length() + 1 ).contains( "." ) ) {
                            con.setParent( sfc );
                            break;
                        }
                    }
                }
            }

            if ( con.getParent() == null && !( con instanceof SingleFieldConstraintEBLeftSide ) ) {
                con.setParent( createParentFor( m, factPattern, fieldName ) );
            }

            return con;
        }

        private SingleFieldConstraint createSingleFieldConstraint( RuleModel m,
                                                                   FactPattern factPattern,
                                                                   String fieldName,
                                                                   String operator,
                                                                   String value ) {
            SingleFieldConstraint con = new SingleFieldConstraint();
            fieldName = setFieldBindingOnContraint( fieldName, con );
            con.setFieldName( fieldName );
            setOperatorAndValueOnConstraint( m, operator, value, factPattern, con );
            return con;
        }

        private SingleFieldConstraintEBLeftSide createSingleFieldConstraintEBLeftSide( RuleModel m,
                                                                                       FactPattern factPattern,
                                                                                       String fieldName,
                                                                                       String operator,
                                                                                       String value ) {
            SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();

            fieldName = setFieldBindingOnContraint( fieldName, con );
            String classType = getFQFactType( factPattern.getFactType() );
            con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( factPattern ) );

            parseExpression( m, classType, fieldName, con.getExpressionLeftSide() );

            setOperatorAndValueOnConstraint( m,
                                             operator,
                                             value,
                                             factPattern,
                                             con );

            return con;
        }

        private ExpressionFormLine parseExpression( RuleModel m,
                                                    String factType,
                                                    String fieldName,
                                                    ExpressionFormLine expression ) {
            String[] splits = fieldName.split( "\\." );

            boolean isBoundParam = false;
            if ( factType == null ) {
                factType = getFQFactType( boundParams.get( splits[ 0 ].trim() ) );
                isBoundParam = true;
            }

            ModelField[] typeFields = findFields( m, factType );

            for ( int i = 0; i < splits.length - 1; i++ ) {
                String expressionPart = normalizeExpressionPart( splits[ i ] );
                if ( "this".equals( expressionPart ) ) {
                    expression.appendPart( new ExpressionField( expressionPart,
                                                                getSimpleFactType( factType,
                                                                                   dmo ),
                                                                DataType.TYPE_THIS ) );
                } else if ( isBoundParam ) {
                    ModelField currentFact = findFact( dmo.getProjectModelFields(),
                                                       factType );
                    expression.appendPart( new ExpressionVariable( expressionPart,
                                                                   getSimpleFactType( currentFact.getClassName(),
                                                                                      dmo ),
                                                                   getSimpleFactType( currentFact.getType(),
                                                                                      dmo ) ) );
                    isBoundParam = false;
                } else {
                    ModelField currentField = findField( typeFields,
                                                         expressionPart );
                    expression.appendPart( new ExpressionField( expressionPart,
                                                                getSimpleFactType( currentField.getClassName(),
                                                                                   dmo ),
                                                                getSimpleFactType( currentField.getType(),
                                                                                   dmo ) ) );
                    typeFields = findFields( m, currentField.getClassName() );
                }
            }
            String expressionPart = normalizeExpressionPart( splits[ splits.length - 1 ] );
            ModelField currentField = findField( typeFields,
                                                 expressionPart );
            expression.appendPart( new ExpressionField( expressionPart,
                                                        getSimpleFactType( currentField.getClassName(),
                                                                           dmo ),
                                                        getSimpleFactType( currentField.getType(),
                                                                           dmo ) ) );
            return expression;
        }

        private String normalizeExpressionPart( String expressionPart ) {
            int parenthesisPos = expressionPart.indexOf( '(' );
            if ( parenthesisPos > 0 ) {
                expressionPart = expressionPart.substring( 0, parenthesisPos );
            }
            return expressionPart.trim();
        }

        private String getFQFactType( String factType ) {
            for ( String type : dmo.getProjectModelFields().keySet() ) {
                if ( type.endsWith( "." + factType ) ) {
                    return type;
                }
            }
            return factType;
        }

        private ModelField findFact( Map<String, ModelField[]> modelFields,
                                     String factType ) {
            final ModelField[] typeFields = modelFields.get( factType );
            if ( typeFields == null ) {
                return null;
            }
            for ( ModelField typeField : typeFields ) {
                if ( typeField.getType().equals( DataType.TYPE_THIS ) ) {
                    return typeField;
                }
            }
            return null;
        }

        private ModelField[] findFields( RuleModel m,
                                         String type ) {
            return RuleModelDRLPersistenceImpl.findFields( dmo, m, type );
        }

        private ModelField findField( ModelField[] typeFields,
                                      String fieldName ) {
            if ( typeFields != null && fieldName != null ) {
                for ( ModelField typeField : typeFields ) {
                    if ( typeField.getName().equals( fieldName ) ) {
                        return typeField;
                    }
                }
            }
            return null;
        }

        private SingleFieldConstraint createParentFor( RuleModel m,
                                                       FactPattern factPattern,
                                                       String fieldName ) {
            int dotPos = fieldName.lastIndexOf( '.' );
            if ( dotPos > 0 ) {
                SingleFieldConstraint constraint = createNullCheckFieldConstraint( m, factPattern, fieldName.substring( 0, dotPos ) );
                factPattern.addConstraint( constraint );
                return constraint;
            }
            return null;
        }

        private String setFieldBindingOnContraint( String fieldName,
                                                   SingleFieldConstraint con ) {
            int colonPos = fieldName.indexOf( ':' );
            if ( colonPos > 0 ) {
                String fieldBinding = fieldName.substring( 0, colonPos ).trim();
                con.setFieldBinding( fieldBinding );
                fieldName = fieldName.substring( colonPos + 1 ).trim();
            }
            return fieldName;
        }

        private String setOperatorAndValueOnConstraint( RuleModel m,
                                                        String operator,
                                                        String value,
                                                        FactPattern factPattern,
                                                        SingleFieldConstraint con ) {
            con.setOperator( operator );
            String type = null;
            boolean isAnd = false;
            String[] splittedValue = new String[ 0 ];
            if ( value != null ) {
                isAnd = value.contains( "&&" );
                splittedValue = isAnd ? value.split( "\\&\\&" ) : value.split( "\\|\\|" );
                type = setValueOnConstraint( m, operator, factPattern, con, splittedValue[ 0 ].trim() );
            }

            if ( splittedValue.length > 1 ) {
                ConnectiveConstraint[] connectiveConstraints = new ConnectiveConstraint[ splittedValue.length - 1 ];
                for ( int i = 0; i < connectiveConstraints.length; i++ ) {
                    String constraint = splittedValue[ i + 1 ].trim();
                    String connectiveOperator = findOperator( constraint );
                    String connectiveValue = constraint.substring( connectiveOperator.length() ).trim();

                    connectiveConstraints[ i ] = new ConnectiveConstraint();
                    connectiveConstraints[ i ].setOperator( ( isAnd ? "&& " : "|| " ) + connectiveOperator );
                    setValueOnConstraint( m, operator, factPattern, connectiveConstraints[ i ], connectiveValue );
                }
                con.setConnectives( connectiveConstraints );
            }
            return type;
        }

        private String setValueOnConstraint( RuleModel m,
                                             String operator,
                                             FactPattern factPattern,
                                             BaseSingleFieldConstraint con,
                                             String value ) {
            String type = null;
            if ( value.startsWith( "\"" ) ) {
                type = DataType.TYPE_STRING;
                con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
                con.setValue( value.substring( 1, value.length() - 1 ) );
            } else if ( value.startsWith( "(" ) ) {
                if ( operator != null && operator.contains( "in" ) ) {
                    value = unwrapParenthesis( value );
                    type = value.startsWith( "\"" ) ? DataType.TYPE_STRING : DataType.TYPE_NUMERIC_INTEGER;
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
                    con.setValue( value );
                } else {
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_RET_VALUE );
                    con.setValue( unwrapParenthesis( value ) );
                }
            } else {
                if ( !Character.isDigit( value.charAt( 0 ) ) ) {
                    if ( value.equals( "true" ) || value.equals( "false" ) ) {
                        type = DataType.TYPE_BOOLEAN;
                        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
                    } else if ( isEnumerationValue( factPattern,
                                                    con ) ) {
                        type = DataType.TYPE_COMPARABLE;
                        con.setConstraintValueType( SingleFieldConstraint.TYPE_ENUM );
                    } else if ( value.indexOf( '.' ) > 0 && boundParams.containsKey( value.substring( 0, value.indexOf( '.' ) ).trim() ) ) {
                        con.setExpressionValue( parseExpression( m, null, value, new ExpressionFormLine() ) );
                        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE );
                        value = "";
                    } else {
                        con.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
                    }
                } else {
                    if ( value.endsWith( "I" ) ) {
                        type = DataType.TYPE_NUMERIC_BIGINTEGER;
                        value = value.substring( 0, value.length() - 1 );
                    } else if ( value.endsWith( "B" ) ) {
                        type = DataType.TYPE_NUMERIC_BIGDECIMAL;
                        value = value.substring( 0, value.length() - 1 );
                    } else if ( value.endsWith( "f" ) ) {
                        type = DataType.TYPE_NUMERIC_FLOAT;
                    } else if ( value.endsWith( "d" ) ) {
                        type = DataType.TYPE_NUMERIC_DOUBLE;
                    } else {
                        type = DataType.TYPE_NUMERIC_INTEGER;
                    }
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
                }
                con.setValue( value );
            }
            if ( con instanceof SingleFieldConstraint ) {
                ( (SingleFieldConstraint) con ).setFieldType( type );
            } else if ( con instanceof ConnectiveConstraint ) {
                ( (ConnectiveConstraint) con ).setFieldType( type );
            }
            return type;
        }

        private boolean isEnumerationValue( FactPattern factPattern,
                                            BaseSingleFieldConstraint con ) {
            String factType = null;
            String fieldName = null;
            if ( con instanceof SingleFieldConstraintEBLeftSide ) {
                SingleFieldConstraintEBLeftSide sfcex = (SingleFieldConstraintEBLeftSide) con;
                List<ExpressionPart> sfcexParts = sfcex.getExpressionLeftSide().getParts();
                factType = sfcexParts.get( sfcexParts.size() - 1 ).getPrevious().getClassType();
                fieldName = sfcex.getFieldName();
            } else if ( con instanceof SingleFieldConstraint ) {
                factType = factPattern.getFactType();
                fieldName = ( (SingleFieldConstraint) con ).getFieldName();
            } else if ( con instanceof ConnectiveConstraint ) {
                factType = factPattern.getFactType();
                fieldName = ( (ConnectiveConstraint) con ).getFieldName();
            }

            if ( factType == null || fieldName == null ) {
                return false;
            }

            final String fullyQualifiedFactType = getFQFactType( factType );
            final String key = fullyQualifiedFactType + "#" + fieldName;
            final Map<String, String[]> projectJavaEnumDefinitions = dmo.getProjectJavaEnumDefinitions();

            return projectJavaEnumDefinitions.containsKey( key );
        }
    }

    private static class ComplexExpr implements Expr {

        private final List<Expr> subExprs = new ArrayList<Expr>();
        private final String connector;

        private ComplexExpr( String connector ) {
            this.connector = connector;
        }

        public FieldConstraint asFieldConstraint( RuleModel m,
                                                  FactPattern factPattern ) {
            CompositeFieldConstraint comp = new CompositeFieldConstraint();
            comp.setCompositeJunctionType( connector.equals( "&&" ) ? CompositeFieldConstraint.COMPOSITE_TYPE_AND : CompositeFieldConstraint.COMPOSITE_TYPE_OR );
            for ( Expr expr : subExprs ) {
                comp.addConstraint( expr.asFieldConstraint( m, factPattern ) );
            }
            return comp;
        }
    }

    private static class EvalExpr implements Expr {

        private final String expr;

        private EvalExpr( String expr ) {
            this.expr = expr;
        }

        public FieldConstraint asFieldConstraint( RuleModel m,
                                                  FactPattern factPattern ) {
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setConstraintValueType( SingleFieldConstraint.TYPE_PREDICATE );
            con.setValue( expr );
            return con;
        }
    }

    private static ModelField[] findFields( PackageDataModelOracle dmo,
                                            RuleModel m,
                                            String type ) {
        ModelField[] fields = dmo.getProjectModelFields().get( type );
        if ( fields != null ) {
            return fields;
        }
        for ( String i : m.getImports().getImportStrings() ) {
            if ( i.endsWith( "." + type ) ) {
                fields = dmo.getProjectModelFields().get( i );
                if ( fields != null ) {
                    return fields;
                }
            }
        }

        return dmo.getProjectModelFields().get( m.getPackageName() + "." + type );
    }
}
