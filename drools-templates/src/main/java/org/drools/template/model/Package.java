/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.template.model;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the top of the parse tree. Represents a package of rules once it has
 * been parsed from the spreadsheet. Also is the launching point for dumping out
 * the DRL.
 */
public class Package extends AttributedDRLElement
        implements
        DRLJavaEmitter {

    private String name;

    private List<Import> imports;

    private List<Global> variables;    // List of the application data Variable Objects

    private List<Rule> rules;

    private Functions functions;

    private Queries queries;

    private DeclaredType declaredTypes;

    private String ruleUnit;

    private String dialect;

    public Package(final String name) {
        this.name = name;
        this.imports = new LinkedList<>();
        this.variables = new LinkedList<>();
        this.rules = new LinkedList<>();
        this.functions = new Functions();
        this.queries = new Queries();
        this.declaredTypes = new DeclaredType();
    }

    public void addImport(final Import imp) {
        this.imports.add(imp);
    }

    public void addVariable(final Global varz) {
        this.variables.add(varz);
    }

    public void addRule(final Rule rule) {
        this.rules.add(rule);
    }

    public void addFunctions(final String listing) {
        this.functions.setFunctionsListing(listing);
    }

    public void addQueries(final String listing) {
        this.queries.setQueriesListing(listing);
    }

    public void addDeclaredType(final String declaration) {
        this.declaredTypes.setDeclaredTypeListing(declaration);
    }

    public String getName() {
        return this.name;
    }

    public List<Import> getImports() {
        return this.imports;
    }

    public List<Global> getVariables() {
        return this.variables;
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setRuleUnit( String ruleUnit ) {
        this.ruleUnit = ruleUnit;
    }

    public void setDialect( String dialect ) {
        this.dialect = dialect;
    }

    public void renderDRL( final DRLOutput out) {
        if ( name != null) {
            out.writeLine("package " + name.replace(' ', '_') + ";");
        }
        if ( ruleUnit != null) {
            out.writeLine("unit " + ruleUnit + ";");
        }
        if ( dialect != null) {
            out.writeLine("dialect \"" + dialect + "\"");
        }
        out.writeLine("//generated from Decision Table");

        renderDRL(imports, out);
        renderDRL(variables, out);
        functions.renderDRL(out);
        queries.renderDRL(out);
        declaredTypes.renderDRL(out);

        // attributes
        super.renderDRL(out);

        renderDRL(rules, out);
    }

    private void renderDRL(final List<? extends DRLJavaEmitter> list,
                           final DRLOutput out) {
        for (DRLJavaEmitter emitter : list) {
            emitter.renderDRL(out);
        }
    }

}
