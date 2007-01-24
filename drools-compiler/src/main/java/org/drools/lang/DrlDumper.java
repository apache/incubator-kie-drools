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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PackageDescrDumper;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.util.ReflectiveVisitor;

/**
 * 
 * @author <a href="mailto:jayaramcs@gmail.com">Author Jayaram C S</a>
 *
 */
public class DrlDumper extends ReflectiveVisitor
    implements
    PackageDescrDumper {

    private StringBuffer        drlDump;
    private static final String eol = System.getProperty( "line.separator" );

    public synchronized String dump(final PackageDescr packageDescr) {
        this.drlDump = new StringBuffer();
        visitPackageDescr( packageDescr );
        return this.drlDump.toString();
    }

    public void visitAndDescr(final AndDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            this.template = processDescrList( descr.getDescrs() );
        } else {
            this.template = "";
        }
    }

    public void visitAttributeDescr(final AttributeDescr attributeDescr) {
        this.template = new String();
        String name = attributeDescr.getName();
        String value = null;
        if ( name.equals( "agenda-group" ) || name.equals( "activation-group" ) ) {
            // These attributes may need quotes around them, if they have spaces, so add anyway
            value =  "\"" + attributeDescr.getValue() + "\"";
        } else {
            value = attributeDescr.getValue();
        }
        this.template = "\t " + name + " " + value + DrlDumper.eol;
    }
    
    public void visitFieldConstraintDescr(final FieldConstraintDescr descr) {
        if ( !descr.getRestrictions().isEmpty() ) {
            this.template = descr.getFieldName() + " " + processFieldConstraint( descr.getRestrictions() );
        } 
    }    

    public void visitVariableRestrictionDescr(final VariableRestrictionDescr descr) {
        this.template = new String();
        this.template = descr.getEvaluator() + " " + descr.getIdentifier();
    }

    public void visitColumnDescr(final ColumnDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            if ( descr.getIdentifier() != null ) {
                this.template = "\t\t" + descr.getIdentifier() + " : " + descr.getObjectType() + "( " + processColoumnConstraintList( descr.getDescrs() ) + ")";
            } else {
                this.template = "\t\t" + descr.getObjectType() + "( " + processColoumnConstraintList( descr.getDescrs() ) + ")";
            }
        } else {
            if ( descr.getIdentifier() != null ) {
                this.template = "\t\t" + descr.getIdentifier() + " : " + descr.getObjectType() + "( )";
            } else {
                this.template = "\t\t" + descr.getObjectType() + "( )";
            }
        }

    }

    public void visitEvalDescr(final EvalDescr descr) {
        this.template = new String();
        this.template = "\t\teval ( " + descr.getText() + " )" + DrlDumper.eol;
    }

    public void visitExistsDescr(final ExistsDescr descr) {
        this.template = new String();
        if ( !descr.getDescrs().isEmpty() ) {
            this.template = "\t\texists " + processDescrList( descr.getDescrs() );
        } else {
            this.template = "";
        }
    }

    public void visitFieldBindingDescr(final FieldBindingDescr descr) {
        this.template = new String();
        this.template = descr.getIdentifier() + " : " + descr.getFieldName();
    }

    public void visitFunctionDescr(final FunctionDescr functionDescr) {
        this.template = new String();
        final String parameterTemplate = processParameters( functionDescr.getParameterNames(),
                                                            functionDescr.getParameterTypes() );

        this.template = "function " + functionDescr.getReturnType() + " " + functionDescr.getName() + "(" + parameterTemplate + "){" +

        functionDescr.getText() + DrlDumper.eol + "}" + DrlDumper.eol;

    }

    public void visitLiteralRestrictionDescr(final LiteralRestrictionDescr descr) {
        this.template = new String();
        String text = descr.getText();
        try {
            Integer.parseInt( text );
        } catch ( final NumberFormatException e ) {
            text = "\"" + text + "\"";
        }

        this.template = descr.getEvaluator() + " " + text;
    }
    
    public void visitRestrictionConnectiveDescr(final RestrictionConnectiveDescr descr) {
        if ( descr.getConnective() == RestrictionConnectiveDescr.OR ) {
            this.template = " | ";
        } else {
            this.template = " & ";
        }
    }

    public void visitNotDescr(final NotDescr descr) {
        this.template = new String();
        if ( descr.getDescrs().isEmpty() ) {
            this.template = "\t   not " + processDescrList( descr.getDescrs() );
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
            appendDrlDump( processGlobalsMap( packageDescr.getGlobals() ) );
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
        this.template = "( " + descr.getText() + " )";

    }

    public void visitReturnValueRestrictionDescr(final ReturnValueRestrictionDescr descr) {
        this.template = new String();
        this.template = descr.getEvaluator() + " ( " + descr.getText() + ")";
    }

    public void visitQueryDescr(final QueryDescr descr) {
        this.template = new String();
        this.template = "<query name=\"" + descr.getName() + "\">" + "<lhs>" + processDescrList( descr.getLhs().getDescrs() ) + "</lhs>" + "</query>";
    }

    private String template;

    private String processRules(final List rules) {
        String ruleList = "";
        for ( final Iterator iterator = rules.iterator(); iterator.hasNext(); ) {
            final RuleDescr ruleDescr = (RuleDescr) iterator.next();
            String rule = "rule \"" + ruleDescr.getName() + "\" " + DrlDumper.eol;
            final String attribute = processAttribute( ruleDescr.getAttributes() );
            String lhs = "";
            if ( !ruleDescr.getLhs().getDescrs().isEmpty() ) {
                lhs = "\t when" + DrlDumper.eol + processDescrList( ruleDescr.getLhs().getDescrs() ) + DrlDumper.eol;
            } else {

                lhs = "\t when";
            }

            String rhs = ruleDescr.getConsequence();
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
        for ( final Iterator iterator = descr.iterator(); iterator.hasNext(); ) {

            final Object temp = iterator.next();
            visit( temp );
            descrString += this.template;
            descrString += " , ";
        }
        return descrString.substring( 0,
                                      descrString.length() - 2 );
    }

    private String processFieldConstraint(List list)  {
        String descrString = "";
        for ( final Iterator it = list.iterator(); it.hasNext(); ) {
            final Object temp = it.next();
            visit( temp );
            descrString += this.template;
        }
        return descrString;
    }    
    
    private String processDescrList(final List descr) {
        String descrString = "";
        for ( final Iterator it = descr.iterator(); it.hasNext(); ) {
            visit( it.next() );
            descrString += this.template;
            descrString += DrlDumper.eol;
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

    private String processAttribute(final List attributes) {

        String attributeList = "";
        for ( final Iterator it = attributes.iterator(); it.hasNext(); ) {
            final AttributeDescr attributeDescr = (AttributeDescr) it.next();
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

    private String processGlobalsMap(final Map globals) {
        String globalList = "";

        for ( final Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            final String key = (String) it.next();
            final String value = (String) globals.get( key );
            final String globalTemplate = "global " + value + " " + key + ";" + DrlDumper.eol;
            globalList += globalTemplate;
        }

        return globalList + DrlDumper.eol;
    }

    private String processImportsList(final List imports) {
        String importList = "";

        for ( final Iterator it = imports.iterator(); it.hasNext(); ) {
            final String importString = (String) it.next();
            final String importTemplate = "import " + importString + ";" + DrlDumper.eol;
            importList += importTemplate;
        }
        return importList + DrlDumper.eol;
    }

    private void appendDrlDump(final String temp) {
        this.drlDump.append( temp );
    }

}