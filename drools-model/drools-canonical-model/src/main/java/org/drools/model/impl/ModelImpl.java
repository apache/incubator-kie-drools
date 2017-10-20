/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.impl;

import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.TypeMetaData;

import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
    private List<Rule> rules = new ArrayList<>();
    private List<Query> queries = new ArrayList<>();
    private List<Global> globals = new ArrayList<>();
    private List<TypeMetaData> typeMetaDatas = new ArrayList<>();

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public List<Global> getGlobals() {
        return globals;
    }

    @Override
    public List<Query> getQueries() {
        return queries;
    }

    @Override
    public List<TypeMetaData> getTypeMetaDatas() {
        return typeMetaDatas;
    }

    public ModelImpl withRules( List<Rule> rules ) {
        this.rules = rules;
        return this;
    }

    public ModelImpl addTypeMetaData( TypeMetaData typeMetaData ) {
        this.typeMetaDatas.add(typeMetaData);
        return this;
    }

    public ModelImpl addRule( Rule rule ) {
        this.rules.add(rule);
        return this;
    }

    public ModelImpl addQuery( Query query ) {
        this.queries.add(query);
        return this;
    }

    public ModelImpl withQueries( List<Query> queries ) {
        this.queries = queries;
        return this;
    }

    public ModelImpl withGlobals( List<Global> globals ) {
        this.globals = globals;
        return this;
    }

    public ModelImpl addGlobal( Global global ) {
        this.globals.add(global);
        return this;
    }
}
