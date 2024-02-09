/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.rule.builder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.drools.base.base.CoreComponentsBuilder;
import org.drools.base.base.EnabledBoolean;
import org.drools.base.base.SalienceInteger;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.AnnotationDefinition;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.accessor.Salience;
import org.drools.base.time.TimeUtils;
import org.drools.base.time.impl.Timer;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.compiler.RuleBuildWarning;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.time.TimerExpression;
import org.drools.core.time.impl.CronExpression;
import org.drools.core.time.impl.CronTimer;
import org.drools.core.time.impl.ExpressionIntervalTimer;
import org.drools.core.time.impl.IntervalTimer;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.parser.DroolsError;
import org.drools.drl.parser.lang.DroolsSoftKeywords;
import org.drools.util.DateUtils;
import org.drools.util.StringUtils;
import org.kie.api.definition.rule.ActivationListener;
import org.kie.api.definition.rule.All;
import org.kie.api.definition.rule.Direct;
import org.kie.api.definition.rule.Propagation;

import static org.drools.compiler.rule.builder.util.AnnotationFactory.getTypedAnnotation;

/**
 * This builds the rule structure from an AST.
 * Generates semantic code where necessary if semantics are used.
 * This is an internal API.
 */
public class RuleBuilder {

    private RuleBuilder() { }

    public static void preProcess(final RuleBuildContext context) {
        RuleDescr ruleDescr = context.getRuleDescr();

        //Query and get object instead of using String
        if ( null != ruleDescr.getParentName() && null != context.getPkg().getRule( ruleDescr.getParentName() ) ) {
            context.getRule().setParent( context.getPkg().getRule( ruleDescr.getParentName() ) );
        }

        // add all the rule's meta attributes
        buildMetaAttributes( context );

        if ( context.getRuleDescr() instanceof QueryDescr ) {
            context.getDialect().getPatternBuilderForQuery(((QueryImpl) context.getRule())).build( context, (QueryDescr) context.getRuleDescr() );
        }

        context.initRule();
    }

    /**
     * Build the give rule into the
     */
    public static void build(final RuleBuildContext context) {
        RuleDescr ruleDescr = context.getRuleDescr();

        final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( ruleDescr.getLhs().getClass() );
        if ( builder != null ) {
            context.getRule().setLhs( (GroupElement) builder.build( context, ruleDescr.getLhs(), context.getPrefixPattern() ) );
        } else {
            throw new RuntimeException( "BUG: builder not found for descriptor class " + ruleDescr.getLhs().getClass() );
        }

        // build all the rule's attributes
        // must be after building LHS because some attributes require bindings from the LHS
        buildAttributes( context );

        // Build the consequence and generate it's invoker/s
        // generate the main rule from the previously generated s.
        if ( !(ruleDescr instanceof QueryDescr) ) {
            // do not build the consequence if we have a query

            ConsequenceBuilder consequenceBuilder = context.getDialect().getConsequenceBuilder();
            consequenceBuilder.build( context, RuleImpl.DEFAULT_CONSEQUENCE_NAME );
            
            for ( String name : ruleDescr.getNamedConsequences().keySet() ) {
                consequenceBuilder.build( context, name );
            }
        }
    }

    public static void buildMetaAttributes(final RuleBuildContext context ) {
        RuleImpl rule = context.getRule();
        for ( String metaAttr : context.getRuleDescr().getAnnotationNames() ) {
            AnnotationDescr ad = context.getRuleDescr().getAnnotation( metaAttr );
            String adFqn = ad.getFullyQualifiedName();
            if (adFqn != null) {
                AnnotationDefinition annotationDefinition;
                try {
                    annotationDefinition = AnnotationDefinition.build( context.getDialect().getTypeResolver().resolveType( adFqn ),
                                                                       ad.getValueMap(),
                                                                       context.getDialect().getTypeResolver() );
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException( e );
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException( e );
                }
                if ( annotationDefinition.getValues().size() == 1 && annotationDefinition.getValues().containsKey( AnnotationDescr.VALUE ) ) {
                    rule.addMetaAttribute( metaAttr, annotationDefinition.getPropertyValue( AnnotationDescr.VALUE ) );
                } else {
                    Map<String, Object> map = new HashMap<>( annotationDefinition.getValues().size() );
                    for ( String key : annotationDefinition.getValues().keySet() ) {
                        map.put( key, annotationDefinition.getPropertyValue( key ) );
                    }
                    rule.addMetaAttribute( metaAttr, map );
                }
            } else {
                if ( ad.hasValue() ) {
                    if ( ad.getValueMap().size() == 1 ) {
                        rule.addMetaAttribute( metaAttr,
                                               resolveValue( ad.getSingleValueAsString() ) );
                    } else {
                        rule.addMetaAttribute( metaAttr,
                                               ad.getValueMap() );
                    }
                } else {
                    rule.addMetaAttribute( metaAttr,
                                           null );
                }
            }
        }
    }

