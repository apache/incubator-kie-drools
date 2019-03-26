/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

    private String _name;

    private List<Import> _imports;

    private List<Global> _variables;    // List of the application data Variable Objects

    private List<Rule> _rules;

    private Functions _functions;

    private Queries _queries;

    private DeclaredType _declaredTypes;

    public Package(final String name) {
        this._name = name;
        this._imports = new LinkedList<Import>();
        this._variables = new LinkedList<Global>();
        this._rules = new LinkedList<Rule>();
        this._functions = new Functions();
        this._queries = new Queries();
        this._declaredTypes = new DeclaredType();
    }

    public void addImport(final Import imp) {
        this._imports.add(imp);
    }

    public void addVariable(final Global varz) {
        this._variables.add(varz);
    }

    public void addRule(final Rule rule) {
        this._rules.add(rule);
    }

    public void addFunctions(final String listing) {
        this._functions.setFunctionsListing(listing);
    }

    public void addQueries(final String listing) {
        this._queries.setQueriesListing(listing);
    }

    public void addDeclaredType(final String declaration) {
        this._declaredTypes.setDeclaredTypeListing(declaration);
    }

    public String getName() {
        return this._name;
    }

    public List<Import> getImports() {
        return this._imports;
    }

    public List<Global> getVariables() {
        return this._variables;
    }

    public List<Rule> getRules() {
        return this._rules;
    }

    public void renderDRL(final DRLOutput out) {
        if (_name != null) {
            out.writeLine("package " + this._name.replace(' ',
                                                          '_') + ";");
        }
        out.writeLine("//generated from Decision Table");
        renderDRL(this._imports,
                  out);
        renderDRL(this._variables,
                  out);
        this._functions.renderDRL(out);
        this._queries.renderDRL(out);
        this._declaredTypes.renderDRL(out);

        // attributes
        super.renderDRL(out);

        renderDRL(this._rules,
                  out);

    }

    private void renderDRL(final List<? extends DRLJavaEmitter> list,
                           final DRLOutput out) {
        for (DRLJavaEmitter emitter : list) {
            emitter.renderDRL(out);
        }
    }

}
