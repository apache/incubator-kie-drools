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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.core.util.ReflectiveVisitor;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PackageDescrDumper;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;

/**
 * TODO: replace this class by a proper DRL dumper based on templates
 */
public class DrlDumper extends ReflectiveVisitor
    implements
    PackageDescrDumper {

    private StringBuilder        drlDump;
    private static final String eol          = System.getProperty( "line.separator" );
    private String              template;
    private boolean             needsBracket = false;

    public synchronized String dump(final PackageDescr packageDescr) {
        this.drlDump = new StringBuilder();
        visitPackageDescr( packageDescr );
        return this.drlDump.toString();
    }

    public String getTemplate() {
        return this.template;
    }

    public void visitAndDescr(final AndDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            this.template = processDescrList( descr.getDescrs() );
        } else {
            this.template = "";
        }
    }
    
    private static Set needsQuotes = new HashSet();
    static {
        needsQuotes.add( "agenda-group" );
        needsQuotes.add( "activation-group" );
        needsQuotes.add( "ruleflow-group" );
        needsQuotes.add( "date-effective" );
        needsQuotes.add( "date-expires" );
        needsQuotes.add( "dialect" );
    }
    

    public void visitAttributeDescr(final AttributeDescr attributeDescr) {
        this.template = new String();
        final String name = attributeDescr.getName();
        String value = null;
                        
        if ( needsQuotes.contains( name) ) {
            // These attributes may need quotes around them, if they have spaces, so add anyway
            value = "\"" + attributeDescr.getValue() + "\"";
        } else {
            value = attributeDescr.getValue();
        }
        this.template = "\t " + name + " " + value + DrlDumper.eol;
    }

    public void visitFieldConstraintDescr(final FieldConstraintDescr descr) {
        if ( !descr.getRestrictions().isEmpty() ) {
            this.template = descr.getFieldName() + " " + processFieldConstraint( descr.getRestriction(),
                                                                                 false );
        }
    }

    public void visitVariableRestrictionDescr(final VariableRestrictionDescr descr) {
        this.template = new String();
        this.template = descr.getEvaluator() + " " + descr.getIdentifier();
    }

    public void visitPatternDescr(final PatternDescr descr) {
        StringBuilder buf = new StringBuilder();
        buf.append( "\t\t" );
        if ( descr.getIdentifier() != null ) {
            buf.append(  descr.getIdentifier() );
            buf.append( " : " );
            
        }
        buf.append( descr.getObjectType() );
        buf.append( "( " );
        if ( !descr.getConstraint().getDescrs().isEmpty() ) {
            buf.append( processColoumnConstraintList( descr.getConstraint().getDescrs() ) );
        }
        buf.append( " )" );
        if( descr.getSource() != null ) {
            buf.append( " from " );
            this.template = buf.toString();
            visit( descr.getSource() );
            buf.append( this.template );
        }
        this.template = buf.toString();
    }

    public void visitEvalDescr(final EvalDescr descr) {
        this.template = new String();
        this.template = "\t\teval ( " + descr.getContent() + " )" + DrlDumper.eol;
    }

    public void visitExistsDescr(final ExistsDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            this.template = "\t\texists " + processDescrList( descr.getDescrs() ) + ";";
        } else {
            this.template = "";
        }
    }

    public void visitCollectDescr(final CollectDescr descr) {
        String tmpstr = new String();
        tmpstr += " collect (";
        visitPatternDescr( descr.getInputPattern() );
        tmpstr += this.template.substring( 2 );
        this.template = tmpstr + ");";
    }

    public void visitAccumulateDescr(final AccumulateDescr descr) {
        String tmpstr = new String();
        tmpstr += " accumulate (";
        if ( descr.isSinglePattern() ) {
            visitPatternDescr( descr.getInputPattern() );
        } else {
            visit(descr.getInput());
        }
        tmpstr += this.template.substring( 2 );

        if ( descr.isExternalFunction() ) {
            AccumulateFunctionCallDescr func = descr.getFunctions().get( 0 );
            tmpstr += "," + func.getFunction() + "(" + func.getParams()[0] + ")";
        } else {
            tmpstr += ", init(" + descr.getInitCode() + "), ";
            tmpstr += "action(" + descr.getActionCode() + "), ";
            if( descr.getReverseCode() != null ) {
                tmpstr += " reverse(" + descr.getReverseCode() +"), ";
            }
            tmpstr += "result(" + descr.getResultCode() + ")";
        }
        this.template = tmpstr + ");";
    }

    public void visitFromDescr(final FromDescr descr) {
        this.template = descr.getDataSource().toString();
    }

    public void visitForallDescr(final ForallDescr descr) {
        String localstr = new String();
        localstr += "\t\tforall ( ";
        localstr += DrlDumper.eol;

        for ( final Iterator ite = descr.getDescrs().iterator(); ite.hasNext(); ) {
            Object obj = ite.next();
            visit( obj );
            localstr += this.template + DrlDumper.eol;
        }

        this.template = localstr;
        this.template += "\t\t)";
        this.template += DrlDumper.eol;
    }

    public void visitFieldBindingDescr(final BindingDescr descr) {
        this.template = new String();
        this.template = descr.getVariable() + " : " + descr.getExpression();
    }

    public void visitFunctionDescr(final FunctionDescr functionDescr) {
        this.template = new String();
        final String parameterTemplate = processParameters( functionDescr.getParameterNames(),
                                                            functionDescr.getParameterTypes() );

        this.template = "function " + functionDescr.getReturnType() + " " + functionDescr.getName() + "(" + parameterTemplate + "){" +

        functionDescr.getText() + DrlDumper.eol + "}" + DrlDumper.eol;

    }

    public void visitLiteralRestrictionDescr(final LiteralRestrictionDescr descr) {
        this.template = "";
        String text = descr.getText();
        if ( text == null || descr.getType() == LiteralRestrictionDescr.TYPE_NULL ) {
            text = "null";
        } else if( descr.getType() == LiteralRestrictionDescr.TYPE_NUMBER ) {
            try {
                Integer.parseInt( text );
            } catch ( final NumberFormatException e ) {
                text = "\"" + text + "\"";
            }
        } else if( descr.getType() == LiteralRestrictionDescr.TYPE_STRING ) {
            text = "\"" + text + "\"";
        }
        this.template = descr.getEvaluator() + " " + text;
    }

    public void visitQualifiedIdentifierRestrictionDescr(final QualifiedIdentifierRestrictionDescr descr) {
        this.template = descr.getEvaluator() + " " + descr.getText();
    }

    public void visitRestrictionConnectiveDescr(final RestrictionConnectiveDescr descr) {
        String tmp = this.processFieldConstraint( descr,
                                                  true );
        this.template = tmp;
    }

    public void visitNotDescr(final NotDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            this.template = "\t   not ( " + processDescrList( descr.getDescrs() ) +")";
        } else {
            this.template = "";
        }
    }

    public void visitOrDescr(final OrDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            this.template = processOrDescrList( descr.getDescrs() );
        } else {
            this.template = " ";
        }
    }

    public void visitPackageDescr(final PackageDescr packageDescr) {
        final String packageName = packageDescr.getName();
        final String xmlString = "package " + packageName + ";" + DrlDumper.eol + DrlDumper.eol;

        appendDrlDump( xmlString );
        if ( packageDescr.getImports() != null ) {
            appendDrlDump( processImportsList( packageDescr.getImports() ) );
        }
        if ( packageDescr.getGlobals() != null ) {
            appendDrlDump( processGlobalsList( packageDescr.getGlobals() ) );
        }
        if ( packageDescr.getFunctionImports() != null ) {
            appendDrlDump( processFunctionImportsList( packageDescr.getFunctionImports() ) );
        }
        if ( packageDescr.getFunctions() != null ) {
            appendDrlDump( processFunctionsList( packageDescr.getFunctions() ) );
        }
        if ( packageDescr.getRules() != null ) {
            appendDrlDump( processRules( packageDescr.getRules() ) );
        }

    }

    public void visitPredicateDescr(final PredicateDescr descr) {
        this.template = new String();
        this.template = "eval( " + descr.getContent() + " )";

    }

    public void visitReturnValueRestrictionDescr(final ReturnValueRestrictionDescr descr) {
        this.template = new String();
        this.template = descr.getEvaluator() + " ( " + descr.getContent() + ")";
    }

    public void visitQueryDescr(final QueryDescr descr) {
        this.template = new String();
        this.template = "query \"" + descr.getName() + "\"" + processDescrList( descr.getLhs().getDescrs() )  + "end";
    }
    

    private String processRules(final List rules) {
        String ruleList = "";
        Object ruleobj;
        for ( final Iterator iterator = rules.iterator(); iterator.hasNext(); ) {
            ruleobj = iterator.next();
            if ( ruleobj instanceof QueryDescr ) {
                visitQueryDescr((QueryDescr) ruleobj);
                ruleList += this.template;
                break;
            }

            final RuleDescr ruleDescr = (RuleDescr) ruleobj;
            String rule = "rule \"" + ruleDescr.getName() + "\" " + DrlDumper.eol;
            final String attribute = processAttribute( ruleDescr.getAttributes().values() );
            String lhs = "";
            if ( !ruleDescr.getLhs().getDescrs().isEmpty() ) {
                lhs = "\t when" + DrlDumper.eol + processDescrList( ruleDescr.getLhs().getDescrs() ) + DrlDumper.eol;
            } else {

                lhs = "\t when";
            }

            String rhs = (String) ruleDescr.getConsequence();
            if ( rhs == null ) {
                rhs = "\t then" + DrlDumper.eol + "\t";
            } else {
                rhs = "\t then" + DrlDumper.eol + ruleDescr.getConsequence();
            }

            rule += attribute;
            rule += lhs;
            rule += rhs;
            rule += "end" + DrlDumper.eol;
            ruleList += rule;
        }

        return ruleList + DrlDumper.eol;
    }

    private String processOrDescrList(final List descr) {
        String descrString = "";
        for ( final Iterator iterator = descr.iterator(); iterator.hasNext(); ) {
            visit( iterator.next() );
            descrString += this.template;
            if ( descrString.endsWith( DrlDumper.eol ) ) {
                descrString = descrString.substring( 0,
                                                     descrString.indexOf( DrlDumper.eol ) );
            }
            descrString += " || ";
        }
        return descrString.substring( 0,
                                      descrString.length() - 4 );
    }

    private String processColoumnConstraintList(final List descr) {
        String descrString = "";
        Object previous = null;
        for ( final Iterator iterator = descr.iterator(); iterator.hasNext(); ) {

            final Object temp = iterator.next();
            visit( temp );

            if ( previous == null ) {
                descrString += this.template;
            } else if ( previous instanceof BindingDescr && !(temp instanceof BindingDescr) && !(temp instanceof PredicateDescr) ) {
                final FieldConstraintDescr tempDescr = (FieldConstraintDescr) temp;
                final BindingDescr previousDescr = (BindingDescr) previous;
                if ( tempDescr.getFieldName().equals( previousDescr.getExpression() ) ) {
                    // as its a binding followed by a field constraint we need to remove 
                    // the extra field name                    
                    descrString += this.template.substring( tempDescr.getFieldName().length() + 1 );
                } else {
                    descrString += " , " + this.template;
                }
            } else {
                descrString += " , " + this.template;
            }

            previous = temp;

        }
        return descrString.substring( 0,
                                      descrString.length() );
    }

    private String processFieldConstraint(final RestrictionConnectiveDescr restriction,
                                          boolean addBrackets) {
        String descrString = "";
        String connective = "";
        boolean bracketTmp = this.needsBracket;
        this.needsBracket = restriction.getRestrictions().size() > 1;

        if ( restriction.getConnective() == RestrictionConnectiveDescr.OR ) {
            connective = " || ";
        } else {
            connective = " && ";
        }
        if ( addBrackets && bracketTmp ) {
            descrString += "( ";
        }
        for ( final Iterator it = restriction.getRestrictions().iterator(); it.hasNext(); ) {
            final Object temp = it.next();
            visit( temp );
            descrString += this.template;
            if ( it.hasNext() ) {
                descrString += connective;
            }
        }
        if ( addBrackets && bracketTmp ) {
            descrString += " )";
        }
        this.needsBracket = bracketTmp;
        return descrString;
    }

    private String processDescrList(final List descr) {
        String descrString = "";
        for ( final Iterator ite = descr.iterator(); ite.hasNext(); ) {

            Object obj = ite.next();

            visit( obj );
            descrString += this.template;

            // this is absolutely dumb... we need to redo this dumper...
            if ( obj.getClass().equals( PatternDescr.class ) || 
                 obj.getClass().equals( CollectDescr.class ) ||
                 obj.getClass().equals( EvalDescr.class ) ||
                 obj.getClass().equals( AccumulateDescr.class ) ||
                 obj.getClass().equals( FromDescr.class )) {
                descrString += DrlDumper.eol;
            } else if ( ite.hasNext() ) {
                descrString += " && ";
            }
        }
        return descrString;
    }

    private String processFunctionsList(final List functions) {
        String functionList = "";

        for ( final Iterator it = functions.iterator(); it.hasNext(); ) {
            visit( it.next() );
            functionList += this.template;
        }

        return functionList + DrlDumper.eol;
    }

    private String processAttribute(final Collection<AttributeDescr> attributes) {

        String attributeList = "";
        for ( final AttributeDescr attributeDescr : attributes) {
            visit( attributeDescr );
            attributeList += this.template;
        }
        return attributeList;
    }

    private String processParameters(final List parameterNames,
                                     final List parameterTypes) {
        if ( parameterNames.isEmpty() ) {
            return "";
        }

        String paramList = "";
        int i = 0;
        for ( final Iterator it = parameterNames.iterator(); it.hasNext(); i++ ) {
            final String paramName = (String) it.next();
            final String paramType = (String) parameterTypes.get( i );
            final String paramTemplate = paramType + " " + paramName + ",";
            paramList += paramTemplate;
        }
        paramList = paramList.substring( 0,
                                         paramList.length() - 1 );
        return paramList;
    }

    private String processGlobalsList(final List globals) {
        String globalList = "";

        for ( final Iterator it = globals.iterator(); it.hasNext(); ) {
            final GlobalDescr global = (GlobalDescr) it.next();
            final String identifier = global.getIdentifier();
            final String type = global.getType();
            final String globalTemplate = "global " + type + " " + identifier + ";" + DrlDumper.eol;
            globalList += globalTemplate;
        }

        return globalList + DrlDumper.eol;
    }
    
    private String processFunctionImportsList(final List imports) {
        String importList = "";

        for ( final Iterator it = imports.iterator(); it.hasNext(); ) {
            final String importString = ((FunctionImportDescr) it.next()).getTarget();
            final String importTemplate = "import function " + importString + ";" + DrlDumper.eol;
            importList += importTemplate;
        }
        return importList + DrlDumper.eol;
    }

    private String processImportsList(final List imports) {
        String importList = "";

        for ( final Iterator it = imports.iterator(); it.hasNext(); ) {
            final ImportDescr importDescr = (ImportDescr) it.next();
            final String importTemplate = "import " + importDescr.getTarget() + ";" + DrlDumper.eol;
            importList += importTemplate;
        }
        return importList + DrlDumper.eol;
    }

    private void appendDrlDump(final String temp) {
        this.drlDump.append( temp );
    }

}
