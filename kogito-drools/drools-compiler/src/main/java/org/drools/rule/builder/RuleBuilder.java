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

package org.drools.rule.builder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.drools.RuntimeDroolsException;
import org.drools.base.EnabledBoolean;
import org.drools.base.SalienceInteger;
import org.drools.compiler.DroolsError;
import org.drools.compiler.RuleBuildError;
import org.drools.core.util.DateUtils;
import org.drools.core.util.StringUtils;
import org.drools.lang.DroolsSoftKeywords;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.spi.Salience;
import org.drools.time.TimeUtils;
import org.drools.time.impl.CronExpression;
import org.drools.time.impl.CronTimer;
import org.drools.time.impl.IntervalTimer;
import org.drools.time.impl.Timer;
import org.mvel2.MVEL;

/**
 * This builds the rule structure from an AST.
 * Generates semantic code where necessary if semantics are used.
 * This is an internal API.
 */
public class RuleBuilder {

    // Constructor
    public RuleBuilder() {
    }

    /**
     * Build the give rule into the
     * @param context
     * @return
     */
    public void build(final RuleBuildContext context) {
        RuleDescr ruleDescr = context.getRuleDescr();

        //Query and get object instead of using String
        if ( null != ruleDescr.getParentName() && null != context.getPkg().getRule( ruleDescr.getParentName() ) ) {
            context.getRule().setParent( context.getPkg().getRule( ruleDescr.getParentName() ) );
        }
        // add all the rule's meta attributes
        buildMetaAttributes( context );

        final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( ruleDescr.getLhs().getClass() );
        if ( builder != null ) {
            Pattern prefixPattern = null;
            if ( context.getRuleDescr() instanceof QueryDescr ) {
                prefixPattern = context.getDialect().getQueryBuilder().build( context,
                                                                              (QueryDescr) context.getRuleDescr() );
            }
            final GroupElement ce = (GroupElement) builder.build( context,
                                                                  ruleDescr.getLhs(),
                                                                  prefixPattern );

            context.getRule().setLhs( ce );
        } else {
            throw new RuntimeDroolsException( "BUG: builder not found for descriptor class " + ruleDescr.getLhs().getClass() );
        }

        // build all the rule's attributes
        // must be after building LHS because some attributes require bindings from the LHS
        buildAttributes( context );

        // Build the consequence and generate it's invoker/s
        // generate the main rule from the previously generated s.
        if ( !(ruleDescr instanceof QueryDescr) ) {
            // do not build the consequence if we have a query

            context.getDialect().getConsequenceBuilder().build( context, "default" );
            
            for ( String name : ruleDescr.getNamedConsequences().keySet() ) {
                context.getDialect().getConsequenceBuilder().build( context, name );
            }
        }

    }

    public void buildMetaAttributes(final RuleBuildContext context ) {
        Rule rule = context.getRule();
        for ( String metaAttr : context.getRuleDescr().getAnnotationNames() ) {
            String value = context.getRuleDescr().getAnnotation(metaAttr).getValue();
            if( value.startsWith( "\"" ) && value.endsWith( "\"" ) && value.length() > 2 ) {
                value = StringUtils.unescapeJava( value.substring( 1, value.length()-1 ) );
            }
            rule.addMetaAttribute( metaAttr, value );
        }
    }

    public void buildAttributes(final RuleBuildContext context) {
        final Rule rule = context.getRule();
        final RuleDescr ruleDescr = context.getRuleDescr();

        for ( final AttributeDescr attributeDescr : ruleDescr.getAttributes().values() ) {
            final String name = attributeDescr.getName();
            if ( name.equals( "no-loop" ) ) {
                rule.setNoLoop( getBooleanValue( attributeDescr,
                                                 true ) );
            } else if ( name.equals( "auto-focus" ) ) {
                rule.setAutoFocus( getBooleanValue( attributeDescr,
                                                    true ) );
            } else if ( name.equals( "agenda-group" ) ) {
                rule.setAgendaGroup( attributeDescr.getValue() );
            } else if ( name.equals( "activation-group" ) ) {
                rule.setActivationGroup( attributeDescr.getValue() );
            } else if ( name.equals( "ruleflow-group" ) ) {
                rule.setRuleFlowGroup( attributeDescr.getValue() );
            } else if ( name.equals( "lock-on-active" ) ) {
                rule.setLockOnActive( getBooleanValue( attributeDescr,
                                                       true ) );
            } else if ( name.equals( DroolsSoftKeywords.DURATION ) || name.equals( DroolsSoftKeywords.TIMER ) ) {
                String duration = attributeDescr.getValue();
                buildTimer( rule, duration, context);
            }  else if ( name.equals( "calendars" ) ) {
                buildCalendars( rule, attributeDescr.getValue(), context );
            } else if ( name.equals( "date-effective" ) ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime( DateUtils.parseDate( attributeDescr.getValue(),
                                                  context.getPackageBuilder().getDateFormats()  ) );
                rule.setDateEffective( cal );
            } else if ( name.equals( "date-expires" ) ) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime( DateUtils.parseDate( attributeDescr.getValue(),
                                                  context.getPackageBuilder().getDateFormats()  ) );
                rule.setDateExpires( cal );
            }
        }

