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
package org.kie.dmn.feel.lang.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Lexer;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.StringEvalHelper;
import org.kie.dmn.feel.util.TokenTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeImpl
        implements Scope {
    public static final Logger LOG = LoggerFactory.getLogger(ScopeImpl.class);

    private String name;
    private Scope  parentScope;

    private Map<String, Symbol> symbols     = new LinkedHashMap<>();
    private Map<String, Scope>  childScopes = new LinkedHashMap<>();

    private TokenTree tokenTree;

    private Type type;

    public ScopeImpl() {
    }

    public ScopeImpl(String name, Scope parentScope) {
        this.name = name;
        this.parentScope = parentScope;
        if ( parentScope != null ) {
            parentScope.addChildScope( this );
        }
    }

    public ScopeImpl(String name, Scope parentScope, Type type) {
        this(name, parentScope);
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Scope getParentScope() {
        return parentScope;
    }

    public boolean define(Symbol symbol) {
        if ( symbols.containsKey( symbol.getId() ) ) {
            // duplicate symbol definition
            return false;
        }
        symbols.put(StringEvalHelper.normalizeVariableName(symbol.getId() ), symbol );
        if( tokenTree != null ) {
            // also load the symbol into the token tree
            tokenTree.addName( tokenize( symbol.getId() ) );
        }
        return true;
    }

    public Symbol resolve(String id) {
        Symbol s = symbols.get( StringEvalHelper.normalizeVariableName( id ) );
        if (s == null && getParentScope() != null) {
            return getParentScope().resolve(id);
        }
        return s;
    }

    public Symbol resolve(String[] qualifiedName) {
        Symbol root = symbols.get( StringEvalHelper.normalizeVariableName( qualifiedName[0] ) );
        if (root == null && getParentScope() != null) {
            return getParentScope().resolve(qualifiedName);
        } else if( root != null ) {
            Symbol currentSymbol = root;
            for( int i = 1; i < qualifiedName.length && currentSymbol != null; i++ ) {
                currentSymbol = currentSymbol.getScope().resolve( StringEvalHelper.normalizeVariableName( qualifiedName[i] ) );
            }
            return currentSymbol;
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentScope(Scope parentScope) {
        this.parentScope = parentScope;
    }

    public void addChildScope( Scope scope ) {
        this.childScopes.put( scope.getName(), scope );
    }

    public Map<String, Scope> getChildScopes() {
        return childScopes;
    }

    public void setChildScopes(Map<String, Scope> childScopes) {
        this.childScopes = childScopes;
    }
    
    @Override
    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void start( String token ) {
        LOG.trace("[{}]: start() {}", name, token);
        if( tokenTree == null ) {
            initializeTokenTree();
        }
        this.tokenTree.start( token );
        if (this.getParentScope() != null) {
            this.getParentScope().start(token);
        }
    }

    public boolean followUp( String token, boolean isPredict ) {
        LOG.trace("[{}]: followUp() {}", name, token);
        // must call followup on parent scope
        boolean parent = this.getParentScope() != null && this.getParentScope().followUp(token, isPredict);
        return this.tokenTree.followUp( token, !isPredict ) || parent;
    }

    private void initializeTokenTree() {
        LOG.trace("[]: initializeTokenTree()");
        tokenTree = tokenTreeFromSymbols(getSymbols());
    }

    public static TokenTree tokenTreeFromSymbols(Map<String, Symbol> symbols) {
        TokenTree tt = new TokenTree();
        for( String symbol : symbols.keySet() ) {
            List<String> tokens = tokenize( symbol );
            tt.addName(tokens);
        }
        return tt;
    }

    private static List<String> tokenize(String symbol) {
        CharStream input = CharStreams.fromString(symbol);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        List<String> tokens = new ArrayList<>(  );

        for (Token token = lexer.nextToken();
             token.getType() != Token.EOF;
             token = lexer.nextToken()) {
            tokens.add( token.getText() );
        }
        return tokens;
    }

    @Override
    public String toString() {
        return "Scope{" +
               " name='" + name + '\'' +
               ", parentScope='" + (getParentScope() != null ? getParentScope().getName() : "<null>") +
               "' }";
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
