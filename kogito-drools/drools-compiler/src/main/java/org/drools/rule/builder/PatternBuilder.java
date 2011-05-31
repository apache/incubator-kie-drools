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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.drools.base.*;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.EvaluatorDefinition.Target;
import org.drools.base.extractors.MVELClassFieldReader;
import org.drools.base.mvel.MVELCompileable;
import org.drools.compiler.*;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.factmodel.FieldDefinition;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateFieldExtractor;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.lang.DRLLexer;
import org.drools.lang.MVELDumper;
import org.drools.lang.descr.*;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.*;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.*;
import org.drools.spi.Constraint.ConstraintType;
import org.drools.spi.Restriction;
import org.drools.time.TimeUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.util.PropertyTools;

import java.util.*;
import java.util.regex.Matcher;

/**
 * A builder for patterns
 */
public class PatternBuilder
    implements
    RuleConditionBuilder {

    private static final java.util.regex.Pattern evalRegexp = java.util.regex.Pattern.compile( "^eval\\s*\\(",
                                                                                               java.util.regex.Pattern.MULTILINE );

    public PatternBuilder() {
    }

    public RuleConditionElement build( RuleBuildContext context,
                                       BaseDescr descr ) {
        boolean typeSafe = context.isTypesafe();
        try {
            return this.build( context,
                               descr,
                               null );
        } finally {
            context.setTypesafe( typeSafe );
        }
    }

    /**
     * Build a pattern for the given descriptor in the current
     * context and using the given utils object
     *
     * @param context
     * @param descr
     * @param prefixPattern
     * @return
     */
    @SuppressWarnings("unchecked")
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
                final Class< ? > userProvidedClass = context.getDialect().getTypeResolver().resolveType( patternDescr.getObjectType() );
                PackageRegistry pkgr = context.getPackageBuilder().getPackageRegistry( ClassUtils.getPackage( userProvidedClass ) );
                org.drools.rule.Package pkg = pkgr == null ? context.getPkg() : pkgr.getPackage();
                final boolean isEvent = pkg.isEvent( userProvidedClass );
                objectType = new ClassObjectType( userProvidedClass,
                                                  isEvent );
            } catch ( final ClassNotFoundException e ) {
                // swallow as we'll do another check in a moment and then record the problem
            }
        }

        // lets see if it maps to a query
        if ( objectType == null ) {
            RuleConditionElement rce = null;
            // it might be a recursive query, so check for same names
            if ( context.getRule().getName().equals( patternDescr.getObjectType() ) ) {
                // it's a query so delegate to the QueryElementBuilder
                QueryElementBuilder qeBuilder = new QueryElementBuilder();
                rce = qeBuilder.build( context,
                                        descr,
                                        prefixPattern );
            }

            if ( rce == null ) {
                Rule rule = context.getPkg().getRule( patternDescr.getObjectType() );
                if ( rule != null && rule instanceof Query ) {
                    // it's a query so delegate to the QueryElementBuilder
                    QueryElementBuilder qeBuilder = new QueryElementBuilder();
                    rce = qeBuilder.build( context,
                                           descr,
                                           prefixPattern );
                } else {
                    // this isn't a query either, so log an error
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  patternDescr,
                                                                  null,
                                                                  "Unable to resolve ObjectType '" + patternDescr.getObjectType() + "'" ) );
                }
            }
            return rce;
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
            if ( patternDescr.isUnification() ) {
                // rewrite existing bindings into == constraints, so it unifies
                build( context,
                       patternDescr,
                       pattern,
                       "this == " + patternDescr.getIdentifier() );
            } else {
                // This declaration already exists, so throw an Exception
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              patternDescr,
                                                              null,
                                                              "Duplicate declaration for variable '" + patternDescr.getIdentifier() + "' in the rule '" + context.getRule().getName() + "'" ) );
            }
        }

        if ( objectType instanceof ClassObjectType ) {
            // make sure the Pattern is wired up to correct ClassObjectType and set as a target for rewiring
            context.getPkg().getClassFieldAccessorStore().getClassObjectType( ((ClassObjectType) objectType),
                                                                              pattern );
        }

        // adding the newly created pattern to the build stack this is necessary in case of local declaration usage
        context.getBuildStack().push( pattern );

        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            Class< ? > cls = ((ClassObjectType) pattern.getObjectType()).getClassType();
            TypeDeclaration typeDeclr = context.getPackageBuilder().getTypeDeclaration( cls );
            if ( typeDeclr != null ) {
                context.setTypesafe( typeDeclr.isTypesafe() );
            } else {
                context.setTypesafe( true );
            }
        }

        List<BaseDescr> notAConstraints = processPositional( new ArrayList<BaseDescr>( patternDescr.getDescrs() ), // needs to be cloned
                                                             context,
                                                             patternDescr,
                                                             pattern );
        for ( BaseDescr nac : notAConstraints ) {
            patternDescr.removeConstraint( nac );
        }

        // Process all constraints
        processConstraintsAndBinds( context,
                                    patternDescr,
                                    pattern );

        if ( patternDescr.getSource() != null ) {
            // we have a pattern source, so build it
            RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( patternDescr.getSource().getClass() );

            PatternSource source = (PatternSource) builder.build( context,
                                                                  patternDescr.getSource() );

            pattern.setSource( source );
        }

        for ( BehaviorDescr behaviorDescr : patternDescr.getBehaviors() ) {
            if ( pattern.getObjectType().isEvent() ) {
                if ( Behavior.BehaviorType.TIME_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                    SlidingTimeWindow window = new SlidingTimeWindow( TimeUtils.parseTimeString( behaviorDescr.getParameters().get( 0 ) ) );
                    pattern.addBehavior( window );
                } else if ( Behavior.BehaviorType.LENGTH_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                    SlidingLengthWindow window = new SlidingLengthWindow( Integer.valueOf( behaviorDescr.getParameters().get( 0 ) ) );
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

    /**
     * Process all constraints and bindings on this pattern
     * 
     * @param context
     * @param patternDescr
     * @param pattern
     */
    private void processConstraintsAndBinds( final RuleBuildContext context,
                                             final PatternDescr patternDescr,
                                             final Pattern pattern ) {

        for ( BaseDescr b : patternDescr.getDescrs() ) {
            String expression = null;
            if ( b instanceof BindingDescr ) {
                BindingDescr bind = (BindingDescr) b;
                expression = bind.getVariable() + (bind.isUnification() ? " := " : " : ") + bind.getExpression();
            } else if ( b instanceof ExprConstraintDescr ) {
                expression = ((ExprConstraintDescr) b).getExpression();
            } else {
                expression = b.getText();
            }

            ConstraintConnectiveDescr result = parseExpression( context,
                                                                patternDescr,
                                                                expression );
            if ( result == null ) {
                return;
            }

            if ( result.getDescrs().size() == 1 && result.getDescrs().get( 0 ) instanceof BindingDescr ) {
                // it is just a bind, so build it
                buildRuleBindings( context,
                                   patternDescr,
                                   pattern,
                                   (BindingDescr) result.getDescrs().get( 0 ),
                                   null ); // null containers get added to the pattern
            } else {
                // need to build the actual constraints as well
                build( context,
                       patternDescr,
                       pattern,
                       result );

            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<BaseDescr> processPositional( List< ? extends BaseDescr> descrs,
                                               RuleBuildContext context,
                                               PatternDescr patternDescr,
                                               Pattern pattern ) {
        List<BaseDescr> victims = new LinkedList<BaseDescr>();
        for ( BaseDescr baseDescr : descrs ) {

            ExprConstraintDescr descr = (ExprConstraintDescr) baseDescr;

            if ( descr.getType() == ExprConstraintDescr.Type.POSITIONAL && pattern.getObjectType() instanceof ClassObjectType ) {
                Class< ? > klazz = ((ClassObjectType) pattern.getObjectType()).getClassType();
                TypeDeclaration tDecl = context.getPackageBuilder().getTypeDeclaration( klazz );

                if ( tDecl == null ) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  descr,
                                                                  klazz,
                                                                  "Unable to find @positional definitions for :" + klazz + "\n" ) );
                    continue;
                }

                FieldDefinition field = tDecl.getTypeClassDef().getField( descr.getPosition() );
                if ( field == null ) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  descr,
                                                                  null,
                                                                  "Unable to find @positional field " + descr.getPosition() ) + "\n" );
                    continue;
                }

                // TODO: WTH is this??????
                DRLLexer lex = new DRLLexer( new ANTLRStringStream( descr.getExpression() ) );
                boolean isSimpleIdentifier = false;
                try {
                    lex.mID();
                    isSimpleIdentifier = lex.getCharIndex() >= descr.getExpression().length();
                } catch ( RecognitionException e ) {

                }

                if ( isSimpleIdentifier ) {
                    BindingDescr binder = new BindingDescr();
                    binder.setUnification( true );
                    binder.setExpression( field.getName() );
                    binder.setVariable( descr.getExpression() );
                    patternDescr.addConstraint( binder );
                    victims.add( descr );
                    continue;
                } else {
                    descr.setText( field.getName() + " == " + descr.getExpression() );
                    descr.setType( ExprConstraintDescr.Type.NAMED );
                }
            }
        }
        return victims;
    }

    private void build( final RuleBuildContext context,
                        final PatternDescr patternDescr,
                        final Pattern pattern,
                        final String expr ) {
        ConstraintConnectiveDescr result = parseExpression( context,
                                                            patternDescr,
                                                            expr );
        if ( result == null ) {
            return;
        }
        build( context,
               patternDescr,
               pattern,
               result );
    }

    private void build( RuleBuildContext context,
                        PatternDescr patternDescr,
                        Pattern pattern,
                        ConstraintConnectiveDescr descr ) {
        for ( BaseDescr d : descr.getDescrs() ) {

            boolean simple = false;
            MVELDumper.MVELDumperContext mvelCtx = new MVELDumper.MVELDumperContext();
            String expr = new MVELDumper().dump( d,
                                                 mvelCtx );
            Map<String, OperatorDescr> aliases = mvelCtx.getAliases();
            
            for( BindingDescr bind : mvelCtx.getBindings() ) {
                buildRuleBindings( context,
                                   patternDescr,
                                   pattern,
                                   bind,
                                   null ); // null containers get added to the pattern
            }

            // process atomic expressions
            if ( d instanceof AtomicExprDescr ) {
                Matcher m = evalRegexp.matcher( ((AtomicExprDescr) d).getExpression() );
                if ( m.find() ) {
                    // MVELDumper already stripped the eval
                    // this will build the eval using the specified dialect
                    PredicateDescr pdescr = new PredicateDescr( expr );
                    buildEval( context,
                               pattern,
                               pdescr,
                               null,
                               aliases );
                    continue;
                }
            }

            // check if it is a simple expression or not
            RelationalExprDescr relDescr = null;
            if ( d instanceof RelationalExprDescr ) {
                relDescr = (RelationalExprDescr) d;
                if ( relDescr.getLeft() instanceof AtomicExprDescr &&
                     relDescr.getRight() instanceof AtomicExprDescr ) {
                    simple = true;
                }
            }

            // Either it's a complex expression, so do as predicate
            // Or it's a Map and we have to treat it as a special case
            if ( !simple || new ClassObjectType( Map.class ).isAssignableFrom( pattern.getObjectType() ) ) {
                Dialect dialect = context.getDialect();
                MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                context.setDialect( mvelDialect );

                PredicateDescr pdescr = new PredicateDescr( expr );
                buildEval( context,
                           pattern,
                           pdescr,
                           null,
                           aliases );

                // fall back to original dialect
                context.setDialect( dialect );
                continue;
            }

            if ( !(d instanceof RelationalExprDescr) ) {
                throw new RuntimeException( "What caused this?: " + d );
            }

            RelationalExprDescr exprDescr = (RelationalExprDescr) d;

            AtomicExprDescr rdescr = ((AtomicExprDescr) exprDescr.getRight());
            String fieldName = ((AtomicExprDescr) exprDescr.getLeft()).getExpression();
            String value = rdescr.getExpression().trim();

            ExprBindings rightExpr = new ExprBindings();
            setInputs( context,
                       rightExpr,
                       (pattern.getObjectType() instanceof ClassObjectType) ? ((ClassObjectType) pattern.getObjectType()).getClassType() : FactTemplate.class,
                       value );

            String[] parts = fieldName.split( "\\." );
            if ( parts.length == 2 ) {
                if ( "this".equals( parts[0].trim() ) ) {
                    // it's a redundant this so trim
                    fieldName = parts[1];
                } else if ( pattern.getDeclaration() != null && parts[0].trim().equals( pattern.getDeclaration().getIdentifier() ) ) {
                    // it's a redundant declaration so trim
                    fieldName = parts[1];
                }
            }

            if ( fieldName.indexOf( '.' ) >= 0 || fieldName.indexOf( '[' ) >= 0 || fieldName.indexOf( '(' ) >= 0 ) {
                // if left has any inputs then we need to rewrite to eval
                ExprBindings leftExpr = new ExprBindings();
                setInputs( context,
                           leftExpr,
                           ((ClassObjectType) ((Pattern) context.getBuildStack().peek()).getObjectType()).getClassType(),
                           fieldName );
                if ( !leftExpr.getRuleBindings().isEmpty() ) {
                    Dialect dialect = context.getDialect();
                    MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                    context.setDialect( mvelDialect );

                    PredicateDescr pdescr = new PredicateDescr( expr );
                    buildEval( context,
                               pattern,
                               pdescr,
                               null,
                               aliases );

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

            Restriction restriction = null;
            // is it a literal? Does not include enums
            if ( rdescr.isLiteral() ) {
                restriction = buildLiteralRestriction( context,
                                                       extractor,
                                                       new LiteralRestrictionDescr( operator,
                                                                                    relDescr.isNegated(),
                                                                                    relDescr.getParameters(),
                                                                                    value,
                                                                                    LiteralRestrictionDescr.TYPE_STRING ) ); // default type
                if ( restriction == null ) {
                    // otherwise we just get wierd errors after this point on literals
                    continue;
                }
            } else {
                // is it an enum?
                int dotPos = value.lastIndexOf( '.' );
                if ( dotPos >= 0 ) {
                    final String mainPart = value.substring( 0,
                                                             dotPos );
                    String lastPart = value.substring( dotPos + 1 );
                    try {
                        final Class< ? > cls = context.getDialect().getTypeResolver().resolveType( mainPart );
                        if ( lastPart.indexOf( '(' ) < 0 && lastPart.indexOf( '.' ) < 0 && lastPart.indexOf( '[' ) < 0 ) {
                            restriction = buildLiteralRestriction( context,
                                                                   extractor,
                                                                   new LiteralRestrictionDescr( operator,
                                                                                                relDescr.isNegated(),
                                                                                                relDescr.getParameters(),
                                                                                                value,
                                                                                                LiteralRestrictionDescr.TYPE_STRING ) ); // default type
                        }
                    } catch ( ClassNotFoundException e ) {
                        // do nothing as this is just probing to see if it was a class, which we now know it isn't :)
                    }
                }
            }

            if ( restriction != null ) {
                pattern.addConstraint( new LiteralConstraint( extractor,
                                                              (LiteralRestriction) restriction ) );
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
                                                          relDescr.isNegated(),
                                                          relDescr.getParametersText(),
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
                if ( !value.startsWith( "(" ) ) {
                    // it's not a traditional return value, so override the dialect
                    MVELDialect mvelDialect = (MVELDialect) context.getDialect( "mvel" );
                    context.setDialect( mvelDialect );
                }

                // execute it as a return value
                restriction = buildRestriction( context,
                                                (Pattern) context.getBuildStack().peek(),
                                                extractor,
                                                new ReturnValueRestrictionDescr( operator,
                                                                                 relDescr.isNegated(),
                                                                                 relDescr.getParametersText(),
                                                                                 value ),
                                                aliases );
                // fall back to original dialect
                context.setDialect( dialect );

            }

            if ( restriction == null || extractor == null ) {
                // something failed and an error should already have been reported
                return;
            }
            pattern.addConstraint( new VariableConstraint( extractor,
                                                           restriction ) );
        }
    }

    private void setInputs( RuleBuildContext context,
                            ExprBindings descrBranch,
                            Class thisClass,
                            String expr ) {
        MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

        MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
        ParserConfiguration conf = data.getParserConfiguration();

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
     * @param container
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

    @SuppressWarnings("unchecked")
    private void buildRuleBindings( final RuleBuildContext context,
                                    final PatternDescr patternDescr,
                                    final Pattern pattern,
                                    final BindingDescr fieldBindingDescr,
                                    final AbstractCompositeConstraint container ) {

        if ( context.getDeclarationResolver().isDuplicated( context.getRule(),
                                                            fieldBindingDescr.getVariable() ) ) {
            if ( fieldBindingDescr.isUnification() ) {
                // rewrite existing bindings into == constraints, so it unifies
                build( context,
                       patternDescr,
                       pattern,
                       fieldBindingDescr.getExpression() + " == " + fieldBindingDescr.getVariable() );
                return;
            } else {
                // This declaration already exists, so throw an Exception
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              fieldBindingDescr,
                                                              null,
                                                              "Duplicate declaration for variable '" + fieldBindingDescr.getVariable() + "' in the rule '" + context.getRule().getName() + "'" ) );
            }
        }

        Declaration declr = pattern.addDeclaration( fieldBindingDescr.getVariable() );
        context.addDeclaration( declr );

        final InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                                     fieldBindingDescr,
                                                                     pattern.getObjectType(),
                                                                     fieldBindingDescr.getExpression(),
                                                                     declr,
                                                                     true );
        declr.setReadAccessor( extractor );

    }

    @SuppressWarnings("unchecked")
    private void buildEval( final RuleBuildContext context,
                            final Pattern pattern,
                            final PredicateDescr predicateDescr,
                            final AbstractCompositeConstraint container,
                            final Map<String, OperatorDescr> aliases ) {

        Map<String, Class< ? >> declarations = getDeclarationsMap( predicateDescr,
                                                                   context );
        Map<String, Class< ? >> globals = context.getPackageBuilder().getGlobals();
        Map<String, EvaluatorWrapper> operators = new HashMap<String, EvaluatorWrapper>();

        for ( Map.Entry<String, OperatorDescr> entry : aliases.entrySet() ) {
            OperatorDescr op = entry.getValue();
            String leftStr = op.getLeftString();
            String rightStr = op.getRightString();

            Declaration leftDecl = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                    leftStr );
            if ( leftDecl == null && "this".equals( leftStr ) ) {
                leftDecl = this.createDeclarationObject( context,
                                                         "this",
                                                         pattern );

            }
            Declaration rightDecl = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                                           rightStr );
            if ( rightDecl == null && "this".equals( rightStr ) ) {
                rightDecl = this.createDeclarationObject( context,
                                                          "this",
                                                          pattern );

            }

            Target left = leftDecl != null && leftDecl.isPatternDeclaration() ? Target.HANDLE : Target.FACT;
            Target right = rightDecl != null && rightDecl.isPatternDeclaration() ? Target.HANDLE : Target.FACT;

            op.setLeftIsHandle( left == Target.HANDLE );
            op.setRightIsHandle( right == Target.HANDLE );

            Evaluator evaluator = getEvaluator( context,
                                                predicateDescr,
                                                ValueType.OBJECT_TYPE,
                                                op.getOperator(),
                                                false, // the rewrite takes care of negation
                                                op.getParametersText(),
                                                left,
                                                right );
            EvaluatorWrapper wrapper = new EvaluatorWrapper( evaluator,
                                                             left == Target.HANDLE ? leftDecl : null,
                                                             right == Target.HANDLE ? rightDecl : null );
            operators.put( entry.getKey(),
                           wrapper );
        }

        Class< ? > thisClass = null;
        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
        }

        final AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                predicateDescr,
                                                                                predicateDescr.getContent(),
                                                                                new BoundIdentifiers( declarations,
                                                                                                      globals,
                                                                                                      operators,
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
        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
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
                                     analysis.getBoundIdentifiers(),
                                     factDeclarations );

        final Declaration[] previousDeclarations = (Declaration[]) tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
        final Declaration[] localDeclarations = (Declaration[]) factDeclarations.toArray( new Declaration[factDeclarations.size()] );
        final String[] requiredGlobals = usedIdentifiers.getGlobals().keySet().toArray( new String[usedIdentifiers.getGlobals().size()] );
        final String[] requiredOperators = usedIdentifiers.getOperators().keySet().toArray( new String[usedIdentifiers.getOperators().size()] );

        Arrays.sort( previousDeclarations,
                     SortDeclarations.instance );
        Arrays.sort( localDeclarations,
                     SortDeclarations.instance );

        final PredicateConstraint predicateConstraint = new PredicateConstraint( null,
                                                                                 previousDeclarations,
                                                                                 localDeclarations,
                                                                                 requiredGlobals,
                                                                                 requiredOperators );

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
     * @param pattern
     * @param unboundIdentifiers
     * @param factDeclarations
     */
    private void createImplicitBindings( final RuleBuildContext context,
                                         final Pattern pattern,
                                         final Set<String> unboundIdentifiers,
                                         final BoundIdentifiers boundIdentifiers,
                                         final List factDeclarations ) {
        for ( String identifier : unboundIdentifiers ) {
            Declaration declaration = createDeclarationObject( context,
                                                               identifier,
                                                               pattern );
            // the name may not be a local field, such as enums
            // maybe should have a safer way to detect this, as other issues may cause null too 
            // that we would need to know about
            if ( declaration != null ) {
                factDeclarations.add( declaration );
                // implicit bindings need to be added to "local" declarations, as they are nolonger unbound
                if ( boundIdentifiers.getDeclarations() == null ) {
                    boundIdentifiers.setDeclarations( new HashMap<String, Declaration>() );
                }
                boundIdentifiers.getDeclarations().put( identifier,
                                                        declaration );
                boundIdentifiers.getDeclrClasses().put( identifier,
                                                        declaration.getExtractor().getExtractToClass() );
                unboundIdentifiers.remove( identifier );

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
        return createDeclarationObject( context,
                                        identifier,
                                        identifier,
                                        pattern );

    }

    private Declaration createDeclarationObject( final RuleBuildContext context,
                                                 final String identifier,
                                                 final String expr,
                                                 final Pattern pattern ) {
        final BindingDescr implicitBinding = new BindingDescr( identifier,
                                                               expr );

        final Declaration declaration = new Declaration( identifier,
                                                         null,
                                                         pattern,
                                                         true );

        InternalReadAccessor extractor = getFieldReadAccessor( context,
                                                               implicitBinding,
                                                               pattern.getObjectType(),
                                                               implicitBinding.getExpression(),
                                                               declaration,
                                                               false );

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
        ValueType vtype = extractor.getValueType();
        try {
            String value = literalRestrictionDescr.getText().trim();

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            ParserConfiguration pconf = data.getParserConfiguration();
            ParserContext pctx = new ParserContext( pconf );

            Object o = MVEL.executeExpression( MVEL.compileExpression( value,
                                                                       pctx ) );
            if ( o != null && vtype == null ) {
                // was a compilation problem else where, so guess valuetype so we can continue
                vtype = ValueType.determineValueType( o.getClass() );
            }

            field = FieldFactory.getFieldValue( o,
                                                vtype,
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
                                                  vtype,
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
                                                     final ReturnValueRestrictionDescr returnValueRestrictionDescr,
                                                     final Map<String, OperatorDescr> aliases ) {
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
                                                                                                null,
                                                                                                thisClass ) );
        if ( analysis == null ) {
            // something bad happened
            return null;
        }
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final List tupleDeclarations = new ArrayList();
        final List factDeclarations = new ArrayList();
        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
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
                                     usedIdentifiers,
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
        // reportError is needed as some times failure to build accessor is not a failure, just an indication that building is not possible so try something else.
        InternalReadAccessor reader = null;

        if ( ValueType.FACTTEMPLATE_TYPE.equals( objectType.getValueType() ) ) {
            //@todo use accessor cache            
            final FactTemplate factTemplate = ((FactTemplateObjectType) objectType).getFactTemplate();
            reader = new FactTemplateFieldExtractor( factTemplate,
                                                     factTemplate.getFieldTemplateIndex( fieldName ) );
            if ( target != null ) {
                target.setReadAccessor( reader );
            }
        } else if ( fieldName.indexOf( '.' ) > -1 || fieldName.indexOf( '[' ) > -1 || fieldName.indexOf( '(' ) > -1 ) {
            // we need MVEL extractor for expressions
            try {
                reader = context.getPkg().getClassFieldAccessorStore().getMVELReader( context.getPkg().getName(),
                                                                                      ((ClassObjectType) objectType).getClassName(),
                                                                                      fieldName,
                                                                                      context.isTypesafe() );
                MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
                data.addCompileable( (MVELCompileable) reader );
                ((MVELCompileable) reader).compile( data );
            } catch ( final Exception e ) {
                if ( reportError ) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  descr,
                                                                  e,
                                                                  "Unable to create reader for '" + fieldName + "':" + e.getMessage() ) );
                }
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
                                                                  "Unable to create Field Extractor for '" + fieldName + "'" + e.getMessage() ) );
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
                                    final String parameters,
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
                                                      parameters,
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

    @SuppressWarnings("unchecked")
    private ConstraintConnectiveDescr parseExpression( final RuleBuildContext context,
                                                       final PatternDescr patternDescr,
                                                       final String expression ) {
        DrlExprParser parser = new DrlExprParser();
        ConstraintConnectiveDescr result = parser.parse( expression );
        if ( result == null || parser.hasErrors() ) {
            for ( DroolsParserException error : parser.getErrors() ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              patternDescr,
                                                              null,
                                                              "Unable to parser pattern expression:\n" + error.getMessage() ) );
            }
            return null;
        }
        return result;
    }
}
