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

import org.drools.lang.descr.*;

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
    private static final String eol     = System.getProperty( "line.separator" );

    public synchronized String dump(final PackageDescr packageDescr) {
        this.drlDump = new StringBuffer();
        visitPackageDescr( packageDescr );
        return this.drlDump.toString();
    }

    public void visitAndDescr(final AndDescr descr) {
        this.template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            this.template = processDescrList( descr.getDescrs() );
        } else {
            this.template = "";
        }
    }

    public void visitAttributeDescr(final AttributeDescr attributeDescr) {
        this.template = new String();
        this.template = "\t " + attributeDescr.getName() + " " + attributeDescr.getValue() + this.eol;
    }

    public void visitBoundVariableDescr(final BoundVariableDescr descr) {
        this.template = new String();
        this.template = descr.getFieldName() + " " + descr.getEvaluator() + " " + descr.getIdentifier();
    }

    public void visitColumnDescr(final ColumnDescr descr) {
        this.template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
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
        this.template = "\t\teval ( " + descr.getText() + " )" + this.eol;
    }

    public void visitExistsDescr(final ExistsDescr descr) {
        this.template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            this.template = "\t\texists " + processDescrList( descr.getDescrs() );
        } else {
            this.template = "";
        }
    }

    public void visitFieldBindingDescr(final FieldBindingDescr descr) {
        this.template = new String();
        this.template = descr.getIdentifier() + " : ";
    }

    public void visitFunctionDescr(final FunctionDescr functionDescr) {
        this.template = new String();
        final String parameterTemplate = processParameters( functionDescr.getParameterNames(),
                                                            functionDescr.getParameterTypes() );

        this.template = "function " + functionDescr.getReturnType() + " " + functionDescr.getName() + "(" + parameterTemplate + "){" +

        functionDescr.getText() + this.eol + "}" + this.eol;

    }

    public void visitLiteralDescr(final LiteralDescr descr) {
        this.template = new String();
        String text = descr.getText();
        try {
            Integer.parseInt( text );
        } catch ( final NumberFormatException e ) {
            text = "\"" + text + "\"";
        }

        this.template = descr.getFieldName() + " " + descr.getEvaluator() + " " + text;
    }

    public void visitNotDescr(final NotDescr descr) {
        this.template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            this.template = "\t   not " + processDescrList( descr.getDescrs() );
        } else {
            this.template = "";
        }

    }

    public void visitOrDescr(final OrDescr descr) {
        this.template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            this.template = processOrDescrList( descr.getDescrs() );
        } else {
            this.template = " ";
        }
    }

    public void visitPackageDescr(final PackageDescr packageDescr) {
        final String packageName = packageDescr.getName();
        final String xmlString = "package " + packageName + ";" + this.eol + this.eol;

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
        this.template = descr.getDeclaration() + ":" + descr.getFieldName() + " -> ( " + descr.getText() + " )";

    }

    public void visitReturnValueDescr(final ReturnValueDescr descr) {
        this.template = new String();
        this.template = descr.getFieldName() + " " + descr.getEvaluator() + " ( " + descr.getText() + ")";
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
            String rule = "rule \"" + ruleDescr.getName() + "\" " + this.eol;
            final String attribute = processAttribute( ruleDescr.getAttributes() );
            String lhs = "";
            if ( ruleDescr.getLhs().getDescrs() != Collections.EMPTY_LIST ) {
                lhs = "\t when" + this.eol + processDescrList( ruleDescr.getLhs().getDescrs() ) + this.eol;
            } else {

                lhs = "\t when";
            }

            String rhs = ruleDescr.getConsequence();
            if ( rhs == null ) {
                rhs = "\t then" + this.eol + "\t";
            } else {
                rhs = "\t then" + this.eol + "\t\t" + ruleDescr.getConsequence();
            }

            rule += attribute;
            rule += lhs;
            rule += rhs;
            rule += "end" + this.eol;
            ruleList += rule;
        }

        return ruleList + this.eol;
    }

    private String processOrDescrList(final List descr) {
        String descrString = "";
        for ( final Iterator iterator = descr.iterator(); iterator.hasNext(); ) {
            visit( iterator.next() );
            descrString += this.template;
            if ( descrString.endsWith( this.eol ) ) {
                descrString = descrString.substring( 0,
                                                     descrString.indexOf( this.eol ) );
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
            if ( !(temp instanceof FieldBindingDescr) ) {
                descrString += " , ";
            }

        }
        return descrString.substring( 0,
                                      descrString.length() - 2 );
    }

    private String processDescrList(final List descr) {
        String descrString = "";
        for ( final Iterator iterator = descr.iterator(); iterator.hasNext(); ) {
            visit( iterator.next() );
            descrString += this.template;
            descrString += this.eol;
        }
        return descrString;
    }

    private String processFunctionsList(final List functions) {
        String functionList = "";

        for ( final Iterator iterator = functions.iterator(); iterator.hasNext(); ) {
            visit( iterator.next() );
            functionList += this.template;
        }

        return functionList + this.eol;
    }

    private String processAttribute(final List attributes) {

        String attributeList = "";
        for ( final Iterator iterator = attributes.iterator(); iterator.hasNext(); ) {
            final AttributeDescr attributeDescr = (AttributeDescr) iterator.next();
            visit( attributeDescr );
            attributeList += this.template;
        }
        return attributeList;
    }

    private String processParameters(final List parameterNames,
                                     final List parameterTypes) {
        String paramList = "";
        int i = 0;
        for ( final Iterator iterator = parameterNames.iterator(); iterator.hasNext(); i++ ) {
            final String paramName = (String) iterator.next();
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

        for ( final Iterator iterator = globals.keySet().iterator(); iterator.hasNext(); ) {
            final String key = (String) iterator.next();
            final String value = (String) globals.get( key );
            final String globalTemplate = "global " + value + " " + key + ";" + this.eol;
            globalList += globalTemplate;
        }

        return globalList + this.eol;
    }

    private String processImportsList(final List imports) {
        String importList = "";

        for ( final Iterator iterator = imports.iterator(); iterator.hasNext(); ) {
            final String importString = (String) iterator.next();
            final String importTemplate = "import " + importString + ";" + this.eol;
            importList += importTemplate;
        }
        return importList + this.eol;
    }

    private void appendDrlDump(final String temp) {
        this.drlDump.append( temp );
    }

}