        buildSalience( context );

        buildEnabled( context );

        //        buildDuration( context );
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
    
    private void buildCalendars(Rule rule, String calendarsString, RuleBuildContext context) {
        Object val = null;
        try {
            val = MVEL.eval( calendarsString );
            String[] calNames = null;
            if ( val instanceof List ) {
                calNames = ( String[] ) ((List)val).toArray( new String[ ((List)val).size() ] );
            } else if ( val instanceof String ) {
                calNames = new String[] { (String) val };
            } else {
                context.getErrors().add( "Calendars attribute did not return a String or String[] '" + val + "'"  );
            }
            if ( calNames != null ) {
                rule.setCalendars( calNames );
            }
        } catch ( Exception e ) {
            context.getErrors().add( "Unable to build Calendars attribute '" + val + "'"  + e.getMessage() );
        }
    }
    
    private void buildTimer(Rule rule, String timerString, RuleBuildContext context) {
        if( timerString.indexOf( '(' ) >=0 ) {
            timerString = timerString.substring( timerString.indexOf( '(' )+1, timerString.lastIndexOf( ')' ) ).trim();
        }
        
        int colonPos = timerString.indexOf( ":" );
        String protocol = null;
        if ( colonPos == -1 ) {
            // no protocol so assume interval semantics
            protocol = "int";
        } else {
            protocol = timerString.substring( 0, colonPos );
        }
        
        int startPos = timerString.indexOf( "start" );
        int endPos = timerString.indexOf( "end" );
        int repeatPos = timerString.indexOf( "repeat-limit" );
        
        Date startDate = null;
        Date endDate = null;
        int repeatLimit = -1;
        
        int  optionsPos = timerString.length();
        
        if ( startPos != -1 ) {
            optionsPos = startPos;
            int p = ( endPos != -1 && endPos < repeatPos ) ? endPos : repeatPos;
            
            if ( p == -1 ) {
                p = timerString.length();
            }
            
            int equalsPos = timerString.indexOf( '=', startPos );
            startDate = DateUtils.parseDate( timerString.substring( equalsPos + 1, p ).trim(),
                                             context.getPackageBuilder().getDateFormats()  );
        }
        
        if ( endPos != -1 ) {
            if ( optionsPos > endPos ) {
                optionsPos = endPos;
            }
            int p = ( startPos != -1 && startPos < repeatPos ) ? startPos : repeatPos;
            
            if ( p == -1 ) {
                p = timerString.length();
            }
            
            int equalsPos = timerString.indexOf( '=', endPos );
            endDate = DateUtils.parseDate( timerString.substring( equalsPos + 1, p ).trim(),
                                           context.getPackageBuilder().getDateFormats()  );
        }
        
        if ( repeatPos != -1 ) {
            if ( optionsPos > repeatPos ) {
                optionsPos = repeatPos;
            }
            int p = ( startPos != -1 && startPos < endPos ) ? startPos : endPos;
            
            if ( p == -1 ) {
                p = timerString.length();
            }
            
            int equalsPos = timerString.indexOf( '=', repeatPos );
            repeatLimit = Integer.parseInt( timerString.substring( equalsPos + 1, p ).trim() );
        }
                     
        String body = timerString.substring( colonPos + 1, optionsPos ).trim();
        
        Timer timer = null;
        if ( "cron".equals( protocol ) ) {
            try {
                timer = new CronTimer( startDate, endDate, repeatLimit, new CronExpression( body ) );
            } catch ( ParseException e ) {
                context.getErrors().add( "Unable to build set timer '" + timerString + "'");
                return;
            }
        } else if ( "int".equals( protocol ) ) {
            String[] times = body.trim().split( "\\s" );
            long delay = 0;
            long period = 0;
            if ( times.length == 1 ) {
                // only defines a delay
                delay = TimeUtils.parseTimeString( times[0] );
            } else if ( times.length == 2 ) {
                // defines a delay and a period for intervals
                delay = TimeUtils.parseTimeString( times[0] );
                period = TimeUtils.parseTimeString( times[1] );
            } else {
                DroolsError err = new RuleBuildError( rule, context.getParentDescr(), null,
                        "Incorrect number of arguments for interval timer '" + timerString + "'" );
                context.getErrors().add( err );
                return;
            }
            timer = new IntervalTimer(startDate, endDate, repeatLimit, delay, period);
        } else {
            context.getErrors().add( "Protocol for timer does not exist '" + timerString +"'");
            return;
        }
        rule.setTimer( timer );
    }

}