    private static Object resolveValue( String value ) {
        // for backward compatibility, if something is not an expression, we return an string as is
        Object result = value;
        // try to resolve as an expression:
        try {
            result = CoreComponentsBuilder.get().getMVELExecutor().eval( value );
        } catch ( Exception e ) {
            // do nothing
        }
        return result;
    }

    public static void buildAttributes(final RuleBuildContext context) {
        final RuleImpl rule = context.getRule();
        final RuleDescr ruleDescr = context.getRuleDescr();
        boolean enforceEager = false;

        for ( final AttributeDescr attributeDescr : ruleDescr.getAttributes().values() ) {
            final String name = attributeDescr.getName();
            switch ( name ) {
                case "no-loop":
                    rule.setNoLoop( getBooleanValue( attributeDescr, true ) );
                    enforceEager = true;
                    break;
                case "auto-focus":
                    rule.setAutoFocus( getBooleanValue( attributeDescr, true ) );
                    break;
                case "agenda-group":
                    if ( StringUtils.isEmpty( rule.getRuleFlowGroup() ) ) {
                        rule.setAgendaGroup( attributeDescr.getValue() ); // don't override if RFG has already set this
                    } else {
                        if ( rule.getRuleFlowGroup().equals( attributeDescr.getValue() ) ) {
                            DroolsWarning warn = new RuleBuildWarning( rule, context.getParentDescr(), null,
                                                                       "Both an agenda-group ( " + attributeDescr.getValue() +
                                                                       " ) and a ruleflow-group ( " + rule.getRuleFlowGroup() +
                                                                       " ) are defined for rule " + rule.getName() + ". Since version 6.x the " +
                                                                       "two concepts have been unified, the ruleflow-group name will override the agenda-group. " );
                            context.addWarning( warn );
                        }
                    }
                    break;
                case "activation-group":
                    rule.setActivationGroup( attributeDescr.getValue() );
                    break;
                case "ruleflow-group":
                    rule.setRuleFlowGroup( attributeDescr.getValue() );
                    if ( !rule.getAgendaGroup().equals( InternalAgendaGroup.MAIN ) && !rule.getAgendaGroup().equals( attributeDescr.getValue() ) ) {
                        DroolsWarning warn = new RuleBuildWarning( rule, context.getParentDescr(), null,
                                                                   "Both an agenda-group ( " + attributeDescr.getValue() +
                                                                   " ) and a ruleflow-group ( " + rule.getRuleFlowGroup() +
                                                                   " ) are defined for rule " + rule.getName() + ". Since version 6.x the " +
                                                                   "two concepts have been unified, the ruleflow-group name will override the agenda-group. " );
                        context.addWarning( warn );
                    }
                    rule.setAgendaGroup( attributeDescr.getValue() ); // assign AG to the same name as RFG, as they are aliased to AGs anyway

                    break;
                case "lock-on-active":
                    boolean lockOnActive = getBooleanValue( attributeDescr, true );
                    rule.setLockOnActive( lockOnActive );
                    enforceEager |= lockOnActive;
                    break;
                case DroolsSoftKeywords.DURATION:
                case DroolsSoftKeywords.TIMER:
                    String duration = attributeDescr.getValue();
                    rule.setTimer( buildTimer( rule, duration, context ) );
                    break;
                case "calendars":
                    buildCalendars( rule, attributeDescr.getValue(), context );
                    break;
                case "date-effective":
                    try {
                        Date date = DateUtils.parseDate( attributeDescr.getValue() );
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime( date );
                        rule.setDateEffective( cal );
                    } catch (Exception e) {
                        DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                              "Wrong date-effective value: " + e.getMessage() );
                        context.addError( err );
                    }
                    break;
                case "date-expires":
                    try {
                        Date date = DateUtils.parseDate( attributeDescr.getValue() );
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime( date );
                        rule.setDateExpires( cal );
                    } catch (Exception e) {
                        DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                              "Wrong date-expires value: " + e.getMessage() );
                        context.addError( err );
                    }
                    break;
            }
        }

        buildSalience( context );

        buildEnabled( context );

        parseAnnotation(context, rule, ruleDescr, enforceEager);
    }

    private static void parseAnnotation(RuleBuildContext context, RuleImpl rule, RuleDescr ruleDescr, boolean enforceEager) {
        try {
            ActivationListener activationListener = getTypedAnnotation(ruleDescr, ActivationListener.class);
            if (activationListener != null) {
                rule.setActivationListener(CoreComponentsBuilder.get().getMVELExecutor().evalToString(activationListener.value()));
            }

            if (enforceEager) {
                rule.setEager(true);
            } else {
                Propagation propagation = getTypedAnnotation(ruleDescr, Propagation.class);
                if (propagation != null) {
                    if (propagation.value() == Propagation.Type.IMMEDIATE) {
                        rule.setDataDriven(true);
                    } else if (propagation.value() == Propagation.Type.EAGER) {
                        rule.setEager(true);
                    }
                }
            }

            Direct direct = getTypedAnnotation(ruleDescr, Direct.class);
            if (direct != null && direct.value()) {
                rule.setActivationListener("direct");
            }

            rule.setAllMatches(ruleDescr.hasAnnotation(All.class));

        } catch (Exception e) {
            DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                  e.getMessage() );
            context.addError( err  );
        }
    }

    private static boolean getBooleanValue(AttributeDescr attributeDescr, boolean defaultValue) {
        return (attributeDescr.getValue() == null || "".equals( attributeDescr.getValue().trim() )) ? defaultValue : Boolean.valueOf(attributeDescr.getValue());
    }

    private static void buildEnabled(final RuleBuildContext context) {
        String enabledText = context.getRuleDescr().getEnabled();
        if ( enabledText != null ) {
            if ( "true".equalsIgnoreCase( enabledText.trim() ) || "false".equalsIgnoreCase( enabledText.trim() ) ) {
                if ( Boolean.parseBoolean( enabledText ) ) {
                    context.getRule().setEnabled( EnabledBoolean.ENABLED_TRUE );
                } else {
                    context.getRule().setEnabled( EnabledBoolean.ENABLED_FALSE );
                }
            } else {
                context.getDialect().getEnabledBuilder().build( context );
            }
        }
    }

    private static void buildSalience(final RuleBuildContext context) {
        String salienceText = context.getRuleDescr().getSalience();
        if ( salienceText != null && !salienceText.equals( "" ) ) {
            try {
                // First check if it is an Integer
                Salience salience = new SalienceInteger( Integer.parseInt( salienceText ) );
                context.getRule().setSalience( salience );
            } catch ( Exception e ) {
                // It wasn't an integer, so build as an expression
                context.getDialect().getSalienceBuilder().build( context );
            }
        }
    }
    
    private static void buildCalendars(RuleImpl rule, String calendarsString, RuleBuildContext context) {
        Object val = null;
        try {
            val = CoreComponentsBuilder.get().getMVELExecutor().eval( calendarsString );
            String[] calNames = null;
            if ( val instanceof List ) {
                calNames = ( String[] ) ((List)val).toArray( new String[ ((List)val).size() ] );
            } else if ( val instanceof String ) {
                calNames = new String[] { (String) val };
            } else {
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                      "Calendars attribute did not return a String or String[] '" + val + "'" );
                context.addError( err  );
            }
            if ( calNames != null ) {
                rule.setCalendars( calNames );
            }
        } catch ( Exception e ) {
            DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                  "Unable to build Calendars attribute '" + val + "'"  + e.getMessage() );
            context.addError( err );
        }
    }
    
    public static Timer buildTimer(RuleImpl rule, String timerString, RuleBuildContext context) {
        return buildTimer( timerString, context, expr -> createMVELExpr(expr, context), error -> registerError(error, rule, context) );
    }

    public static Timer buildTimer( String timerString, RuleBuildContext context,
                                    Function<String, TimerExpression> exprCreator, Consumer<String> errorManager ) {
        if( timerString.indexOf( '(' ) >=0 ) {
            timerString = timerString.substring( timerString.indexOf( '(' )+1, timerString.lastIndexOf( ')' ) ).trim();
        }
        
        int colonPos = timerString.indexOf( ":" );
        int semicolonPos = timerString.indexOf( ";" );
        String protocol = "int"; // default protocol
        if ( colonPos == -1 ) {
            if ( timerString.startsWith( "int" ) || timerString.startsWith( "cron" ) || timerString.startsWith( "expr" ) ) {
                errorManager.accept( "Incorrect timer definition '" + timerString + "' - missing colon?" );
                return null;
            }
        } else {
            protocol = timerString.substring( 0, colonPos );
        }
        
        String startDate = extractParam(timerString, "start");
        String endDate = extractParam(timerString, "end");
        String repeatLimitString = extractParam(timerString, "repeat-limit");
        int repeatLimit = repeatLimitString != null ? Integer.parseInt( repeatLimitString ) : -1;
        
        String body = timerString.substring( colonPos + 1, semicolonPos > 0 ? semicolonPos : timerString.length() ).trim();
        
        if ( "cron".equals( protocol ) ) {
            try {
                return new CronTimer( exprCreator.apply(startDate), exprCreator.apply(endDate), repeatLimit, new CronExpression( body ) );
            } catch ( ParseException e ) {
                errorManager.accept( "Unable to build set timer '" + timerString + "'" );
                return null;
            }
        }

        if ( "int".equals( protocol ) ) {
            String[] times = body.trim().split( "\\s" );
            long delay = 0;
            long period = 0;

            if ( times.length > 2 ) {
                errorManager.accept( "Incorrect number of arguments for interval timer '" + timerString + "'" );
                return null;
            }

            try {
                if ( times.length == 1 ) {
                    // only defines a delay
                    delay = TimeUtils.parseTimeString( times[0] );
                } else {
                    // defines a delay and a period for intervals
                    delay = TimeUtils.parseTimeString( times[0] );
                    period = TimeUtils.parseTimeString( times[1] );
                }
            } catch (RuntimeException e) {
                errorManager.accept( "Incorrect timer definition '" + timerString + "' " + e.getMessage() );
                return null;
            }

            return new IntervalTimer( exprCreator.apply(startDate), exprCreator.apply(endDate), repeatLimit, delay, period );
        }

        if ( "expr".equals( protocol ) ) {
            body = body.trim();
            StringTokenizer tok = new StringTokenizer( body, ",;" );

            if ( tok.countTokens() > 2 ) {
                errorManager.accept( "Incorrect number of arguments for expression timer '" + timerString + "'" );
                return null;
            }

            TimerExpression times = createTimerExpression( context, exprCreator, tok.nextToken().trim() );
            TimerExpression period = createTimerExpression( context, exprCreator, tok.hasMoreTokens() ? tok.nextToken().trim() : "0");

            return new ExpressionIntervalTimer( exprCreator.apply(startDate), exprCreator.apply(endDate), repeatLimit, times, period );
        }

        errorManager.accept( "Protocol for timer does not exist '" + timerString +"'" );
        return null;
    }

    private static TimerExpression createTimerExpression( RuleBuildContext context, Function<String, TimerExpression> exprCreator, String expression ) {
        return context != null ?
                ConstraintBuilder.get().buildTimerExpression( expression, context ) :
                exprCreator.apply( expression );
    }

    private static String extractParam(String timerString, String name) {
        int paramPos = timerString.indexOf( name );
        if (paramPos < 0) {
            return null;
        }
        int equalsPos = timerString.indexOf( '=', paramPos );
        int sepPos = timerString.indexOf( ',', equalsPos );
        int endPos = sepPos > 0 ? sepPos : timerString.length();
        return timerString.substring( equalsPos + 1, endPos ).trim();
    }

    private static TimerExpression createMVELExpr(String expr, RuleBuildContext context) {
        if (expr == null || context == null) {
            return null;
        }
        try {
            DateUtils.parseDate( expr );
            expr = "\"" + expr + "\""; // if expr is a valid date wrap in quotes
        } catch (Exception e) { }
        return ConstraintBuilder.get().buildTimerExpression( expr, context );
    }

    private static void registerError(String error, RuleImpl rule, RuleBuildContext context) {
        DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null, error );
        context.addError( err );
    }
}
