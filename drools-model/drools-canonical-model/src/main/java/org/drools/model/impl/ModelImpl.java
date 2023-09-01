package org.drools.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.drools.model.EntryPoint;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.TypeMetaData;

public class ModelImpl implements Model {

    private final String name;
    private List<Rule> rules = new ArrayList<>();
    private List<Query> queries = new ArrayList<>();
    private List<Global> globals = new ArrayList<>();
    private List<TypeMetaData> typeMetaDatas = new ArrayList<>();

    public ModelImpl() {
        this( UUID.randomUUID().toString() );
    }

    public ModelImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

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

    @Override
    public List<EntryPoint> getEntryPoints() {
        return Collections.emptyList();
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
