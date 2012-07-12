/* 
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
package org.drools.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.base.EvaluatorWrapper;
import org.drools.base.evaluators.Operator;
import org.drools.compiler.DrlExprParser;
import org.drools.core.util.ReflectiveVisitor;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.OperatorDescr;
import org.drools.lang.descr.RelationalExprDescr;

public class MVELDumper extends ReflectiveVisitor implements ExpressionRewriter {
    
    private static final java.util.regex.Pattern evalRegexp = java.util.regex.Pattern.compile( "^eval\\s*\\(", Pattern.MULTILINE );

    private static final String[] standard;
    static {
        standard = new String[]{ "==", "<", ">", ">=", "<=", "!=", "~=" };
        Arrays.sort( standard );
    }

    public String dump( BaseDescr base ) {
        return dump( new StringBuilder(),
                     base,
                     0,
                     false,
                     createContext() ).toString();
    }

    public String dump( BaseDescr base,
                        MVELDumperContext context ) {
        return dump( new StringBuilder(),
                     base,
                     0,
                     false,
                     context ).toString();
    }

    public String dump( BaseDescr base,
                        int parentPrecedence ) {
        return dump( new StringBuilder(),
                     base,
                     parentPrecedence,
                     false,
                     createContext() ).toString();
    }

    public StringBuilder dump( StringBuilder sbuilder,
                               BaseDescr base,
                               int parentPriority,
                               boolean isInsideRelCons,
                               MVELDumperContext context ) {
        if ( context == null ) {
            context = createContext();
        }
        if ( base instanceof ConstraintConnectiveDescr ) {

            processConnectiveDescr( sbuilder, base, parentPriority, isInsideRelCons, context );

        } else if ( base instanceof AtomicExprDescr ) {
            AtomicExprDescr atom = (AtomicExprDescr) base;
            String expr = atom.getExpression().trim();
            Matcher m = evalRegexp.matcher( atom.getExpression() );
            if (m.find()) {
                // stripping "eval" as it is no longer necessary
                expr = expr.substring( expr.indexOf( '(' ) + 1,
                                       expr.lastIndexOf( ')' ) );
            }
            sbuilder.append( expr );
        } else if ( base instanceof BindingDescr ) {
            context.addBinding( (BindingDescr) base );
            if( isInsideRelCons ) {
                BindingDescr bind = (BindingDescr) base;
                String expr = bind.getExpression().trim();
                sbuilder.append( expr );
            }
        } else if ( base instanceof RelationalExprDescr ) {
            RelationalExprDescr red = (RelationalExprDescr) base;
            String left = dump( new StringBuilder(),
                                red.getLeft(),
                                Integer.MAX_VALUE,
                                true,
                                context ).toString(); // maximum precedence, so wrap any child connective in parenthesis
            String right = dump( new StringBuilder(),
                                 red.getRight(),
                                 Integer.MAX_VALUE,
                                 true,
                                 context ).toString();
            processRestriction( context,
                                sbuilder,
                                left,
                                red.getOperatorDescr(),
                                right );// maximum precedence, so wrap any child connective in parenthesis
        } else if ( base instanceof ExprConstraintDescr ) {
            DrlExprParser expr = new DrlExprParser();
            ConstraintConnectiveDescr result = expr.parse( ((ExprConstraintDescr) base).getExpression() );
            if ( result.getDescrs().size() == 1 ) {
                dump( sbuilder,
                      result.getDescrs().get( 0 ),
                      0,
                      isInsideRelCons,
                      context );
            } else {
                dump( sbuilder,
                      result,
                      0,
                      isInsideRelCons,
                      context );
            }
        }
        return sbuilder;
    }

    protected void processConnectiveDescr( StringBuilder sbuilder,
                                         BaseDescr base,
                                         int parentPriority,
                                         boolean isInsideRelCons,
                                         MVELDumperContext context ) {
        ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) base;
        boolean first = true;
        boolean wrapParenthesis = parentPriority > ccd.getConnective().getPrecedence();
        if ( wrapParenthesis ) {
            sbuilder.append( "( " );
        }
        for ( BaseDescr constr : ccd.getDescrs() ) {
            if ( !( constr instanceof BindingDescr ) ) {
                if ( first ) {
                    first = false;
                } else {
                    sbuilder.append( " " );
                    sbuilder.append( ccd.getConnective().toString() );
                    sbuilder.append( " " );
                }
            }
            dump( sbuilder,
                    constr,
                    ccd.getConnective().getPrecedence(),
                    isInsideRelCons,
                    context );
        }
        if( first == true ) {
            // means all children were actually only bindings, replace by just true
            sbuilder.append( "true" );
        }
        if ( wrapParenthesis ) {
            sbuilder.append( " )" );
        }

    }

    public void processRestriction( MVELDumperContext context,
                                    StringBuilder sbuilder,
                                    String left,
                                    OperatorDescr operator,
                                    String right ) {
        Operator op = Operator.determineOperator( operator.getOperator(),
                                                  operator.isNegated() );
        if ( op == Operator.determineOperator( "memberOf",
                                               operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                    .append( right )
                    .append( " contains " )
                    .append( left )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else if ( op == Operator.determineOperator( "contains",
                                                      operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                    .append( left )
                    .append( " contains " )
                    .append( right )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else if ( op == Operator.determineOperator( "excludes",
                                                      operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( !operator.isNegated() ) )
                    .append( left )
                    .append( " contains " )
                    .append( right )
                    .append( evaluatorSufix( !operator.isNegated() ) );
        } else if ( op == Operator.determineOperator( "matches",
                                                      operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                    .append( left )
                    .append( " ~= " )
                    .append( right )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else if ( lookupBasicOperator( operator.getOperator() ) ) {
            rewriteBasicOperator( context, sbuilder, left, operator, right );
        } else {
            // rewrite operator as a function call
            rewriteOperator( context, sbuilder, left, operator, right );
        }
    }

    protected void rewriteBasicOperator( MVELDumperContext context,
                                         StringBuilder sbuilder,
                                         String left,
                                         OperatorDescr operator,
                                         String right) {
        sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                .append( left )
                .append( " " )
                .append( operator.getOperator() )
                .append( " " )
                .append( right )
                .append( evaluatorSufix( operator.isNegated() ) );
    }

    protected boolean lookupBasicOperator( String op ) {
        return Arrays.binarySearch( standard, op ) >= 0;

    }

    protected void rewriteOperator( MVELDumperContext context,
                                    StringBuilder sbuilder,
                                    String left,
                                    OperatorDescr operator,
                                    String right ) {
        String alias = context.createAlias( operator );
        operator.setLeftString( left );
        operator.setRightString( right );
        sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                .append( alias )
                .append( ".evaluate( " )
                .append( left )
                .append( ", " )
                .append( right )
                .append( " )" )
                .append( evaluatorSufix( operator.isNegated() ) );

    }

    protected String evaluatorPrefix(final boolean isNegated) {
        if ( isNegated ) {
            return "!( ";
        }
        return "";
    }

    protected String evaluatorSufix(final boolean isNegated) {
        if ( isNegated ) {
            return " )";
        }
        return "";
    }


    protected MVELDumperContext createContext() {
        return new MVELDumperContext();
    }


    public Class<?> getEvaluatorWrapperClass() {
        return EvaluatorWrapper.class;
    }

    public static class MVELDumperContext {
        protected Map<String, OperatorDescr> aliases;
        protected int                        counter;
        protected List<BindingDescr>         bindings;
        protected Map<String, Class<?>>      localTypes;

        public MVELDumperContext() {
            this.aliases = new HashMap<String, OperatorDescr>();
            this.counter = 0;
            this.bindings = null;
            this.localTypes = null;
        }

        /**
         * @return the aliases
         */
        public Map<String, OperatorDescr> getAliases() {
            return aliases;
        }

        /**
         * @param aliases the aliases to set
         */
        public void setAliases( Map<String, OperatorDescr> aliases ) {
            this.aliases = aliases;
        }

        /**
         * Creates a new alias for the operator, setting it in the descriptor
         * class, adding it to the internal Map and returning it as a String
         *
         * @param operator
         * @return
         */
        public String createAlias( OperatorDescr operator ) {
            String alias = operator.getOperator() + counter++;
            operator.setAlias( alias );
            this.aliases.put( alias,
                              operator );
            return alias;
        }

        /**
         * Adds a binding to the list of bindings on this context
         * @param bind
         */
        public void addBinding( BindingDescr bind ) {
            if( this.bindings == null ) {
                this.bindings = new ArrayList<BindingDescr>();
            }
            this.bindings.add( bind );
        }

        @SuppressWarnings("unchecked")
        public List<BindingDescr> getBindings() {
            return this.bindings == null ? Collections.EMPTY_LIST : this.bindings;
        }

        public Map<String,Class<?>> getLocalTypes() {
            return localTypes;
        }

        public void setLocalTypes(Map<String, Class<?>> localTypes) {
            this.localTypes = localTypes;
        }
    }
}
