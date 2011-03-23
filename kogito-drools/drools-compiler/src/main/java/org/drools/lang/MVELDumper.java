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

import org.drools.base.evaluators.Operator;
import org.drools.core.util.ReflectiveVisitor;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.RelationalExprDescr;

public class MVELDumper extends ReflectiveVisitor {

    public String dump( BaseDescr base ) {
        return dump( new StringBuilder(),
                     base,
                     0 ).toString();
    }

    public String dump( BaseDescr base,
                        int parentPrecedence ) {
        return dump( new StringBuilder(),
                     base,
                     parentPrecedence ).toString();
    }

    public StringBuilder dump( StringBuilder sbuilder,
                               BaseDescr base,
                               int parentPriority ) {
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
                dump( sbuilder,
                      constr,
                      ccd.getConnective().getPrecedence() );
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
            processRestriction( sbuilder,
                                dump( red.getLeft(),
                                      Integer.MAX_VALUE ), // maximum precedence, so wrap any child connective in parenthesis
                                red.getOperator(),
                                red.isNegated(),
                                dump( red.getRight(),
                                      Integer.MAX_VALUE ) );// maximum precedence, so wrap any child connective in parenthesis
        }
        return sbuilder;
    }

    public void processRestriction( StringBuilder sbuilder,
                                      String left,
                                      String operator,
                                      boolean isNegated,
                                      String right ) {
        Operator op = Operator.determineOperator( operator,
                                                  isNegated );
        if ( op == Operator.determineOperator( "memberOf",
                                               isNegated ) ) {
            operator = "contains";
            sbuilder.append( evaluatorPrefix( isNegated ) )
                    .append( right )
                    .append( " " )
                    .append( operator )
                    .append( " " )
                    .append( left )
                    .append( evaluatorSufix( isNegated ) );
        } else if ( op == Operator.determineOperator( "excludes",
                                                      isNegated ) ) {
            operator = "contains";
            sbuilder.append( evaluatorPrefix( !isNegated ) )
                    .append( left )
                    .append( " " )
                    .append( operator )
                    .append( " " )
                    .append( right )
                    .append( evaluatorSufix( isNegated ) );
        } else if ( op == Operator.determineOperator( "matches",
                                                      isNegated ) ) {
            operator = "~=";
            sbuilder.append( evaluatorPrefix( isNegated ) )
                    .append( left )
                    .append( " " )
                    .append( operator )
                    .append( " " )
                    .append( right )
                    .append( evaluatorSufix( isNegated ) );
        } else {
            sbuilder.append( evaluatorPrefix( isNegated ) )
                    .append( left )
                    .append( " " )
                    .append( operator )
                    .append( " " )
                    .append( right )
                    .append( evaluatorSufix( isNegated ) );
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
}
