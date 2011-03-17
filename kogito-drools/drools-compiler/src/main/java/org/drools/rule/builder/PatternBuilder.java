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
import java.util.Collection;
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
import org.drools.base.extractors.BaseObjectClassFieldReader;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.base.mvel.MVELReturnValueExpression;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.DrlExprParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageRegistry;
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
import org.drools.rule.TypeDeclaration;
import org.drools.rule.UnificationRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.rule.VariableRestriction;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.rule.builder.dialect.mvel.MVELReturnValueBuilder;
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
            build(context, 
                  pattern, 
                  new ExprConstraintDescr("this == " + patternDescr.getIdentifier() ));            
        }

        if ( objectType instanceof ClassObjectType ) {
            // make sure the Pattern is wired up to correct ClassObjectType and set as a target for rewiring
            context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType),
                                                                              pattern );
        }

        // adding the newly created pattern to the build stack this is necessary in case of local declaration usage
        context.getBuildStack().push( pattern );
        
        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            Class cls = ((ClassObjectType)pattern.getObjectType()).getClassType();
            TypeDeclaration typeDeclr = context.getPackageBuilder().getTypeDeclaration( cls );
            if  ( typeDeclr != null ) {
                context.setTypesafe( typeDeclr.isTypesafe() );
            }
        }

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
                    buildRuleBindings( context,
                           pattern,
                           b,
                           null ); // null containers get added to the pattern
                } else {
                    // it is both a binding and a constraint
                    b.setExpression( left );
                    buildRuleBindings( context,
                                       pattern,
                                       b,
                                       null ); // null containers get added to the pattern
                    b.setExpression( expression );

                    // needs to build the actual constraints as well
                    build(context,
                          pattern,
                          new ExprConstraintDescr( b.getExpression() ));
                }

            } else {
                buildRuleBindings( context,
                                   pattern,
                                   b,
                                   null ); // null containers get added to the pattern
            }
        }

        for ( BaseDescr b : patternDescr.getDescrs() ) {
            build(context,
                  pattern,
                 (ExprConstraintDescr) b);
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

    public void build( RuleBuildContext context,
                       Pattern pattern,
                       ExprConstraintDescr descr ) {
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
        
            boolean simple = false;  
            String expr = null;
            RelationalExprDescr relDescr = null;
            if ( d instanceof AtomicExprDescr ) {  
                expr = ((AtomicExprDescr)d).getExpression();
            } else {
                if( d instanceof RelationalExprDescr ) {
                    relDescr = (RelationalExprDescr) d;
                    if (  relDescr.getLeft() instanceof AtomicExprDescr &&
                          relDescr.getRight() instanceof AtomicExprDescr ) {
                          simple = true;
                    }
                }
                StringBuilder sbuilder = new StringBuilder();
                renderConstraint( sbuilder,
                                  d,
                                  0 ); // root, so no priority at all    
                expr = sbuilder.toString().trim();                
            }            
            
            if ( expr.startsWith( "eval" ) ) {
                // strip evals, as mvel won't understand those.
                int startParen = expr.indexOf( '(' ) + 1;
                int endParen = expr.lastIndexOf( ')' );
                expr = expr.substring( startParen,
                                       endParen );
                
                // this will build the eval using the specified dialect
                PredicateDescr pdescr = new PredicateDescr( expr );
                buildEval( context,
                       pattern,
                       pdescr,
                       null );  
                continue;                
            }                      
                        
            if ( !simple || !context.isTypesafe() ) {
                Dialect dialect = context.getDialect();
                MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                context.setDialect( mvelDialect );
                
                PredicateDescr pdescr = new PredicateDescr( expr );
                buildEval( context,
                           pattern,
                           pdescr,
                           null );
                
                // fall back to original dialect
                context.setDialect( dialect );
                continue;
            }
            
            if ( !(d instanceof RelationalExprDescr ) ) {
                throw new RuntimeException("What caused this?: " + d);
            }                        
            
            RelationalExprDescr exprDescr = (RelationalExprDescr) d;
            
            AtomicExprDescr rdescr = ((AtomicExprDescr) exprDescr.getRight());
            String fieldName = ((AtomicExprDescr) exprDescr.getLeft()).getExpression();
            String value = rdescr.getExpression().trim();
            
            ExprBindings rightExpr = new ExprBindings( );            
            setInputs( context,
                       rightExpr,
                       ((ClassObjectType) pattern.getObjectType()).getClassType(),
                       value ); 
            
            String[] parts = fieldName.split( "\\." );
            if ( parts.length == 2 ) {
                if ( "this".equals( parts[0].trim() ) ) {
                    if ( parts[1].trim().startsWith( "[" ) ) {
                        // they are trying map accessors to rewrite to eval
                        Dialect dialect = context.getDialect();
                        MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                        context.setDialect( mvelDialect );
                        
                        PredicateDescr pdescr = new PredicateDescr( expr );
                        buildEval( context,
                                   pattern,
                                   pdescr,
                                   null );
                        
                        // fall back to original dialect
                        context.setDialect( dialect );
                        continue;                       
                    } else {
                        // it's a redundant this so trim
                        fieldName = parts[1];
                    }
                } else if (  pattern.getDeclaration() != null && parts[0].trim().equals( pattern.getDeclaration().getIdentifier() ) ) {
                    // it's a redundant declaration so trim
                    fieldName = parts[1];
                }
            }
                                   
            
            if ( fieldName.indexOf( '.' ) >= 0 ||  fieldName.indexOf( '[' ) >= 0 || fieldName.indexOf( '(' ) >= 0 ) {
                // if left has any inputs then we need to rewrite to eval
                ExprBindings leftExpr = new ExprBindings(  );            
                setInputs( context,
                           leftExpr,
                           ((ClassObjectType) ((Pattern) context.getBuildStack().peek()).getObjectType()).getClassType(),
                           fieldName );   
                if ( !leftExpr.getRuleBindings().isEmpty()) {
                    Dialect dialect = context.getDialect();
                    MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                    context.setDialect( mvelDialect );
                    
                    PredicateDescr pdescr = new PredicateDescr( expr );
                    buildEval( context,
                               pattern,
                               pdescr,
                               null );
                    
                    // fall back to original dialect
                    context.setDialect( dialect );
                    continue;                  
                }
            }

            final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                         d,
                                                                         pattern.getObjectType(),
                                                                         fieldName,
                                                                         null,
                                                                         false );
            
            if ( extractor == null ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              d,
                                                              null,
                                                              "Unable to build constraint as  '" + fieldName + "' is invalid" ) );
                continue;                
            }
            
            
            String operator = relDescr.getOperator().trim();
            // extractor the operator and determine if it's negated or not
            boolean negatedOperator = operator.startsWith( "not " );
            if ( negatedOperator ) {
                operator = operator.substring( 4 );
            }
            
            Restriction restriction = null;
            // is it a literal? Does not include enums
            if ( rdescr.isLiteral() ) {
                restriction =  buildLiteralRestriction( context, extractor, new LiteralRestrictionDescr( operator, negatedOperator, value ) );
            } else {            
                // is it an enum?
                int dotPos = value.indexOf( '.' );
                if ( dotPos >= 0 ) {
                    final String className = value.substring( 0,
                                                              dotPos );
                    String enumName = value.substring( dotPos + 1 );
                    try {
                        final Class cls = context.getDialect().getTypeResolver().resolveType( className );
                        if ( enumName.indexOf( '(' ) < 0 && enumName.indexOf( '.' ) < 0 && enumName.indexOf( '[' ) < 0 ) {
                            restriction =  buildLiteralRestriction( context, extractor, new LiteralRestrictionDescr( operator, negatedOperator, value ) );
                        }
                    } catch ( ClassNotFoundException e ) {
                        // do nothing as this is just probing to see if it was a class, which we now know it isn't :)
                    }                
                }
            }
            
            if ( restriction != null ) {
                pattern.addConstraint( new LiteralConstraint( extractor,
                                       ( LiteralRestriction ) restriction ) );
                continue;
            }

            Declaration declr = null;
            if ( value.indexOf( '(' ) < 0 && value.indexOf( '.' ) < 0 && value.indexOf( '[' ) < 0 ) {
                declr = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                               value );

                if ( declr == null ) {
                    // trying to create implicit declaration
                    final Pattern thisPattern = (Pattern) context.getBuildStack().peek();
                    declr = this.createDeclarationObject( context,
                                                         value,
                                                         thisPattern );
                    if ( declr == null ) {
                        context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                      d,
                                                                      null,
                                                                      "Unable to return Declaration for identifier '" + value + "'" ) );
                        continue;
                    }
                }        
            }
            
            if ( declr == null ) {
                parts = value.split( "\\." );
                if ( parts.length == 2 ) {
                    if ( "this".equals( parts[0].trim() ) ) {
                        declr = this.createDeclarationObject( context,
                                                              parts[1].trim(),
                                                              (Pattern) context.getBuildStack().peek() );
                        value = parts[1].trim();                        
                    } else {
                        declr = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                 parts[0].trim() );
                        // if a declaration exists, then it may be a variable direct property access
                        if ( declr != null ) {
                            if ( declr.isPatternDeclaration() ) {
                                declr = this.createDeclarationObject( context,
                                                                      parts[1].trim(),
                                                                      declr.getPattern() );
                                value = parts[1].trim();
    
                            } else {
                                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                              d,
                                                                              "",
                                                                              "Not possible to directly access the property '" + parts[1] + "' of declaration '" + parts[0] + "' since it is not a pattern" ) );
                                continue;
                            }
                        }
                    }
                }
            }
            
            if ( declr != null ) {
                Target right = getRightTarget( extractor );
                Target left = (declr.isPatternDeclaration() && !(Date.class.isAssignableFrom( declr.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( declr.getExtractor().getExtractToClass() ))) ? Target.HANDLE : Target.FACT;
                final Evaluator evaluator = getEvaluator( context,
                                                          d,
                                                          extractor.getValueType(),
                                                          operator,
                                                          negatedOperator,
                                                          null,
                                                          left,
                                                          right );
                if ( evaluator == null ) {
                    continue;
                }

                restriction = new VariableRestriction( extractor,
                                                       declr,
                                                       evaluator );
                
                if ( declr.getPattern().getObjectType().equals( new ClassObjectType( DroolsQuery.class ) ) ) {
                    // declaration is query argument, so allow for unification.
                    restriction = new UnificationRestriction( (VariableRestriction) restriction );
                }                
            }

            if ( restriction == null ) {
                Dialect dialect = context.getDialect();
                if ( !value.startsWith( "(" )) {
                    // it's not a traditional return value, so override the dialect
                    MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                    context.setDialect( mvelDialect );
                }
                
                // execute it as a return value
                restriction = buildRestriction( context,
                                                (Pattern) context.getBuildStack().peek(),
                                                extractor,
                                                new ReturnValueRestrictionDescr( operator,
                                                                                 negatedOperator,
                                                                                 null,
                                                                                 value ) );   
                // fall back to original dialect
                context.setDialect( dialect );               
             
            }
            
            if( restriction == null || extractor == null ) {
                // something failed and an error should already have been reported
                return;
            }
            pattern.addConstraint( new VariableConstraint( extractor, restriction ) );
        }
    }

    private String builtInOperators = "> >= < <= == != && ||";

    private void renderConstraint( StringBuilder sbuilder,
                                   BaseDescr d,
                                   int parentPriority ) {
        if ( d instanceof RelationalExprDescr ) {
            RelationalExprDescr red = (RelationalExprDescr) d;
            if ( builtInOperators.contains( ((RelationalExprDescr) d).getOperator() ) ) {
                renderConstraint( sbuilder,
                                  ((RelationalExprDescr) d).getLeft(),
                                  Integer.MAX_VALUE ); // maximum priority, so wrap any child connective in parenthesis
                sbuilder.append( " " );
                sbuilder.append( ((RelationalExprDescr) d).getOperator() );
                sbuilder.append( " " );
                renderConstraint( sbuilder,
                                  ((RelationalExprDescr) d).getRight(),
                                  Integer.MAX_VALUE ); // maximum priority, so wrap any child connective in parenthesis
            } else {
                MVELDumper dumper = new MVELDumper( null );
                dumper.setFieldName( ((AtomicExprDescr) ((RelationalExprDescr) d).getLeft()).getExpression() );
                String operator = red.getOperator();

                // extractor the operator and determine if it's negated or not
                boolean negated = operator.startsWith( "not " );
                if ( negated ) {
                    operator = red.getOperator().substring( 4 );
                }

                // as there is no && or || operator we know this is atomic
                String s = dumper.processRestriction( operator,
                                                      negated,
                                                      ((AtomicExprDescr) ((RelationalExprDescr) d).getRight()).getExpression() );
                sbuilder.append( s );
            }

        } else if ( d instanceof AtomicExprDescr ) {
            sbuilder.append( ((AtomicExprDescr) d).getExpression() );
        } else if ( d instanceof ConstraintConnectiveDescr ) {
            ConstraintConnectiveDescr cc = (ConstraintConnectiveDescr) d; 
            boolean afterFirst = false;
            boolean wrapParenthesis = parentPriority > cc.getConnective().getPrecedence(); 
            if( wrapParenthesis ) {
                sbuilder.append( "( " );
            }
            for ( BaseDescr c : cc.getDescrs() ) {
                if ( afterFirst ) {
                    sbuilder.append( " " );
                    sbuilder.append( cc.getConnective().toString() );
                    sbuilder.append( " " );
                } else {
                    afterFirst = true;
                }
                renderConstraint( sbuilder,
                                  c,
                                  cc.getConnective().getPrecedence() );
            }
            if( wrapParenthesis ) {
                sbuilder.append( " )" );
            }
        }
    }

    private void setInputs( RuleBuildContext context,
                            ExprBindings descrBranch,
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

    public static class ExprBindings {
        private Set<String> globalBindings;
        private Set<String> ruleBindings;

        public ExprBindings() {
            this.globalBindings = new HashSet<String>();
            this.ruleBindings = new HashSet<String>();
        }
        
        public Set<String> getGlobalBindings() {
            return globalBindings;
        }

        public Set<String> getRuleBindings() {
            return ruleBindings;
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



    private void buildRuleBindings( final RuleBuildContext context,
                                    final Pattern pattern,
                                    final BindingDescr fieldBindingDescr,
                                    final AbstractCompositeConstraint container ) {

        if ( context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                            fieldBindingDescr.getVariable() ) ) {
            // rewrite existing bindings into == constraints, so it unifies
            build(context, 
                  pattern, 
                  new ExprConstraintDescr(fieldBindingDescr.getExpression() + " == " + fieldBindingDescr.getVariable() ));
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
    private void buildEval( final RuleBuildContext context,
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
        return createDeclarationObject(context, identifier, identifier, pattern );
        
    }
    

    private Declaration createDeclarationObject( final RuleBuildContext context,
                                                 final String identifier,
                                                 final String expr,
                                                 final Pattern pattern ) {
        final BindingDescr implicitBinding = new BindingDescr( identifier,
                                                               expr );

        final Declaration declaration = new Declaration( identifier, null, pattern, true );
        
        InternalReadAccessor extractor = null;
        if ( expr.indexOf( '.' ) >= 0 ||  expr.indexOf( '[' ) >= 0 || expr.indexOf( '(' ) >= 0 ) {
            throw new RuntimeException("This shouldn't execute at the moment, it's stub code ready for mvel expr");
            //new MVELClassFieldR
        } else {
            extractor = getFieldReadAccessor( context,
                                              implicitBinding,
                                              pattern.getObjectType(),
                                              implicitBinding.getExpression(),
                                              declaration,
                                              false );            
        }

        if ( extractor == null ) {
            return null;
        }

        declaration.setReadAccessor( extractor );
        
        return declaration;
    }   

    private LiteralRestriction buildLiteralRestriction( final RuleBuildContext context,
                                                        final InternalReadAccessor extractor,
                                                        final LiteralRestrictionDescr literalRestrictionDescr ) {
        FieldValue field = null;
        try {
            String value = literalRestrictionDescr.getText().trim();

            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            ParserConfiguration pconf = new ParserConfiguration();
            pconf.setImports( dialect.getImports() );
            pconf.setPackageImports( (HashSet) dialect.getPackgeImports() );
            ParserContext pctx = new ParserContext(pconf);
            
            
            field = FieldFactory.getFieldValue( MVEL.executeExpression( MVEL.compileExpression( value, pctx ) ),
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


    private Target getRightTarget( final InternalReadAccessor extractor ) {
        Target right = (extractor.isSelfReference() && !(Date.class.isAssignableFrom( extractor.getExtractToClass() ) || Number.class.isAssignableFrom( extractor.getExtractToClass() ))) ? Target.HANDLE : Target.FACT;
        return right;
    }

    private ReturnValueRestriction buildRestriction( final RuleBuildContext context,
                                                     final Pattern pattern,
                                                     final InternalReadAccessor extractor,
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
