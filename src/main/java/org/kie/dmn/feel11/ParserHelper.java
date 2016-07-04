/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel11;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.lang.Scope;
import org.kie.dmn.lang.types.LocalScope;
import org.kie.dmn.lang.types.SymbolTable;
import org.kie.dmn.lang.types.VariableSymbol;
import org.kie.dmn.util.TokenTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserHelper {

    private static final String GLOBAL = "<global>";
    private static final String LOCAL  = "<local>";

    private SymbolTable   symbols      = new SymbolTable();
    private Scope         currentScope = new LocalScope( GLOBAL, symbols.getBuiltInScope() );
    private Stack<String> currentName  = new Stack<>();
    private TokenTree     tokenTree    = new TokenTree();

    public ParserHelper() {
        // initial context is loaded
        currentName.push( LOCAL );
    }

    public SymbolTable getSymbolTable() {
        return symbols;
    }

    public void pushScope() {
        currentScope = new LocalScope( currentName.peek(), currentScope );
        System.out.println( "Pushing scope: " + currentScope );
    }

    public void popScope() {
        System.out.println( "Popping scope: " + currentScope );
        currentScope = currentScope.getParentScope();
    }

    public void pushName(ParserRuleContext ctx) {
        this.currentName.push( getOriginalText( ctx ) );
    }

    public void popName() {
        this.currentName.pop();
    }

    public void defineVariable(ParserRuleContext ctx) {
        VariableSymbol var = new VariableSymbol( getOriginalText( ctx ) );
        System.out.println( " --> var = " + var.getId() );
        tokenTree.addName( getAllTokens( ctx, new ArrayList<>(  ) ) );
        this.currentScope.define( var );
    }

    public void startVariable( Token t ) {
        this.tokenTree.start( t );
    }

    public boolean followUp( Token t ) {
        if( t.getText().equals( "decision" ) | t.getText().equals( "table" ) ) {
            return true;
        }
        return this.tokenTree.followUp( t );
    }

    private String getOriginalText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        Interval interval = new Interval( a, b );
        return ctx.getStart().getInputStream().getText( interval );
    }

    private List<Token> getAllTokens( ParseTree ctx, List<Token> tokens ) {
        for( int i = 0; i < ctx.getChildCount(); i++ ) {
            ParseTree child = ctx.getChild( i );
            if( child instanceof TerminalNode ) {
                tokens.add( ((TerminalNode) child).getSymbol() );
            } else {
                getAllTokens( child, tokens );
            }
        }
        return tokens;
    }

}
