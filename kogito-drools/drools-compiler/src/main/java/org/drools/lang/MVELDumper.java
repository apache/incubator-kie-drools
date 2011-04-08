package org.drools.lang;

/*
 * Author Jayaram C S
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.evaluators.Operator;
import org.drools.compiler.DrlExprParser;
import org.drools.core.util.ReflectiveVisitor;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.OperatorDescr;
import org.drools.lang.descr.RelationalExprDescr;

public class MVELDumper extends ReflectiveVisitor {

    private static final String[] standard;
    static {
        standard = new String[]{"==", "<", ">", ">=", "<=", "!="};
        Arrays.sort( standard );
    }

    public String dump( BaseDescr base ) {
        return dump( new StringBuilder(),
                     base,
                     0,
                     new MVELDumperContext() ).toString();
    }

    public String dump( BaseDescr base,
                        MVELDumperContext context ) {
        return dump( new StringBuilder(),
                     base,
                     0,
                     context ).toString();
    }

    public String dump( BaseDescr base,
                        int parentPrecedence ) {
        return dump( new StringBuilder(),
                     base,
                     parentPrecedence,
                     new MVELDumperContext()).toString();
    }

    public StringBuilder dump( StringBuilder sbuilder,
                               BaseDescr base,
                               int parentPriority,
                               MVELDumperContext context ) {
        if( context == null ) {
            context = new MVELDumperContext();
        }
        if ( base instanceof ConstraintConnectiveDescr ) {
            ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) base;
            boolean first = true;
            boolean wrapParenthesis = parentPriority > ccd.getConnective().getPrecedence();
            if ( wrapParenthesis ) {
                sbuilder.append( "( " );
            }
            for ( BaseDescr constr : ccd.getDescrs() ) {
                if ( first ) {
                    first = false;
                } else {
                    sbuilder.append( " " );
                    sbuilder.append( ccd.getConnective().toString() );
                    sbuilder.append( " " );
                }
                //sbuilder.append( "(" );
                dump( sbuilder,
                      constr,
                      ccd.getConnective().getPrecedence(),
                      context );
            }
            if ( wrapParenthesis ) {
                sbuilder.append( " )" );
            }
        } else if ( base instanceof AtomicExprDescr ) {
            AtomicExprDescr atom = (AtomicExprDescr) base;
            String expr = atom.getExpression().trim();
            if ( expr.matches( "eval\\s*\\(.*\\)\\s*" ) ) {
                // stripping "eval" as it is no longer necessary
                expr = expr.substring( expr.indexOf( '(' ) + 1,
                                       expr.lastIndexOf( ')' ) );
            }
            sbuilder.append( expr );
        } else if ( base instanceof RelationalExprDescr ) {
            RelationalExprDescr red = (RelationalExprDescr) base;
            String left = dump( red.getLeft(),
                                Integer.MAX_VALUE ); // maximum precedence, so wrap any child connective in parenthesis
            String right = dump( red.getRight(),
                                Integer.MAX_VALUE );
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
                      context );
            } else {
                dump( sbuilder,
                      result,
                      0,
                      context );
            }
        }
        return sbuilder;
    }

    public void processRestriction( MVELDumperContext context,
                                    StringBuilder sbuilder,
                                    String left,
                                    OperatorDescr operator,
                                    String right) {
        Operator op = Operator.determineOperator( operator.getOperator(),
                                                  operator.isNegated() );
        if ( op == Operator.determineOperator( "memberOf",
                                               operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                    .append( right )
                    .append( " contains " )
                    .append( left )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else if ( op == Operator.determineOperator( "excludes",
                                                      operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( !operator.isNegated() ) )
                    .append( left )
                    .append( " contains " )
                    .append( right )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else if ( op == Operator.determineOperator( "matches",
                                                      operator.isNegated() ) ) {
            sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                    .append( left )
                    .append( " ~= " )
                    .append( right )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else if ( Arrays.binarySearch( standard,
                                         op.getOperatorString() ) > 0 ) {
            sbuilder.append( evaluatorPrefix( operator.isNegated() ) )
                    .append( left )
                    .append( " " )
                    .append( operator.getOperator() )
                    .append( " " )
                    .append( right )
                    .append( evaluatorSufix( operator.isNegated() ) );
        } else {
            // rewrite operator as a function call
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
    }

    private String evaluatorPrefix( final boolean isNegated ) {
        if ( isNegated ) {
            return "!( ";
        }
        return "";
    }

    private String evaluatorSufix( final boolean isNegated ) {
        if ( isNegated ) {
            return " )";
        }
        return "";
    }
    
    public static class MVELDumperContext {
        private Map<String, OperatorDescr> aliases;
        private int counter;

        public MVELDumperContext() {
            this.aliases = new HashMap<String, OperatorDescr>();
            this.counter = 0;
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
            String alias = operator.getOperator()+counter++;
            operator.setAlias( alias );
            this.aliases.put( alias, operator );
            return alias;
        }
        
        
    }
}
