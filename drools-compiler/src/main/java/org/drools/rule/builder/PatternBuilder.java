/*
 * Copyright 2006 JBoss Inc
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

package org.drools.rule.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.EvaluatorDefinition.Target;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.DrlExprParser;
import org.drools.compiler.DroolsParserException;
import org.drools.core.util.StringUtils;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateFieldExtractor;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.lang.MVELDumper;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.BehaviorDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.RelationalExprDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.RestrictionDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.SlidingWindowDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.AbstractCompositeConstraint;
import org.drools.rule.AbstractCompositeRestriction;
import org.drools.rule.AndCompositeRestriction;
import org.drools.rule.AndConstraint;
import org.drools.rule.Behavior;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.MultiRestrictionFieldConstraint;
import org.drools.rule.MutableTypeConstraint;
import org.drools.rule.OrCompositeRestriction;
import org.drools.rule.OrConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.PatternSource;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Query;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.SlidingLengthWindow;
import org.drools.rule.SlidingTimeWindow;
import org.drools.rule.UnificationRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.Constraint;
import org.drools.spi.Constraint.ConstraintType;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;
import org.drools.spi.PatternExtractor;
import org.drools.spi.Restriction;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessor;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.util.PropertyTools;

/**
 * A builder for patterns
 */
public class PatternBuilder
    implements
    RuleConditionBuilder {

    public PatternBuilder() {
    }

    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr ) {
        return this.build( context,
                           descr,
                           null );
    }

    /**
     * Build a pattern for the given descriptor in the current 
     * context and using the given utils object
     * 
     * @param context
     * @param utils
     * @param patternDescr
     * @return
     */
    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr,
                                       Pattern prefixPattern ) {

        final PatternDescr patternDescr = (PatternDescr) descr;

        if ( patternDescr.getObjectType() == null || patternDescr.getObjectType().equals( "" ) ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          patternDescr,
                                                          null,
                                                          "ObjectType not correctly defined" ) );
            return null;
        }

        ObjectType objectType = null;

        final FactTemplate factTemplate = context.getPkg().getFactTemplate( patternDescr.getObjectType() );

        if ( factTemplate != null ) {
            objectType = new FactTemplateObjectType( factTemplate );
        } else {
            try {
                final Class userProvidedClass = context.getDialect().getTypeResolver().resolveType( patternDescr.getObjectType() );
                final boolean isEvent = context.getPkg().isEvent( userProvidedClass );
                objectType = new ClassObjectType( userProvidedClass,
                                                  isEvent );
            } catch ( final ClassNotFoundException e ) {
                // swallow as we'll do another check in a moment and then record the problem
            }
        }

        // lets see if it maps to a query
        if ( objectType == null ) {
            Rule rule = context.getPkg().getRule( patternDescr.getObjectType() );
            if ( rule != null && rule instanceof Query ) {
                // it's a query so delegate to the QueryElementBuilder
                QueryElementBuilder qeBuilder = new QueryElementBuilder();
                return qeBuilder.build( context,
                                        descr,
                                        prefixPattern );
            } else {
                // this isn't a query either, so log an error
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              patternDescr,
                                                              null,
                                                              "Unable to resolve ObjectType '" + patternDescr.getObjectType() + "'" ) );
                return null;
            }
        }

        Pattern pattern;

        boolean duplicateBindings = context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                                                   patternDescr.getIdentifier() );

        if ( !StringUtils.isEmpty( patternDescr.getIdentifier() ) && !duplicateBindings ) {

            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   patternDescr.getIdentifier(),
                                   patternDescr.isInternalFact() );
            if ( objectType instanceof ClassObjectType ) {
                // make sure PatternExtractor is wired up to correct ClassObjectType and set as a target for rewiring
                context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType),
                                                                                  (PatternExtractor) pattern.getDeclaration().getExtractor() );
            }
        } else {
            pattern = new Pattern( context.getNextPatternId(),
                                   0, // offset is 0 by default
                                   objectType,
                                   null );
        }

        if ( duplicateBindings ) {
            // rewrite existing bindings into == constraints, so it unifies
            FieldConstraintDescr varDescr = new FieldConstraintDescr( "this" );
            varDescr.addRestriction( new VariableRestrictionDescr( "==",
                                                                   patternDescr.getIdentifier() ) );
            build( context,
                   pattern,
                   (FieldConstraintDescr) varDescr,
                   null );
        }

        if ( objectType instanceof ClassObjectType ) {
            // make sure the Pattern is wired up to correct ClassObjectType and set as a target for rewiring
            context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType),
                                                                              pattern );
        }

        // adding the newly created pattern to the build stack this is necessary in case of local declaration usage
        context.getBuildStack().push( pattern );

        List<DescrBranch> literalConstraints = new ArrayList<DescrBranch>();
        List<DescrBranch> literalIndexes = new ArrayList<DescrBranch>();
        List<DescrBranch> variableConstraints = new ArrayList<DescrBranch>();
        List<DescrBranch> variableIndexes = new ArrayList<DescrBranch>();

        for ( BindingDescr b : patternDescr.getBindings() ) {
            if ( true ) { // TODO: replace this by legacy mode configuration
                String expression = b.getExpression();

                DrlExprParser parser = new DrlExprParser();
                ConstraintConnectiveDescr result = parser.parse( expression );
                if ( parser.hasErrors() ) {
                    for ( DroolsParserException error : parser.getErrors() ) {
                        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                      descr,
                                                                      null,
                                                                      "Unable to parser pattern expression:\n" + error.getMessage() ) );
                    }
                    return null;
                }
                String left = parser.getLeftMostExpr();
                // BELLOW is a hack.. need to implement it properly
                if ( expression.equals( left ) ) {
                    // it is just a bind, so build it
                    build( context,
                           pattern,
                           b,
                           null ); // null containers get added to the pattern
                } else {
                    // it is both a binding and a constraint
                    b.setExpression( left );
                    build( context,
                           pattern,
                           b,
                           null ); // null containers get added to the pattern
                    b.setExpression( expression );

                    // needs to build the actual constraints as well
                    processExpr( context,
                                 new ExprConstraintDescr( b.getExpression() ),
                                 literalIndexes,
                                 literalConstraints,
                                 variableIndexes,
                                 variableConstraints );
                }

            } else {
                build( context,
                       pattern,
                       b,
                       null ); // null containers get added to the pattern
            }
        }

        for ( BaseDescr b : patternDescr.getDescrs() ) {
            processExpr( context,
                         (ExprConstraintDescr) b,
                         literalIndexes,
                         literalConstraints,
                         variableIndexes,
                         variableConstraints );
        }

        for ( DescrBranch branch : literalIndexes ) {
            buildLiteralConstraint( context,
                                    pattern,
                                    branch,
                                    null );
        }
        for ( DescrBranch branch : literalConstraints ) {
            buildLiteralConstraint( context,
                                    pattern,
                                    branch,
                                    null );
        }

        for ( DescrBranch branch : variableIndexes ) {
            buildVariableConstraint( context,
                                     pattern,
                                     branch,
                                     null );
        }
        for ( DescrBranch branch : variableConstraints ) {
            buildVariableConstraint( context,
                                     pattern,
                                     branch,
                                     null );
        }

        if ( patternDescr.getSource() != null ) {
            // we have a pattern source, so build it
            RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( patternDescr.getSource().getClass() );

            PatternSource source = (PatternSource) builder.build( context,
                                                                  patternDescr.getSource() );

            pattern.setSource( source );
        }

        for ( BehaviorDescr behaviorDescr : patternDescr.getBehaviors() ) {
            if ( pattern.getObjectType().isEvent() ) {
                if ( Behavior.BehaviorType.TIME_WINDOW.matches( behaviorDescr.getType() ) ) {
                    SlidingWindowDescr swd = (SlidingWindowDescr) behaviorDescr;
                    SlidingTimeWindow window = new SlidingTimeWindow( swd.getLength() );
                    pattern.addBehavior( window );
                } else if ( Behavior.BehaviorType.LENGTH_WINDOW.matches( behaviorDescr.getType() ) ) {
                    SlidingWindowDescr swd = (SlidingWindowDescr) behaviorDescr;
                    SlidingLengthWindow window = new SlidingLengthWindow( (int) swd.getLength() );
                    pattern.addBehavior( window );
                }
            } else {
                // Some behaviors can only be assigned to patterns declared as events
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              patternDescr,
                                                              null,
                                                              "A Sliding Window behavior can only be assigned to patterns declared with @role( event ). The pattern '" + pattern.getObjectType() + "' in the rule '" + context.getRule().getName()
                                                                      + "' is not declared as an Event." ) );
            }
        }

        // poping the pattern
        context.getBuildStack().pop();

        return pattern;
    }

    public void processExpr( RuleBuildContext context,
                             ExprConstraintDescr descr,
                             List<DescrBranch> literalIndexes,
                             List<DescrBranch> literalConstraints,
                             List<DescrBranch> variablesIndexes,
                             List<DescrBranch> variableConstraints ) {
        DrlExprParser parser = new DrlExprParser();
        ConstraintConnectiveDescr result = parser.parse( descr.getText() );
        if ( parser.hasErrors() ) {
            for ( DroolsParserException error : parser.getErrors() ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              descr,
                                                              null,
                                                              "Unable to parser pattern expression:\n" + error.getMessage() ) );
            }
            return;
        }

        for ( Iterator<BaseDescr> it = result.getDescrs().iterator(); it.hasNext(); ) {
            BaseDescr d = it.next();

            boolean indexable = false;
            boolean simple = false;
            boolean returnValue = false;
            if ( d instanceof RelationalExprDescr ) {
                RelationalExprDescr red = (RelationalExprDescr) d;
                if ( red.getLeft() instanceof AtomicExprDescr &&
                      red.getRight() instanceof AtomicExprDescr ) {
                    simple = true;
                    String expr = ((AtomicExprDescr) red.getRight()).getExpression().trim();
                    if ( "==".equals( red.getOperator() ) && (expr != null && !expr.startsWith( "(" )) ) {
                        // we have an indexable constraint
                        indexable = true;
                    } 
                    if( expr.startsWith( "(" ) ) {
                        returnValue = true;
                    }
                }
            }

            StringBuilder sbuilder = new StringBuilder();
            renderConstraint( sbuilder,
                              d );

            String expr = sbuilder.toString().trim();

            if ( expr.startsWith( "eval" ) ) {
                // strip evals, as mvel won't understand those.
                int startParen = expr.indexOf( '(' ) + 1;
                int endParen = expr.lastIndexOf( ')' );
                expr = expr.substring( startParen,
                                       endParen );
            }

            DescrBranch descrBranch = new DescrBranch( expr,
                                                       d,
                                                       simple,
                                                       indexable );

            setInputs( context,
                       descrBranch,
                       ((ClassObjectType) ((Pattern) context.getBuildStack().peek()).getObjectType()).getClassType(),
                       expr );

            boolean literal = descrBranch.getRuleBindings().isEmpty() && 
                              descrBranch.getGlobalBindings().isEmpty() ;

            if ( indexable ) {
                if ( literal ) {
                    literalIndexes.add( descrBranch );
                } else {
                    variablesIndexes.add( descrBranch );
                }
            } else {
                if ( literal && !returnValue ) {
                    literalConstraints.add( descrBranch );
                } else {
                    variableConstraints.add( descrBranch );
                }
            }

        }
    }

    private String builtInOperators = "> >= < <= == != && ||";

    private void renderConstraint( StringBuilder sbuilder,
                                   BaseDescr d ) {
        if ( d instanceof RelationalExprDescr ) {
            RelationalExprDescr red = (RelationalExprDescr) d;
            if ( builtInOperators.contains( ((RelationalExprDescr) d).getOperator() ) ) {
                renderConstraint( sbuilder,
                                  ((RelationalExprDescr) d).getLeft() );
                sbuilder.append( " " );
                sbuilder.append( ((RelationalExprDescr) d).getOperator() );
                sbuilder.append( " " );
                renderConstraint( sbuilder,
                                  ((RelationalExprDescr) d).getRight() );
            } else {
                MVELDumper dumper = new MVELDumper( null );
                dumper.setFieldName( ((AtomicExprDescr) ((RelationalExprDescr) d).getLeft()).getExpression() );
                String operator = red.getOperator();

                // extractor the operator and determine if it's negated or not
                int notPos = operator.indexOf( "not" );
                if ( notPos >= 0 ) {
                    operator = red.getOperator().substring( notPos + 3 );
                }

                // as there is no && or || operator we know this is atomic
                String s = dumper.processRestriction( operator,
                                                      (notPos >= 0),
                                                      ((AtomicExprDescr) ((RelationalExprDescr) d).getRight()).getExpression() );
                sbuilder.append( s );
            }

        } else if ( d instanceof AtomicExprDescr ) {
            sbuilder.append( ((AtomicExprDescr) d).getExpression() );
        } else if ( d instanceof ConstraintConnectiveDescr ) {
            boolean afterFirst = false;
            for ( BaseDescr c : ((ConstraintConnectiveDescr) d).getDescrs() ) {
                if ( afterFirst ) {
                    sbuilder.append( ((ConstraintConnectiveDescr) d).getConnective().toString() );
                } else {
                    afterFirst = true;
                }
                renderConstraint( sbuilder,
                                  c );
            }
        }
    }

    private void setInputs( RuleBuildContext context,
                              DescrBranch descrBranch,
                              Class thisClass,
                            String expr ) {
        MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

        ParserConfiguration conf = new ParserConfiguration();
        conf.setImports( dialect.getImports() );
        conf.setPackageImports( (HashSet) dialect.getPackgeImports() );

        conf.setClassLoader( context.getPackageBuilder().getRootClassLoader() );

        final ParserContext pctx = new ParserContext( conf );
        pctx.setStrictTypeEnforcement( false );
        pctx.setStrongTyping( false );
        pctx.addInput( "this",
                       thisClass );
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.analysisCompile( expr,
                              pctx );

        if ( !pctx.getInputs().isEmpty() ) {
            for ( String v : pctx.getInputs().keySet() ) {
                if ( "this".equals( v ) || PropertyTools.getFieldOrAccessor( thisClass,
                                                                             v ) != null ) {
                    // ignore
                    continue;
                } else if ( !context.getPkg().getGlobals().containsKey( v ) ) {
                    descrBranch.getRuleBindings().add( v );
                } else {
                    descrBranch.getGlobalBindings().add( v );
                }
            }
        }

    }

    public static class DescrBranch {
        private String      expression;
        private BaseDescr   descr;
        private Set<String> globalBindings;
        private Set<String> ruleBindings;
        private boolean     indexable;
        private boolean     simple;

        public DescrBranch(String expression,
                           BaseDescr descr,
                           boolean simple,
                           boolean indexable) {
            this.expression = expression;
            this.descr = descr;
            this.indexable = indexable;
            this.simple = simple;
            this.globalBindings = new HashSet<String>();
            this.ruleBindings = new HashSet<String>();
        }

        public String getExpression() {
            return expression;
        }

        public BaseDescr getDescr() {
            return descr;
        }

        public Set<String> getGlobalBindings() {
            return globalBindings;
        }

        public Set<String> getRuleBindings() {
            return ruleBindings;
        }

        public void setDescr( BaseDescr descr ) {
            this.descr = descr;
        }

        public boolean isIndexable() {
            return indexable;
        }

        public boolean isSimple() {
            return this.simple;
        }

    }

    private void buildConstraint( final RuleBuildContext context,
                                  final Pattern pattern,
                                  final Object constraint,
                                  final AbstractCompositeConstraint container ) {
        if ( constraint instanceof BindingDescr ) {
            build( context,
                   pattern,
                   (BindingDescr) constraint,
                   container );
        } else if ( constraint instanceof FieldConstraintDescr ) {
            build( context,
                   pattern,
                   (FieldConstraintDescr) constraint,
                   container );
        } else if ( constraint instanceof PredicateDescr ) {
            build( context,
                   pattern,
                   (PredicateDescr) constraint,
                   container );
        } else if ( constraint instanceof AndDescr ) {
            AndConstraint and = new AndConstraint();
            for ( Iterator it = ((AndDescr) constraint).getDescrs().iterator(); it.hasNext(); ) {
                this.buildConstraint( context,
                                      pattern,
                                      it.next(),
                                      and );
            }

            if ( container == null ) {
                pattern.addConstraint( and );
            } else {
                if ( and.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
                    this.setConstraintType( pattern,
                                            (MutableTypeConstraint) and );
                }
                container.addConstraint( and );
            }
        } else if ( constraint instanceof OrDescr ) {
            OrConstraint or = new OrConstraint();
            for ( Iterator it = ((OrDescr) constraint).getDescrs().iterator(); it.hasNext(); ) {
                this.buildConstraint( context,
                                      pattern,
                                      it.next(),
                                      or );
            }

            if ( container == null ) {
                pattern.addConstraint( or );
            } else {
                if ( or.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
                    this.setConstraintType( pattern,
                                            (MutableTypeConstraint) or );
                }
                container.addConstraint( or );
            }
        } else {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          (BaseDescr) constraint,
                                                          null,
                                                          "This is a bug: unable to build constraint descriptor: '" + constraint + "' in rule '" + context.getRule().getName() + "'" ) );
        }
    }

    private void buildLiteralConstraint( final RuleBuildContext context,
                                         final Pattern pattern,
                                         final DescrBranch branch,
                                         final AbstractCompositeConstraint container ) {
        if ( branch.isSimple() ) {
            RelationalExprDescr red = (RelationalExprDescr) branch.getDescr();
            String fieldName = ((AtomicExprDescr) red.getLeft()).getExpression();
            AtomicExprDescr right = (AtomicExprDescr) red.getRight();

            // if 'this.' is used, strip it
            String[] identifiers = fieldName.split( "\\." );
            if ( identifiers.length == 2 && "this".equals( identifiers[0] ) ) {
                fieldName = identifiers[1];
            }

            final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                         red,
                                                                         pattern.getObjectType(),
                                                                         fieldName,
                                                                         null,
                                                                         false );

            FieldValue field = null;
            try {

                String value = right.getExpression();
                
                // TODO: the following is a very weak check as strings and number literals might contain such characters
                int dotPos = right.getExpression().indexOf( '.' );
                int parenPos = right.getExpression().indexOf( '(' ); // need to make sure this isn't a method @TODO handle methods/functions
                if ( (!right.isLiteral()) && dotPos >= 0 && parenPos < 0 ) {
                    final String className = value.substring( 0,
                                                              dotPos );
                    String classFieldName = value.substring( dotPos + 1 );
                    try {
                        final Class staticClass = context.getDialect().getTypeResolver().resolveType( className );
                        field = FieldFactory.getFieldValue( staticClass.getField( classFieldName ).get( null ),
                                                            extractor.getValueType(),
                                                            context.getPackageBuilder().getDateFormats() );
                        if ( field.isObjectField() ) {
                            ((ObjectFieldImpl) field).setEnum( true );
                            ((ObjectFieldImpl) field).setEnumName( staticClass.getName() );
                            ((ObjectFieldImpl) field).setFieldName( classFieldName );
                        }
                    } catch ( final ClassNotFoundException e ) {
                        // nothing to do, as it is not a class name with static field
                    } catch ( final Exception e ) {
                        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                      red,
                                                                      e,
                                                                      "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + value + "'" ) );
                    }
                } else {
                    if ( right.isLiteral() && value.startsWith( "\"" ) && context.getConfiguration().isProcessStringEscapes() ) {
                        value = StringUtils.unescapeJava( (String) value );
                    }
                    field = FieldFactory.getFieldValue( value,
                                                        extractor.getValueType(),
                                                        context.getPackageBuilder().getDateFormats() );
                }
            } catch ( final Exception e ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              red,
                                                              e,
                                                              "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + right.getExpression() + "'" ) );
            }

            if ( field == null ) {
                //return null;
                return;
            }

            Target rightTarget = getRightTarget( extractor );
            Target leftTarget = Target.FACT;
            String operator = red.getOperator();

            // extractor the operator and determine if it's negated or not
            int notPos = operator.indexOf( "not" );
            if ( notPos >= 0 ) {
                operator = red.getOperator().substring( notPos + 3 );
            }

            final Evaluator evaluator = getEvaluator( context,
                                                      red,
                                                      extractor.getValueType(),
                                                      operator,
                                                      (notPos >= 0),
                                                      null,
                                                      leftTarget,
                                                      rightTarget );
            if ( evaluator == null ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              red,
                                                              null,
                                                              "Unable to create Evaluator for type '" + extractor.getValueType() + "' on expression '" + branch.getExpression() + "'" ) );
                return;
            }

            LiteralRestriction restriction = new LiteralRestriction( field,
                                                                     evaluator,
                                                                     extractor );
            LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                  (LiteralRestriction) restriction );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (LiteralConstraint) constraint );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  restriction );

            pattern.addConstraint( constraint );
        } else {
            PredicateDescr pdescr = new PredicateDescr( branch.getExpression() );
            build( context,
                   pattern,
                   pdescr,
                   container );
        }
    }

    private void buildVariableConstraint( RuleBuildContext context,
                                          Pattern pattern,
                                          DescrBranch branch,
                                          Object object ) {
        if ( branch.isSimple() ) {
            RelationalExprDescr red = (RelationalExprDescr) branch.getDescr();
            String fieldName = ((AtomicExprDescr) red.getLeft()).getExpression();
            String value = ((AtomicExprDescr) red.getRight()).getExpression().trim();

            // if 'this.' is used, strip it
            String[] identifiers = fieldName.split( "\\." );
            if ( identifiers.length == 2 && "this".equals( identifiers[0] ) ) {
                fieldName = identifiers[1];
            }

            final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                         red,
                                                                         pattern.getObjectType(),
                                                                         fieldName,
                                                                         null,
                                                                         false );

            String operator = red.getOperator();

            // extractor the operator and determine if it's negated or not
            int notPos = operator.indexOf( "not" );
            if ( notPos >= 0 ) {
                red.getOperator().substring( notPos + 3 );
            }

            FieldConstraintDescr fdescr = new FieldConstraintDescr( fieldName );

            Restriction restriction;
            if ( value.startsWith( "(" ) ) {
                // it's a return value
                value = value.substring( 1,
                                         value.length() - 1 );

                restriction = buildRestriction( context,
                                                (Pattern) context.getBuildStack().peek(),
                                                extractor,
                                                fdescr,
                                                new ReturnValueRestrictionDescr( operator,
                                                                                 (notPos >= 0),
                                                                                 null,
                                                                                 value ) );
            } else if ( value.indexOf( '.' ) >= 0 ) {
                restriction = buildRestriction( context,
                                                extractor,
                                                fdescr,
                                                new QualifiedIdentifierRestrictionDescr( operator,
                                                                                         (notPos >= 0),
                                                                                         null,
                                                                                         value ) );
                //(QualifiedIdentifierRestrictionDescr) restrictionDescr );                
            } else {
                restriction = buildRestriction( context,
                                                 extractor,
                                                 fdescr,
                                                 new VariableRestrictionDescr( operator,
                                                                               (notPos >= 0),
                                                                               null,
                                                                               value ) );
                registerReadAccessor( context,
                                      pattern.getObjectType(),
                                      fieldName,
                                      (AcceptsReadAccessor) restriction );
            }

            VariableConstraint constraint = new VariableConstraint( extractor,
                                                                    restriction );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  constraint );
            pattern.addConstraint( constraint );
        } else {
            PredicateDescr pdescr = new PredicateDescr( branch.getExpression() );
            build( context,
                   pattern,
                   pdescr,
                   null );
        }
    }

    private void build( final RuleBuildContext context,
                        final Pattern pattern,
                        final FieldConstraintDescr fieldConstraintDescr,
                        final AbstractCompositeConstraint container ) {
        String fieldName = fieldConstraintDescr.getFieldName();

        //        if ( fieldName.indexOf( '[' ) > -1 ) {
        //            rewriteToEval( context,
        //                           pattern,
        //                           fieldConstraintDescr,
        //                           container );
        //
        //            // after building the predicate, we are done, so return
        //            return;
        //        }

        if ( fieldName.indexOf( '.' ) > -1 ) {
            // we have a composite field name
            String[] identifiers = fieldName.split( "\\." );
            if ( identifiers.length == 2 && ((pattern.getDeclaration() != null && identifiers[0].equals( pattern.getDeclaration().getIdentifier() )) || ("this".equals( identifiers[0] ))) ) {
                // we have a self reference, so, it is fine to do direct access
                fieldName = identifiers[1];
            }
            //             else {
            //                rewriteToEval( context,
            //                               pattern,
            //                               fieldConstraintDescr,
            //                               container );
            //
            //                // after building the predicate, we are done, so return
            //                return;
            //            }
        }

        // if it is not a complex expression, just build a simple field constraint
        final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                     fieldConstraintDescr,
                                                                     pattern.getObjectType(),
                                                                     fieldName,
                                                                     null,
                                                                     false );
        if ( extractor == null ) {
            if ( fieldConstraintDescr.getFieldName().startsWith( "this." ) ) {
                // it may still be MVEL special syntax, like map key syntax, so try eval
                rewriteToEval( context,
                               pattern,
                               fieldConstraintDescr,
                               container );

                // after building the predicate, we are done, so return
                return;
            } else {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              fieldConstraintDescr,
                                                              null,
                                                              "Unable to create Field Extractor for '" + fieldName + "' of '" + pattern.getObjectType().toString() + "' in rule '" + context.getRule().getName() + "'" ) );
                return;
            }
        }

        Restriction restriction = createRestriction( context,
                                                     pattern,
                                                     fieldConstraintDescr,
                                                     fieldConstraintDescr.getRestriction(),
                                                     extractor );

        if ( restriction == null ) {
            // error was already logged during restriction creation failure
            return;
        }

        Constraint constraint = null;
        if ( restriction instanceof AbstractCompositeRestriction ) {
            constraint = new MultiRestrictionFieldConstraint( extractor,
                                                              restriction );
        } else if ( restriction instanceof LiteralRestriction ) {
            constraint = new LiteralConstraint( extractor,
                                                (LiteralRestriction) restriction );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (LiteralConstraint) constraint );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (LiteralRestriction) restriction );
        } else if ( restriction instanceof VariableRestriction || restriction instanceof UnificationRestriction ) {
            constraint = new VariableConstraint( extractor,
                                                 restriction );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (AcceptsReadAccessor) restriction );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (AcceptsReadAccessor) restriction );
        } else if ( restriction instanceof ReturnValueRestriction ) {
            constraint = new ReturnValueConstraint( extractor,
                                                    (ReturnValueRestriction) restriction );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (ReturnValueConstraint) constraint );
            registerReadAccessor( context,
                                  pattern.getObjectType(),
                                  fieldName,
                                  (ReturnValueRestriction) restriction );
        } else {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          fieldConstraintDescr,
                                                          null,
                                                          "This is a bug: Unkown restriction type '" + restriction.getClass() + "' for pattern '" + pattern.getObjectType().toString() + "' in rule '" + context.getRule().getName() + "'" ) );
        }

        if ( container == null ) {
            pattern.addConstraint( constraint );
        } else {
            if ( constraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
                this.setConstraintType( pattern,
                                        (MutableTypeConstraint) constraint );
            }
            container.addConstraint( constraint );
        }
    }

    /**
     * @param pattern
     * @param constraint
     */
    private void setConstraintType( final Pattern container,
                                    final MutableTypeConstraint constraint ) {
        final Declaration[] declarations = constraint.getRequiredDeclarations();

        boolean isAlphaConstraint = true;
        for ( int i = 0; isAlphaConstraint && i < declarations.length; i++ ) {
            if ( !declarations[i].isGlobal() && declarations[i].getPattern() != container ) {
                isAlphaConstraint = false;
            }
        }

        ConstraintType type = isAlphaConstraint ? ConstraintType.ALPHA : ConstraintType.BETA;
        constraint.setType( type );
    }

    private void rewriteToEval( final RuleBuildContext context,
                                final Pattern pattern,
                                final FieldConstraintDescr fieldConstraintDescr,
                                final AbstractCompositeConstraint container ) {
        // it is a complex expression, so we need to turn it into an MVEL predicate
        Dialect dialect = context.getDialect();

        // switch to MVEL dialect
        MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
        boolean strictMode = mvelDialect.isStrictMode();
        mvelDialect.setStrictMode( false );
        context.setDialect( mvelDialect );

        // analyze field type
        Class resultType = getFieldReturnType( pattern,
                                               fieldConstraintDescr );

        PredicateDescr predicateDescr = new PredicateDescr();
        MVELDumper dumper = new MVELDumper( context );
        predicateDescr.setContent( dumper.dump( fieldConstraintDescr,
                                                Date.class.isAssignableFrom( resultType ) ) );

        build( context,
               pattern,
               predicateDescr,
               container );

        mvelDialect.setStrictMode( strictMode );
        // fall back to original dialect
        context.setDialect( dialect );
    }

    /**
     * @param pattern
     * @param fieldConstraintDescr
     * @return
     */
    private Class getFieldReturnType( final Pattern pattern,
                                      final FieldConstraintDescr fieldConstraintDescr ) {
        String dummyField = "__DUMMY__";
        String dummyExpr = dummyField + "." + fieldConstraintDescr.getFieldName();
        ExpressionCompiler compiler = new ExpressionCompiler( dummyExpr );
        ParserContext mvelcontext = new ParserContext();
        mvelcontext.addInput( dummyField,
                              ((ClassObjectType) pattern.getObjectType()).getClassType() );
        compiler.compile( mvelcontext );
        Class resultType = compiler.getReturnType();
        return resultType;
    }

    private Restriction createRestriction( final RuleBuildContext context,
                                           final Pattern pattern,
                                           final FieldConstraintDescr fieldConstraintDescr,
                                           final RestrictionConnectiveDescr top,
                                           final InternalReadAccessor extractor ) {
        Restriction[] restrictions = new Restriction[top.getRestrictions().size()];
        int index = 0;

        for ( Iterator it = top.getRestrictions().iterator(); it.hasNext(); ) {
            RestrictionDescr restrictionDescr = (RestrictionDescr) it.next();

            if ( restrictionDescr instanceof RestrictionConnectiveDescr ) {
                restrictions[index++] = this.createRestriction( context,
                                                                pattern,
                                                                fieldConstraintDescr,
                                                                (RestrictionConnectiveDescr) restrictionDescr,
                                                                extractor );

            } else {
                restrictions[index] = buildRestriction( context,
                                                        pattern,
                                                        extractor,
                                                        fieldConstraintDescr,
                                                        restrictionDescr );
                if ( restrictions[index] == null ) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  fieldConstraintDescr,
                                                                  null,
                                                                  "Unable to create restriction '" + restrictionDescr.toString() + "' for field '" + fieldConstraintDescr.getFieldName() + "' in the rule '" + context.getRule().getName() + "'" ) );
                }
                index++;
            }
        }

        if ( restrictions.length > 1 ) {
            AbstractCompositeRestriction composite = null;
            if ( top.getConnective() == RestrictionConnectiveDescr.AND ) {
                composite = new AndCompositeRestriction( restrictions );
            } else if ( top.getConnective() == RestrictionConnectiveDescr.OR ) {
                composite = new OrCompositeRestriction( restrictions );
            } else {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              fieldConstraintDescr,
                                                              null,
                                                              "This is a bug: Impossible to create a composite restriction for connective: " + top.getConnective() + "' for field '" + fieldConstraintDescr.getFieldName() + "' in the rule '"
                                                                      + context.getRule().getName() + "'" ) );
            }

            return composite;
        } else if ( restrictions.length == 1 ) {
            return restrictions[0];
        }
        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                      fieldConstraintDescr,
                                                      null,
                                                      "This is a bug: trying to create a restriction for an empty restriction list for field '" + fieldConstraintDescr.getFieldName() + "' in the rule '" + context.getRule().getName() + "'" ) );
        return null;
    }

    private void build( final RuleBuildContext context,
                        final Pattern pattern,
                        final BindingDescr fieldBindingDescr,
                        final AbstractCompositeConstraint container ) {

        if ( context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                            fieldBindingDescr.getVariable() ) ) {
            // rewrite existing bindings into == constraints, so it unifies
            FieldConstraintDescr descr = new FieldConstraintDescr( fieldBindingDescr.getExpression() );
            descr.addRestriction( new VariableRestrictionDescr( "==",
                                                                fieldBindingDescr.getVariable() ) );
            build( context,
                   pattern,
                   (FieldConstraintDescr) descr,
                   container );
            return;
        }

        Declaration declr = pattern.addDeclaration( fieldBindingDescr.getVariable() );

        final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                     fieldBindingDescr,
                                                                     pattern.getObjectType(),
                                                                     fieldBindingDescr.getExpression(),
                                                                     declr,
                                                                     true );
    }

    @SuppressWarnings("unchecked")
    private void build( final RuleBuildContext context,
                        final Pattern pattern,
                        final PredicateDescr predicateDescr,
                        final AbstractCompositeConstraint container ) {

        Map<String, Class< ? >> declarations = getDeclarationsMap( predicateDescr,
                                                                   context );
        Map<String, Class< ? >> globals = context.getPackageBuilder().getGlobals();
        Class thisClass = null;
        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
        }

        final AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                predicateDescr,
                                                                                predicateDescr.getContent(),
                                                                                new BoundIdentifiers( declarations,
                                                                                                      globals,
                                                                                                      thisClass ) );

        if ( analysis == null ) {
            // something bad happened
            return;
        }

        // this will return an array with 2 lists
        // where first list is from rule local variables
        // second list is from global variables
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( String id : usedIdentifiers.getDeclarations().keySet() ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                      id );
            if ( decl.getPattern() == pattern ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }
        this.createImplicitBindings( context,
                                     pattern,
                                     analysis.getNotBoundedIdentifiers(),
                                     factDeclarations );

        final Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );
        final String[] requiredGlobals = usedIdentifiers.getGlobals().keySet().toArray( new String[usedIdentifiers.getGlobals().size()] );

        Arrays.sort( previousDeclarations,
                     SortDeclarations.instance );
        Arrays.sort( localDeclarations,
                     SortDeclarations.instance );

        final PredicateConstraint predicateConstraint = new PredicateConstraint( null,
                                                                                 previousDeclarations,
                                                                                 localDeclarations,
                                                                                 requiredGlobals );

        if ( container == null ) {
            pattern.addConstraint( predicateConstraint );
        } else {
            if ( predicateConstraint.getType().equals( Constraint.ConstraintType.UNKNOWN ) ) {
                this.setConstraintType( pattern,
                                        (MutableTypeConstraint) predicateConstraint );
            }
            container.addConstraint( predicateConstraint );
        }

        final PredicateBuilder builder = context.getDialect().getPredicateBuilder();

        builder.build( context,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       predicateConstraint,
                       predicateDescr,
                       analysis );

    }

    private Map<String, Class< ? >> getDeclarationsMap( final BaseDescr baseDescr,
                                                        final RuleBuildContext context ) {
        Map<String, Class< ? >> declarations = new HashMap<String, Class< ? >>();
        for ( Map.Entry<String, Declaration> entry : context.getDeclarationResolver().getDeclarations( context.getRule() ).entrySet() ) {
            if ( entry.getValue().getExtractor() == null ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              baseDescr,
                                                              null,
                                                              "Field Reader does not exist for declaration '" + entry.getKey() + "' in'" + baseDescr + "' in the rule '" + context.getRule().getName() + "'" ) );
                continue;
            }
            declarations.put( entry.getKey(),
                              entry.getValue().getExtractor().getExtractToClass() );
        }
        return declarations;
    }

    /**
     * @param context
     * @param utils
     * @param pattern
     * @param usedIdentifiers
     * @param NOT_BOUND_INDEX
     * @param factDeclarations
     */
    private void createImplicitBindings( final RuleBuildContext context,
                                         final Pattern pattern,
                                         final Set<String> unboundIdentifiers,
                                         final List factDeclarations ) {
        for ( String identifier : unboundIdentifiers ) {
            Declaration declaration = createDeclarationObject( context,
                                                               identifier,
                                                               pattern );

            if ( declaration != null ) {
                factDeclarations.add( declaration );
            }
        }
    }

    /**
     * Creates a declaration object for the field identified by the given identifier
     * on the give pattern object
     * 
     * @param context
     * @param identifier
     * @param pattern
     * @return
     */
    private Declaration createDeclarationObject( final RuleBuildContext context,
                                                 final String identifier,
                                                 final Pattern pattern ) {
        final BindingDescr implicitBinding = new BindingDescr( identifier,
                                                               identifier );

        final Declaration declaration = new Declaration( identifier,
                                                         pattern );

        final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                     implicitBinding,
                                                                     pattern.getObjectType(),
                                                                     implicitBinding.getExpression(),
                                                                     declaration,
                                                                     false );

        if ( extractor == null ) {
            return null;
        }

        return declaration;
    }

    private Restriction buildRestriction( final RuleBuildContext context,
                                          final Pattern pattern,
                                          final InternalReadAccessor extractor,
                                          final FieldConstraintDescr fieldConstraintDescr,
                                          final RestrictionDescr restrictionDescr ) {
        Restriction restriction = null;
        if ( restrictionDescr instanceof LiteralRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (LiteralRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof QualifiedIdentifierRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (QualifiedIdentifierRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof VariableRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            extractor,
                                            fieldConstraintDescr,
                                            (VariableRestrictionDescr) restrictionDescr );
        } else if ( restrictionDescr instanceof ReturnValueRestrictionDescr ) {
            restriction = buildRestriction( context,
                                            pattern,
                                            extractor,
                                            fieldConstraintDescr,
                                            (ReturnValueRestrictionDescr) restrictionDescr );

        }

        return restriction;
    }

    private Restriction buildRestriction( final RuleBuildContext context,
                                                 final InternalReadAccessor extractor,
                                                 final FieldConstraintDescr fieldConstraintDescr,
                                                 final VariableRestrictionDescr variableRestrictionDescr ) {
        if ( variableRestrictionDescr.getIdentifier() == null || variableRestrictionDescr.getIdentifier().equals( "" ) ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          variableRestrictionDescr,
                                                          null,
                                                          "Identifier not defined for binding field '" + fieldConstraintDescr.getFieldName() + "'" ) );
            return null;
        }

        Declaration declaration = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                   variableRestrictionDescr.getIdentifier() );

        if ( declaration == null ) {
            // trying to create implicit declaration
            final Pattern thisPattern = (Pattern) context.getBuildStack().peek();
            final Declaration implicit = this.createDeclarationObject( context,
                                                                       variableRestrictionDescr.getIdentifier(),
                                                                       thisPattern );
            if ( implicit != null ) {
                declaration = implicit;
            } else {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              variableRestrictionDescr,
                                                              null,
                                                              "Unable to return Declaration for identifier '" + variableRestrictionDescr.getIdentifier() + "'" ) );
                return null;
            }
        }

        Target right = getRightTarget( extractor );
        Target left = (declaration.isPatternDeclaration() && !(Date.class.isAssignableFrom( declaration.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( declaration.getExtractor().getExtractToClass() ))) ? Target.HANDLE : Target.FACT;
        final Evaluator evaluator = getEvaluator( context,
                                                  variableRestrictionDescr,
                                                  extractor.getValueType(),
                                                  variableRestrictionDescr.getEvaluator(),
                                                  variableRestrictionDescr.isNegated(),
                                                  variableRestrictionDescr.getParameterText(),
                                                  left,
                                                  right );
        if ( evaluator == null ) {
            return null;
        }

        Restriction restriction = new VariableRestriction( extractor,
                                                           declaration,
                                                           evaluator );

        if ( declaration.getPattern().getObjectType().equals( new ClassObjectType( DroolsQuery.class ) ) ) {
            // declaration is query argument, so allow for unification.
            restriction = new UnificationRestriction( (VariableRestriction) restriction );
        }

        return restriction;
    }

    private LiteralRestriction buildRestriction( final RuleBuildContext context,
                                                 final InternalReadAccessor extractor,
                                                 final FieldConstraintDescr fieldConstraintDescr,
                                                 final LiteralRestrictionDescr literalRestrictionDescr ) {
        FieldValue field = null;
        try {
            Object value = literalRestrictionDescr.getValue();
            if ( literalRestrictionDescr.getType() == LiteralRestrictionDescr.TYPE_STRING && context.getConfiguration().isProcessStringEscapes() ) {
                value = StringUtils.unescapeJava( (String) value );
            }

            field = FieldFactory.getFieldValue( value,
                                                extractor.getValueType(),
                                                context.getPackageBuilder().getDateFormats() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          literalRestrictionDescr,
                                                          e,
                                                          "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + literalRestrictionDescr.getText() + "'" ) );
        }

        if ( field == null ) {
            return null;
        }

        Target right = getRightTarget( extractor );
        Target left = Target.FACT;
        final Evaluator evaluator = getEvaluator( context,
                                                  literalRestrictionDescr,
                                                  extractor.getValueType(),
                                                  literalRestrictionDescr.getEvaluator(),
                                                  literalRestrictionDescr.isNegated(),
                                                  literalRestrictionDescr.getParameterText(),
                                                  left,
                                                  right );
        if ( evaluator == null ) {
            return null;
        }

        return new LiteralRestriction( field,
                                       evaluator,
                                       extractor );
    }

    private Restriction buildRestriction( final RuleBuildContext context,
                                          final InternalReadAccessor extractor,
                                          final FieldConstraintDescr fieldConstraintDescr,
                                          final QualifiedIdentifierRestrictionDescr qiRestrictionDescr ) {
        FieldValue field = null;
        String t = qiRestrictionDescr.getText();
        final String[] parts = t.split( "\\." );

        Declaration implicit = null;
        if ( "this".equals( parts[0] ) ) {
            implicit = this.createDeclarationObject( context,
                                                     parts[1],
                                                     (Pattern) context.getBuildStack().peek() );
        }
        if ( implicit == null ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                      parts[0] );
            // if a declaration exists, then it may be a variable direct property access, not an enum
            if ( decl != null ) {
                if ( decl.isPatternDeclaration() ) {
                    implicit = this.createDeclarationObject( context,
                                                             t.substring( t.indexOf( '.' ) + 1 ),
                                                             decl.getPattern() );

                } else {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  qiRestrictionDescr,
                                                                  "",
                                                                  "Not possible to directly access the property '" + parts[1] + "' of declaration '" + parts[0] + "' since it is not a pattern" ) );
                    return null;
                }
            }
        }

        if ( implicit != null ) {
            Target right = getRightTarget( extractor );
            Target left = (implicit.isPatternDeclaration() && !(Date.class.isAssignableFrom( implicit.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( implicit.getExtractor().getExtractToClass() ))) ? Target.HANDLE : Target.FACT;
            final Evaluator evaluator = getEvaluator( context,
                                                      qiRestrictionDescr,
                                                      extractor.getValueType(),
                                                      qiRestrictionDescr.getEvaluator(),
                                                      qiRestrictionDescr.isNegated(),
                                                      qiRestrictionDescr.getParameterText(),
                                                      left,
                                                      right );
            if ( evaluator == null ) {
                return null;
            }

            return new VariableRestriction( extractor,
                                            implicit,
                                            evaluator );
        }

        //        // if only 2 parts, it may be a composed direct property access
        //        if ( parts.length == 2 ) {
        //            Declaration implicit = null;
        //            if ( "this".equals( parts[0] ) ) {
        //                implicit = this.createDeclarationObject( context,
        //                                                         parts[1],
        //                                                         (Pattern) context.getBuildStack().peek() );
        //            } else {
        //                final Declaration decl = context.getDeclarationResolver().getDeclaration( context.getRule(),
        //                                                                                          parts[0] );
        //                // if a declaration exists, then it may be a variable direct property access, not an enum
        //                if ( decl != null ) {
        //                    if ( decl.isPatternDeclaration() ) {
        //                        implicit = this.createDeclarationObject( context,
        //                                                                 parts[1],
        //                                                                 decl.getPattern() );
        //
        //                    } else {
        //                        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
        //                                                                      qiRestrictionDescr,
        //                                                                      "",
        //                                                                      "Not possible to directly access the property '" + parts[1] + "' of declaration '" + parts[0] + "' since it is not a pattern" ) );
        //                        return null;
        //                    }
        //                }
        //            }
        //
        //            if ( implicit != null ) {
        //                Target right = getRightTarget( extractor );
        //                Target left = (implicit.isPatternDeclaration() && !(Date.class.isAssignableFrom( implicit.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( implicit.getExtractor().getExtractToClass() ))) ? Target.HANDLE : Target.FACT;
        //                final Evaluator evaluator = getEvaluator( context,
        //                                                          qiRestrictionDescr,
        //                                                          extractor.getValueType(),
        //                                                          qiRestrictionDescr.getEvaluator(),
        //                                                          qiRestrictionDescr.isNegated(),
        //                                                          qiRestrictionDescr.getParameterText(),
        //                                                          left,
        //                                                          right );
        //                if ( evaluator == null ) {
        //                    return null;
        //                }
        //
        //                return new VariableRestriction( extractor,
        //                                                implicit,
        //                                                evaluator );
        //            }
        //        }

        final int lastDot = qiRestrictionDescr.getText().lastIndexOf( '.' );
        final String className = qiRestrictionDescr.getText().substring( 0,
                                                                         lastDot );
        final String fieldName = qiRestrictionDescr.getText().substring( lastDot + 1 );
        try {
            final Class staticClass = context.getDialect().getTypeResolver().resolveType( className );
            field = FieldFactory.getFieldValue( staticClass.getField( fieldName ).get( null ),
                                                extractor.getValueType(),
                                                context.getPackageBuilder().getDateFormats() );
            if ( field.isObjectField() ) {
                ((ObjectFieldImpl) field).setEnum( true );
                ((ObjectFieldImpl) field).setEnumName( staticClass.getName() );
                ((ObjectFieldImpl) field).setFieldName( fieldName );
            }
        } catch ( final ClassNotFoundException e ) {
            // nothing to do, as it is not a class name with static field
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          qiRestrictionDescr,
                                                          e,
                                                          "Unable to create a Field value of type  '" + extractor.getValueType() + "' and value '" + qiRestrictionDescr.getText() + "'" ) );
        }

        Target right = getRightTarget( extractor );
        Target left = Target.FACT;
        final Evaluator evaluator = getEvaluator( context,
                                                  qiRestrictionDescr,
                                                  extractor.getValueType(),
                                                  qiRestrictionDescr.getEvaluator(),
                                                  qiRestrictionDescr.isNegated(),
                                                  qiRestrictionDescr.getParameterText(),
                                                  left,
                                                  right );
        if ( evaluator == null ) {
            return null;
        }

        return new LiteralRestriction( field,
                                       evaluator,
                                       extractor );
    }

    private Target getRightTarget( final InternalReadAccessor extractor ) {
        Target right = (extractor.isSelfReference() && !(Date.class.isAssignableFrom( extractor.getExtractToClass() ) || Number.class.isAssignableFrom( extractor.getExtractToClass() ))) ? Target.HANDLE : Target.FACT;
        return right;
    }

    private ReturnValueRestriction buildRestriction( final RuleBuildContext context,
                                                     final Pattern pattern,
                                                     final InternalReadAccessor extractor,
                                                     final FieldConstraintDescr fieldConstraintDescr,
                                                     final ReturnValueRestrictionDescr returnValueRestrictionDescr ) {
        Map<String, Class< ? >> declarations = getDeclarationsMap( returnValueRestrictionDescr,
                                                                   context );
        Class< ? > thisClass = null;
        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
        }

        Map<String, Class< ? >> globals = context.getPackageBuilder().getGlobals();
        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                          returnValueRestrictionDescr,
                                                                          returnValueRestrictionDescr.getContent(),
                                                                          new BoundIdentifiers( declarations,
                                                                                                globals,
                                                                                                thisClass ) );
        if ( analysis == null ) {
            // something bad happened
            return null;
        }
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( String id : usedIdentifiers.getDeclarations().keySet() ) {
            final Declaration decl = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                      id );
            if ( decl.getPattern() == pattern ) {
                factDeclarations.add( decl );
            } else {
                tupleDeclarations.add( decl );
            }
        }
        this.createImplicitBindings( context,
                                     pattern,
                                     analysis.getNotBoundedIdentifiers(),
                                     factDeclarations );

        Target right = getRightTarget( extractor );
        Target left = Target.FACT;
        final Evaluator evaluator = getEvaluator( context,
                                                  returnValueRestrictionDescr,
                                                  extractor.getValueType(),
                                                  returnValueRestrictionDescr.getEvaluator(),
                                                  returnValueRestrictionDescr.isNegated(),
                                                  returnValueRestrictionDescr.getParameterText(),
                                                  left,
                                                  right );
        if ( evaluator == null ) {
            return null;
        }

        final Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );

        Arrays.sort( previousDeclarations,
                     SortDeclarations.instance );
        Arrays.sort( localDeclarations,
                     SortDeclarations.instance );

        final String[] requiredGlobals = usedIdentifiers.getGlobals().keySet().toArray( new String[usedIdentifiers.getGlobals().size()] );
        final ReturnValueRestriction returnValueRestriction = new ReturnValueRestriction( extractor,
                                                                                          previousDeclarations,
                                                                                          localDeclarations,
                                                                                          requiredGlobals,
                                                                                          evaluator );

        final ReturnValueBuilder builder = context.getDialect().getReturnValueBuilder();

        builder.build( context,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       returnValueRestriction,
                       returnValueRestrictionDescr,
                       analysis );

        return returnValueRestriction;
    }

    public static void registerReadAccessor( final RuleBuildContext context,
                                             final ObjectType objectType,
                                             final String fieldName,
                                             final AcceptsReadAccessor target ) {
        if ( !ValueType.FACTTEMPLATE_TYPE.equals( objectType.getValueType() ) ) {
            InternalReadAccessor reader = context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) objectType).getClassName(),
                                                                                                   fieldName,
                                                                                                   target );
        }
    }

    public static InternalReadAccessor getFieldReadAccessor( final RuleBuildContext context,
                                                             final BaseDescr descr,
                                                             final ObjectType objectType,
                                                             final String fieldName,
                                                             final AcceptsReadAccessor target,
                                                             final boolean reportError ) {
        InternalReadAccessor reader = null;

        if ( ValueType.FACTTEMPLATE_TYPE.equals( objectType.getValueType() ) ) {
            //@todo use accessor cache            
            final FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            reader = new FactTemplateFieldExtractor( factTemplate,
                                                     factTemplate.getFieldTemplateIndex( fieldName ) );
            if ( target != null ) {
                target.setReadAccessor( reader );
            }
        } else {
            try {
                reader = context.getPkg().getClassFieldAccessorStore().getReader( ((ClassObjectType) objectType).getClassName(),
                                                                                  fieldName,
                                                                                  target );
            } catch ( final Exception e ) {
                if ( reportError ) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  descr,
                                                                  e,
                                                                  "Unable to create Field Extractor for '" + fieldName + "'" ) );
                }
            }
        }

        return reader;
    }

    private Evaluator getEvaluator( final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final ValueType valueType,
                                    final String evaluatorString,
                                    final boolean isNegated,
                                    final String parameterText,
                                    final Target left,
                                    final Target right ) {

        final EvaluatorDefinition def = context.getConfiguration().getEvaluatorRegistry().getEvaluatorDefinition( evaluatorString );
        if ( def == null ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Unable to determine the Evaluator for ID '" + evaluatorString + "'" ) );
            return null;
        }

        final Evaluator evaluator = def.getEvaluator( valueType,
                                                      evaluatorString,
                                                      isNegated,
                                                      parameterText,
                                                      left,
                                                      right );

        if ( evaluator == null ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Evaluator '" + (isNegated ? "not " : "") + evaluatorString + "' does not support type '" + valueType ) );
        }

        return evaluator;
    }

}
