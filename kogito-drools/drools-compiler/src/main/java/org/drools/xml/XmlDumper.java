package org.drools.xml;

/*
 * 
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
 * This utility will take a AST of a rule package, and emit XML.
 * This can be used in porting from DRL to XML.
 * @author <a href="mailto:jayaramcs@gmail.com">Author Jayaram C S</a>
 */
public class XmlDumper extends ReflectiveVisitor
    implements
    PackageDescrDumper {

    private StringBuffer        xmlDump;
    private final static String eol = System.getProperty( "line.separator" );

    public synchronized String dump(final PackageDescr packageDescr) {
        this.xmlDump = new StringBuffer();
        visitPackageDescr( packageDescr );
        return this.xmlDump.toString();
    }

    public void visitAndDescr(AndDescr descr) {
        template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            template = "<and>" + processDescrList( descr.getDescrs() ) + "</and>";
        } else {
            template = "<and> </and>";
        }
    }

    public void visitAttributeDescr(AttributeDescr attributeDescr) {
        template = new String();
        template = "<rule-attribute name=\"" + attributeDescr.getName() + "\" value=\"" + attributeDescr.getValue() + "\" />" + eol;
    }

    public void visitBoundVariableDescr(BoundVariableDescr descr) {
        template = new String();
        template = "<bound-variable field-name=\"" + descr.getFieldName() + "\" evaluator=\"" + getEvaluator( descr.getEvaluator() ) + "\" identifier=\"" + descr.getIdentifier() + "\" />" + eol;
    }

    public void visitColumnDescr(ColumnDescr descr) {
        template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            if ( descr.getIdentifier() != null ) {
                template = "<column identifier=\"" + descr.getIdentifier() + "\" object-type=\"" + descr.getObjectType() + "\" >" + processDescrList( descr.getDescrs() ) + "</column>" + eol;
            } else {
                template = "<column object-type=\"" + descr.getObjectType() + "\" >" + processDescrList( descr.getDescrs() ) + "</column>" + eol;
            }
        } else {
            if ( descr.getIdentifier() != null ) {
                template = "<column identifier=\"" + descr.getIdentifier() + "\" object-type=\"" + descr.getObjectType() + "\" > </column>" + eol;
            } else {
                template = "<column object-type=\"" + descr.getObjectType() + "\" > </column>" + eol;
            }
        }

    }

    public void visitEvalDescr(EvalDescr descr) {
        template = new String();
        template = "<eval>" + descr.getText() + "</eval>" + eol;
    }

    public void visitExistsDescr(ExistsDescr descr) {
        template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            template = "<exists>" + processDescrList( descr.getDescrs() ) + "</exists>";
        } else {
            template = "<exists> </exists>";
        }
    }

    public void visitFieldBindingDescr(FieldBindingDescr descr) {
        template = new String();
        template = "<field-binding field-name=\"" + descr.getFieldName() + "\" identifier=\"" + descr.getIdentifier() + "\" />" + eol;
    }

    public void visitFunctionDescr(FunctionDescr functionDescr) {
        template = new String();
        String parameterTemplate = processParameters( functionDescr.getParameterNames(),
                                                      functionDescr.getParameterTypes() );

        template = "<function return-type=\"" + functionDescr.getReturnType() + "\" name=\"" + functionDescr.getName() + "\">" + eol + parameterTemplate + "<body>" + eol + functionDescr.getText() + eol + "</body>" + eol + "</function>" + eol;
    }

    public void visitLiteralDescr(LiteralDescr descr) {
        template = new String();
        template = "<literal field-name=\"" + descr.getFieldName() + "\" evaluator=\"" + getEvaluator( descr.getEvaluator() ) + "\" value=\"" + descr.getText() + "\" />" + eol;
    }

    public void visitNotDescr(NotDescr descr) {
        template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            template = "<not>" + processDescrList( descr.getDescrs() ) + "</not>";
        } else {
            template = "<not> </not>";
        }

    }

    public void visitOrDescr(OrDescr descr) {
        template = new String();
        if ( descr.getDescrs() != Collections.EMPTY_LIST ) {
            template = "<or>" + processDescrList( descr.getDescrs() ) + "</or>";
        } else {
            template = "<or> </or>";
        }
    }

    public void visitPackageDescr(PackageDescr packageDescr) {
        String packageName = packageDescr.getName();
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + eol + " <package name=\"" + packageName + "\"  " + eol + "\txmlns=\"http://drools.org/drools-3.0\" " + eol + "\txmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" " + eol
                           + "\txs:schemaLocation=\"http://drools.org/drools-3.0 drools-3.0.xsd\"> " + eol;
        appendXmlDump( xmlString );
        appendXmlDump( processImportsList( packageDescr.getImports() ) );
        appendXmlDump( processGlobalsMap( packageDescr.getGlobals() ) );
        appendXmlDump( processFunctionsList( packageDescr.getFunctions() ) );
        appendXmlDump( processRules( packageDescr.getRules() ) );
        appendXmlDump( "</package>" );
    }

    public void visitPredicateDescr(PredicateDescr descr) {
        template = new String();
        template = "<predicate field-name=\"" + descr.getFieldName() + "\" identifier=\"" + descr.getDeclaration() + "\" >" + descr.getText() + "</predicate>" + eol;

    }

    public void visitReturnValueDescr(ReturnValueDescr descr) {
        template = new String();
        template = "<return-value field-name=\"" + descr.getFieldName() + "\" evaluator=\"" + getEvaluator( descr.getEvaluator() ) + "\" >" + descr.getText() + "</return-value>" + eol;
    }

    public void visitQueryDescr(QueryDescr descr) {
        template = new String();
        template = "<query name=\"" + descr.getName() + "\">" + "<lhs>" + processDescrList( descr.getLhs().getDescrs() ) + "</lhs>" + "</query>";
    }

    private String template;

    private String processRules(List rules) {
        String ruleList = "";
        for ( Iterator iterator = rules.iterator(); iterator.hasNext(); ) {
            RuleDescr ruleDescr = (RuleDescr) iterator.next();
            String rule = "<rule name=\"" + ruleDescr.getName() + "\">" + eol;
            String attribute = processAttribute( ruleDescr.getAttributes() );
            String lhs = "";
            if ( ruleDescr.getLhs().getDescrs() != Collections.EMPTY_LIST ) {
                lhs = "<lhs>" + processDescrList( ruleDescr.getLhs().getDescrs() ) + "</lhs>";
            } else {

                lhs = "<lhs> </lhs>";
            }

            String rhs = "<rhs>" + ruleDescr.getConsequence() + "</rhs>" + eol;
            rule += attribute;
            rule += lhs;
            rule += rhs;
            rule += "</rule>";
            ruleList += rule;
        }

        return ruleList + eol;
    }

    private String processDescrList(List descr) {
        String descrString = "";
        for ( Iterator iterator = descr.iterator(); iterator.hasNext(); ) {
            visit( iterator.next() );
            descrString += template;
            descrString += eol;
        }
        return descrString + eol;
    }

    private String processFunctionsList(List functions) {
        String functionList = "";

        for ( Iterator iterator = functions.iterator(); iterator.hasNext(); ) {
            visit( iterator.next() );
            functionList += template;
        }

        return functionList + eol;
    }

    private String processAttribute(List attributes) {

        String attributeList = "";
        for ( Iterator iterator = attributes.iterator(); iterator.hasNext(); ) {
            AttributeDescr attributeDescr = (AttributeDescr) iterator.next();
            visit( attributeDescr );
            attributeList += template;
        }
        return attributeList + eol;
    }

    private String processParameters(List parameterNames,
                                     List parameterTypes) {
        String paramList = "";
        int i = 0;
        for ( Iterator iterator = parameterNames.iterator(); iterator.hasNext(); i++ ) {
            String paramName = (String) iterator.next();
            String paramType = (String) parameterTypes.get( i );
            String paramTemplate = "<parameter identifier=\"" + paramName + "\" type=\"" + paramType + "\" />" + eol;
            paramList += paramTemplate;
        }

        return paramList + eol;
    }

    private String processGlobalsMap(Map globals) {
        String globalList = "";
        for ( Iterator iterator = globals.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String) globals.get( key );
            String globalTemplate = "<global identifier=\"" + key + "\" type=\"" + value + "\" />" + eol;
            globalList += globalTemplate;
        }

        return globalList + eol;
    }

    private String processImportsList(List imports) {
        String importList = "";

        for ( Iterator iterator = imports.iterator(); iterator.hasNext(); ) {
            String importString = (String) iterator.next();
            String importTemplate = "<import name=\"" + importString + "\" /> " + eol;
            importList += importTemplate;
        }
        return importList + eol;
    }

    private void appendXmlDump(String temp) {
        xmlDump.append( temp );
    }

    private String getEvaluator(String eval) {

        eval = eval.replaceAll( "<",
                                "&lt;" );
        eval = eval.replaceAll( ">",
                                "&gt;" );
        return eval;
    }
}