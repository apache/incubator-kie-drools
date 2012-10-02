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
import org.drools.rule.builder.RuleBuildContext;

import static org.drools.core.util.ClassUtils.findClass;
import static org.drools.core.util.StringUtils.indexOfOutOfQuotes;
import static org.drools.rule.builder.dialect.DialectUtil.findClassByName;

public class MVELDumper extends ReflectiveVisitor implements ExpressionRewriter {
    
    private static final java.util.regex.Pattern evalRegexp = java.util.regex.Pattern.compile( "^eval\\s*\\(", Pattern.MULTILINE );

    private static final String[] standard;
    static {
        standard = new String[]{ "==", "<", ">", ">=", "<=", "!=", "~=", "instanceof" };
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
            sbuilder.append( processAtomicExpression(context, (AtomicExprDescr) base) );
        } else if ( base instanceof BindingDescr ) {
            processBinding(sbuilder, (BindingDescr) base, isInsideRelCons, context);
        } else if ( base instanceof RelationalExprDescr ) {
            processRelationalExpression(sbuilder, (RelationalExprDescr) base, context);
        } else if ( base instanceof ExprConstraintDescr ) {
            processConstraint(sbuilder, (ExprConstraintDescr) base, isInsideRelCons, context);
        }
        return sbuilder;
    }

    private void processConstraint(StringBuilder sbuilder, ExprConstraintDescr base, boolean isInsideRelCons, MVELDumperContext context) {
        DrlExprParser expr = new DrlExprParser();
        ConstraintConnectiveDescr result = expr.parse( base.getExpression() );
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

    private String processAtomicExpression(MVELDumperContext context, AtomicExprDescr atomicExpr) {
        String expr = atomicExpr.getExpression().trim();
        expr = processEval(expr);
        String[] constrAndExpr = processImplicitConstraints(expr, atomicExpr, context);
        return constrAndExpr[0] + constrAndExpr[1];
    }

    private void processBinding(StringBuilder sbuilder, BindingDescr bind, boolean isInsideRelCons, MVELDumperContext context) {
        String expr = bind.getExpression().trim();
        AtomicExprDescr atomicExpr = new AtomicExprDescr(expr);
        String[] constrAndExpr = processImplicitConstraints(expr, atomicExpr, context);

        if ( isInsideRelCons ) {
            sbuilder.append( constrAndExpr[0] ).append( constrAndExpr[1] );
        } else if ( constrAndExpr[0].length() > 4 ) {
            sbuilder.append( constrAndExpr[0].substring( 0, constrAndExpr[0].length() - 4 ) );
        }

        bind.setExpression( constrAndExpr[1] );
        context.addBinding(bind);
    }

    private void processRelationalExpression(StringBuilder sbuilder, RelationalExprDescr red, MVELDumperContext context) {
        // maximum precedence, so wrap any child connective in parenthesis
        StringBuilder left = dump(new StringBuilder(), red.getLeft(), Integer.MAX_VALUE, true, context);
        StringBuilder right = red.getRight() instanceof AtomicExprDescr ?
                processRightAtomicExpr(left, (AtomicExprDescr)red.getRight(), context) :
                dump( new StringBuilder(), red.getRight(), Integer.MAX_VALUE, true, context);

        processRestriction( context,
                            sbuilder,
                            left.toString(),
                            red.getOperatorDescr(),
                            right.toString() );// maximum precedence, so wrap any child connective in parenthesis
    }

    private StringBuilder processRightAtomicExpr(StringBuilder left, AtomicExprDescr atomicExpr, MVELDumperContext context) {
        String expr = atomicExpr.getExpression().trim();
        expr = processEval( expr );
        String[] constrAndExpr = processImplicitConstraints(expr, atomicExpr, context);
        left.insert( 0, constrAndExpr[0] );
        return new StringBuilder( constrAndExpr[1] );
    }

    String[] processImplicitConstraints(String expr, AtomicExprDescr atomicExpr, MVELDumperContext context) {
        boolean hasQuotes = expr.indexOf('"') >= 0;
        String[] constrAndExpr = new String[] { "", expr };
        int sharpPos = hasQuotes ? indexOfOutOfQuotes(expr, '#') : expr.indexOf('#');
        int nullSafePos = hasQuotes ? indexOfOutOfQuotes(expr, "!.") : expr.indexOf("!.");

        while (sharpPos > 0 || nullSafePos > 0) {
            if ( nullSafePos < 0 || ( sharpPos > 0 && sharpPos < nullSafePos ) ) {
                String[] castAndExpr = processInlineCast(expr, atomicExpr, context, sharpPos);
                expr = castAndExpr[1];
                constrAndExpr = new String[] { constrAndExpr[0] + castAndExpr[0], expr };
            } else {
                String[] nullCheckAndExpr = processNullSafeDereferencing(expr, atomicExpr, nullSafePos);
                expr = nullCheckAndExpr[1];
                constrAndExpr = new String[] { constrAndExpr[0] + nullCheckAndExpr[0], expr };
            }
            sharpPos = hasQuotes ? indexOfOutOfQuotes(expr, '#') : expr.indexOf('#');
            nullSafePos = hasQuotes ? indexOfOutOfQuotes(expr, "!.") : expr.indexOf("!.");
        }
        return new String[] { constrAndExpr[0], processInferredCast(constrAndExpr[1], atomicExpr, context) };
    }

    private String[] processInlineCast(String expr, AtomicExprDescr atomicExpr, MVELDumperContext context, int sharpPos) {
        // convert "field1#Class.field2" in ["field1 instanceof Class && ", "((Class)field1).field2"]
        String field1 = expr.substring(0, sharpPos).trim();
        int sharpPos2 = expr.indexOf('#', sharpPos+1);
        String part2 = sharpPos2 < 0 ? expr.substring(sharpPos+1).trim() : expr.substring(sharpPos+1, sharpPos2).trim();
        String[] classAndField = splitInClassAndField(part2, context);
        if (classAndField == null) {
            return new String[] { "", expr };
        }

        String className = classAndField[0];
        String field2 = classAndField[1];

        String castedExpression = "((" + className + ")" + field1 + ")." + field2 + (sharpPos2 > 0 ? expr.substring(sharpPos2) : "");

        atomicExpr.setRewrittenExpression(castedExpression);
        return new String[] { field1 + " instanceof " + className + " && ", castedExpression };
    }

    private String processInferredCast(String expr, AtomicExprDescr atomicExpr, MVELDumperContext context) {
        if (context == null) {
            return expr;
        }
        Map.Entry<String, String> castEntry = context.getInferredCast(expr);
        if (castEntry == null) {
            return expr;
        }
        String castedExpr = "((" + castEntry.getValue() + ")" + castEntry.getKey() + ")" + expr.substring(castEntry.getKey().length());
        atomicExpr.setRewrittenExpression(castedExpr);
        return castedExpr;
    }

    private String[] processNullSafeDereferencing(String expr, AtomicExprDescr atomicExpr, int nullSafePos) {
        // convert "field1!.field2" in ["field1 != null && ", "field1.field2"]
        String field1 = expr.substring(0, nullSafePos).trim();
        expr = field1 + "." + expr.substring(nullSafePos+2).trim();
        String[] nullCheckAndExpr = new String[] { field1 + " != null && ", expr };
        atomicExpr.setRewrittenExpression(nullCheckAndExpr[1]);
        return nullCheckAndExpr;
    }

    private String processEval(String expr) {
        // stripping "eval" as it is no longer necessary
        return evalRegexp.matcher( expr ).find() ? expr.substring( expr.indexOf( '(' ) + 1, expr.lastIndexOf( ')' ) ) : expr;
    }

    private String[] splitInClassAndField(String expr, MVELDumperContext context) {
        String[] split = expr.split("\\.");
        if (split.length < 2) {
            return null;
        }

        if (split[0].endsWith("!")) {
            split[0] = split[0].substring(0, split[0].length()-1);
        }
        if (split.length < 3) {
            return split;
        }

        // check non-FQN case first
        if ( context == null || findClassByName(context.getRuleContext(), split[0]) != null ) {
            return new String[] { split[0], concatDotSeparated(split, 1, split.length) };
        }

        ClassLoader cl = context.getRuleContext().getPackageBuilder().getRootClassLoader();
        for (int i = split.length-1; i > 1; i++) {
            String className = concatDotSeparated(split, 0, i);
            if (className.endsWith("!")) {
                className = className.substring(0, className.length()-1);
            }
            if (findClass(className, cl) != null) {
                return new String[] { className, concatDotSeparated(split, i, split.length) };
            }
        }

        return null;
    }

    private String concatDotSeparated(String[] parts, int start, int end) {
        StringBuilder sb = new StringBuilder( parts[start] );
        for (int i = start+1; i < end; i++) {
            sb.append(".").append(parts[i]);
        }
        return sb.toString();
    }

    protected void processConnectiveDescr( StringBuilder sbuilder,
                                         BaseDescr base,
                                         int parentPriority,
                                         boolean isInsideRelCons,
                                         MVELDumperContext context ) {
        ConstraintConnectiveDescr ccd = (ConstraintConnectiveDescr) base;
        boolean wrapParenthesis = parentPriority > ccd.getConnective().getPrecedence();
        if ( wrapParenthesis ) {
            sbuilder.append( "( " );
        }
        boolean first = true;
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
        if( first ) {
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
            if (operator.getOperator().equals("instanceof")) {
                context.addInferredCast(left, right);
            }
            rewriteBasicOperator( sbuilder, left, operator, right );
        } else {
            // rewrite operator as a function call
            rewriteOperator( context, sbuilder, left, operator, right );
        }
    }

    protected void rewriteBasicOperator( StringBuilder sbuilder,
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
        private   RuleBuildContext           ruleContext;
        private   Map<String, String>        inferredCasts;

        public MVELDumperContext() {
            this.aliases = new HashMap<String, OperatorDescr>();
            this.counter = 0;
            this.bindings = null;
            this.localTypes = null;
        }

        public void clear() {
            this.aliases.clear();
            this.counter = 0;
            this.bindings = null;
            this.localTypes = null;
        }

        public void addInferredCast(String var, String cast) {
            if (inferredCasts == null) {
                inferredCasts = new HashMap<String, String>();
            }
            inferredCasts.put(var, cast);
        }

        public Map.Entry<String, String> getInferredCast(String expr) {
            if (inferredCasts != null) {
                for (Map.Entry<String, String> entry : inferredCasts.entrySet()) {
                    if (expr.matches(entry.getKey() + "\\s*\\..+")) {
                        return entry;
                    }
                }
            }
            return null;
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
            operator.setAlias(alias);
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

        public RuleBuildContext getRuleContext() {
            return ruleContext;
        }

        public MVELDumperContext setRuleContext(RuleBuildContext ruleContext) {
            this.ruleContext = ruleContext;
            return this;
        }
    }
}
