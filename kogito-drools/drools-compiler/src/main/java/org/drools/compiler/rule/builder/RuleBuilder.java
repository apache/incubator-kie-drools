/*
 * Copyright 2005 JBoss Inc
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

package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.compiler.RuleBuildWarning;
import org.drools.compiler.lang.DroolsSoftKeywords;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELObjectExpressionBuilder;
import org.drools.core.base.EnabledBoolean;
import org.drools.core.base.SalienceInteger;
import org.drools.core.base.mvel.MVELObjectExpression;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.Salience;
import org.drools.core.time.TimeUtils;
import org.drools.core.time.impl.CronExpression;
import org.drools.core.time.impl.CronTimer;
import org.drools.core.time.impl.ExpressionIntervalTimer;
import org.drools.core.time.impl.IntervalTimer;
import org.drools.core.time.impl.Timer;
import org.drools.core.util.DateUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.util.StringUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This builds the rule structure from an AST.
 * Generates semantic code where necessary if semantics are used.
 * This is an internal API.
 */
public class RuleBuilder {

    // Constructor
    public RuleBuilder() {
    }


    public void preProcess(final RuleBuildContext context) {
        RuleDescr ruleDescr = context.getRuleDescr();

        //Query and get object instead of using String
        if ( null != ruleDescr.getParentName() && null != context.getPkg().getRule( ruleDescr.getParentName() ) ) {
            context.getRule().setParent( context.getPkg().getRule( ruleDescr.getParentName() ) );
        }
        // add all the rule's meta attributes
        buildMetaAttributes( context );

        if ( context.getRuleDescr() instanceof QueryDescr ) {
            context.getDialect().getQueryBuilder().build( context,
                                                          (QueryDescr) context.getRuleDescr() );
        }
    }

    /**
     * Build the give rule into the
     * @param context
     * @return
     */
    public void build(final RuleBuildContext context) {
        RuleDescr ruleDescr = context.getRuleDescr();

        final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( ruleDescr.getLhs().getClass() );
        if ( builder != null ) {
            Pattern prefixPattern = context.getPrefixPattern(); // this is established during pre-processing, if it's query
            final GroupElement ce = (GroupElement) builder.build( context,
                                                                  ruleDescr.getLhs(),
                                                                  prefixPattern );

            context.getRule().setLhs( ce );
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

    public void buildMetaAttributes(final RuleBuildContext context ) {
        RuleImpl rule = context.getRule();
        for ( String metaAttr : context.getRuleDescr().getAnnotationNames() ) {
            AnnotationDescr ad = context.getRuleDescr().getAnnotation( metaAttr );
            if ( ad.hasValue() ) {
                if ( ad.getValues().size() == 1 ) {
                    rule.addMetaAttribute( metaAttr,
                                           resolveValue( ad.getSingleValue() ) );
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

    private Object resolveValue( String value ) {
        // for backward compatibility, if something is not an expression, we return an string as is
        Object result = value;
        // try to resolve as an expression:
        try {
            Object resolvedValue = MVELSafeHelper.getEvaluator().eval( value );
            result = resolvedValue;
        } catch ( Exception e ) {
            // do nothing
        }
        return result;
    }

    public void buildAttributes(final RuleBuildContext context) {
        final RuleImpl rule = context.getRule();
        final RuleDescr ruleDescr = context.getRuleDescr();
        boolean enforceEager = false;

        for ( final AttributeDescr attributeDescr : ruleDescr.getAttributes().values() ) {
            final String name = attributeDescr.getName();
            if ( name.equals( "no-loop" ) ) {
                rule.setNoLoop( getBooleanValue( attributeDescr,
                                                 true ) );
                enforceEager = true;
            } else if ( name.equals( "auto-focus" ) ) {
                rule.setAutoFocus( getBooleanValue( attributeDescr,
                                                    true ) );
            } else if ( name.equals( "agenda-group" ) ) {
                if ( StringUtils.isEmpty(rule.getRuleFlowGroup())) {
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
            } else if ( name.equals( "activation-group" ) ) {
                rule.setActivationGroup( attributeDescr.getValue() );
            } else if ( name.equals( "ruleflow-group" ) ) {
                rule.setRuleFlowGroup( attributeDescr.getValue() );
                if ( ! rule.getAgendaGroup().equals( AgendaGroup.MAIN ) && ! rule.getAgendaGroup().equals( attributeDescr.getValue() ) ) {
                    DroolsWarning warn = new RuleBuildWarning( rule, context.getParentDescr(), null,
                                                               "Both an agenda-group ( " + attributeDescr.getValue() +
                                                               " ) and a ruleflow-group ( " + rule.getRuleFlowGroup() +
                                                               " ) are defined for rule " + rule.getName() + ". Since version 6.x the " +
                                                               "two concepts have been unified, the ruleflow-group name will override the agenda-group. " );
                    context.addWarning( warn );
                }
                rule.setAgendaGroup( attributeDescr.getValue() ); // assign AG to the same name as RFG, as they are aliased to AGs anyway
            } else if ( name.equals( "lock-on-active" ) ) {
                boolean lockOnActive = getBooleanValue( attributeDescr, true );
                rule.setLockOnActive( lockOnActive );
                enforceEager |= lockOnActive;
            } else if ( name.equals( DroolsSoftKeywords.DURATION ) || name.equals( DroolsSoftKeywords.TIMER ) ) {
                String duration = attributeDescr.getValue();
                buildTimer( rule, duration, context);
            }  else if ( name.equals( "calendars" ) ) {
                buildCalendars( rule, attributeDescr.getValue(), context );
            } else if ( name.equals( "date-effective" ) ) {
                try {
                    Date date = DateUtils.parseDate( attributeDescr.getValue(),
                                                     context.getKnowledgeBuilder().getDateFormats()  );
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime( date );
                    rule.setDateEffective( cal );
                } catch (Exception e) {
                    DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                          "Wrong date-effective value: " + e.getMessage() );
                    context.addError( err  );
                }
            } else if ( name.equals( "date-expires" ) ) {
                try {
                    Date date = DateUtils.parseDate( attributeDescr.getValue(),
                                                     context.getKnowledgeBuilder().getDateFormats()  );
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime( date );
                    rule.setDateExpires( cal );
                } catch (Exception e) {
                    DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                          "Wrong date-expires value: " + e.getMessage() );
                    context.addError( err  );
                }
            }
        }

        buildSalience( context );

        buildEnabled( context );
        
        AnnotationDescr ann = ruleDescr.getAnnotation( "activationListener" );
        if ( ann != null && !StringUtils.isEmpty( ann.getSingleValue() ) ) {
            rule.setActivationListener( MVELSafeHelper.getEvaluator().evalToString( ann.getSingleValue() ) );
        }

        ann = ruleDescr.getAnnotation( "Eager" );
        if ( enforceEager || ( ann != null && trueOrDefault( ann.getSingleValue() ) ) ) {
            rule.setEager( true );
        }

        ann = ruleDescr.getAnnotation( "Direct" );
        if ( ann != null && trueOrDefault( ann.getSingleValue() ) ) {
            rule.setActivationListener( "direct" );
        }

        //        buildDuration( context );
    }

    private boolean trueOrDefault( String singleValue ) {
        return StringUtils.isEmpty( singleValue ) || "true".equals( singleValue );
    }

    private boolean getBooleanValue(final AttributeDescr attributeDescr,
                                    final boolean defaultValue) {
        return (attributeDescr.getValue() == null || "".equals( attributeDescr.getValue().trim() )) ? defaultValue : Boolean.valueOf( attributeDescr.getValue() ).booleanValue();
    }

    //    private void buildDuration(final RuleBuildContext context) {
    //        String durationText = context.getRuleDescr().getDuration();
    //        try {
    //            // First see if its an Integer
    //            if ( durationText != null && !durationText.equals( "" )) {
    //                Duration duration = new DurationInteger( Integer.parseInt( durationText ) );
    //                context.getRule().setDuration( duration );
    //            }
    //        } catch (Exception e) {
    //            // It wasn't an integer, so build as an expression
    //            context.getDialect().getDurationBuilder().build( context );
    //        }
    //    }

    private void buildEnabled(final RuleBuildContext context) {
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

    private void buildSalience(final RuleBuildContext context) {
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
    
    private void buildCalendars(RuleImpl rule, String calendarsString, RuleBuildContext context) {
        Object val = null;
        try {
            val = MVELSafeHelper.getEvaluator().eval( calendarsString );
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
    
    private void buildTimer(RuleImpl rule, String timerString, RuleBuildContext context) {
        if( timerString.indexOf( '(' ) >=0 ) {
            timerString = timerString.substring( timerString.indexOf( '(' )+1, timerString.lastIndexOf( ')' ) ).trim();
        }
        
        int colonPos = timerString.indexOf( ":" );
        int semicolonPos = timerString.indexOf( ";" );
        String protocol = "int"; // default protocol
        if ( colonPos == -1 ) {
            if ( timerString.startsWith( "int" ) || timerString.startsWith( "cron" ) || timerString.startsWith( "expr" ) ) {
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                      "Incorrect timer definition '" + timerString + "' - missing colon?" );
                context.addError( err );
                return;
            }
        } else {
            protocol = timerString.substring( 0, colonPos );
        }
        
        String startDate = extractParam(timerString, "start");
        String endDate = extractParam(timerString, "end");
        String repeatLimitString = extractParam(timerString, "repeat-limit");
        int repeatLimit = repeatLimitString != null ? Integer.parseInt( repeatLimitString ) : -1;
        
        String body = timerString.substring( colonPos + 1, semicolonPos > 0 ? semicolonPos : timerString.length() ).trim();
        
        Timer timer = null;
        if ( "cron".equals( protocol ) ) {
            try {
                timer = new CronTimer( createMVELExpr(startDate, context), createMVELExpr(endDate, context), repeatLimit, new CronExpression( body ) );
            } catch ( ParseException e ) {
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                      "Unable to build set timer '" + timerString + "'" );                
                context.addError( err );
                return;
            }
        } else if ( "int".equals( protocol ) ) {
            String[] times = body.trim().split( "\\s" );
            long delay = 0;
            long period = 0;

            if ( times.length > 2 ) {
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                      "Incorrect number of arguments for interval timer '" + timerString + "'" );
                context.addError( err );
                return;
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
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                      "Incorrect timer definition '" + timerString + "' " + e.getMessage() );
                context.addError( err );
                return;
            }

            timer = new IntervalTimer( createMVELExpr(startDate, context), createMVELExpr(endDate, context), repeatLimit, delay, period );
        } else if ( "expr".equals( protocol ) ) {
            body = body.trim();
            StringTokenizer tok = new StringTokenizer( body, ",;" );

            if ( tok.countTokens() > 2 ) {
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                        "Incorrect number of arguments for expression timer '" + timerString + "'" );
                context.addError( err );
                return;
            }

            MVELObjectExpression times = MVELObjectExpressionBuilder.build( tok.nextToken().trim(), context );
            MVELObjectExpression period = null;
            if ( tok.hasMoreTokens() ) {
                period = MVELObjectExpressionBuilder.build( tok.nextToken().trim(), context );
            } else {
                period = MVELObjectExpressionBuilder.build( "0", context );
            }

            timer = new ExpressionIntervalTimer( createMVELExpr(startDate, context), createMVELExpr(endDate, context), repeatLimit, times, period );
        } else {
            DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                                                  "Protocol for timer does not exist '" + timerString +"'" );
            context.addError( err );
            return;
        }
        rule.setTimer( timer );
    }

    private String extractParam(String timerString, String name) {
        int paramPos = timerString.indexOf( name );
        if (paramPos < 0) {
            return null;
        }
        int equalsPos = timerString.indexOf( '=', paramPos );
        int sepPos = timerString.indexOf( ',', equalsPos );
        int endPos = sepPos > 0 ? sepPos : timerString.length();
        return timerString.substring( equalsPos + 1, endPos ).trim();
    }

    private MVELObjectExpression createMVELExpr(String expr, RuleBuildContext context) {
        if (expr == null) {
            return null;
        }
        try {
            DateUtils.parseDate( expr, context.getKnowledgeBuilder().getDateFormats() );
            expr = "\"" + expr + "\""; // if expr is a valid date wrap in quotes
        } catch (Exception e) { }
        return MVELObjectExpressionBuilder.build( expr, context );
    }
}
