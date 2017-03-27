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

package org.kie.dmn.feel.parser.feel11;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.ScopeImpl;
import org.kie.dmn.feel.lang.types.SymbolTable;
import org.kie.dmn.feel.lang.types.VariableSymbol;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.UnknownVariableErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class ParserHelper {
    public static final Logger LOG = LoggerFactory.getLogger(ParserHelper.class);

    private FEELEventListenersManager eventsManager;
    private SymbolTable   symbols      = new SymbolTable();
    private Scope         currentScope = symbols.getGlobalScope();
    private Stack<String> currentName  = new Stack<>();
    private int dynamicResolution = 0;

    public ParserHelper() {
        this( null );
    }

    public ParserHelper(FEELEventListenersManager eventsManager) {
        this.eventsManager = eventsManager;
        // initial context is loaded
        currentName.push( Scope.LOCAL );
    }

    public SymbolTable getSymbolTable() {
        return symbols;
    }

    public void pushScope() {
        LOG.trace("pushScope()");
        currentScope = new ScopeImpl( currentName.peek(), currentScope );
    }

    public void popScope() {
        LOG.trace("popScope()");
        currentScope = currentScope.getParentScope();
    }

    public void pushName( String name ) {
        LOG.trace("pushName() {}", name);
        this.currentName.push( name );
    }

    public void pushName(ParserRuleContext ctx) {
        this.currentName.push( getOriginalText( ctx ) );
    }

    public void popName() {
        LOG.trace("popName()");
        this.currentName.pop();
    }

    public void recoverScope() {
        recoverScope( currentName.peek() );
    }

    public void recoverScope( String name ) {
        LOG.trace("[{}] recoverScope( name: {}) with currentScope: {}", this.currentScope.getName(), name, currentScope);
        Scope s = this.currentScope.getChildScopes().get( name );
        if( s != null ) {
            currentScope = s;
        } else { 
            Symbol resolved = this.currentScope.resolve(name);
            if ( resolved != null && resolved.getType() instanceof CompositeType ) {
                pushName(name);
                pushScope();
                CompositeType type = (CompositeType) resolved.getType();
                for ( Map.Entry<String, Type> f : type.getFields().entrySet() ) {
                    this.currentScope.define(new VariableSymbol( f.getKey(), f.getValue() ));
                }
                LOG.trace(".. PUSHED, scope name {} with symbols {}", this.currentName.peek(), this.currentScope.getSymbols());
            } else {
                pushScope();
            }  
        }
    }

    public void dismissScope() {
        LOG.trace("dismissScope()");
        popScope();
    }

    public void validateVariable( ParserRuleContext ctx, List<String> qn, String name ) {
        if( eventsManager != null && !isDynamicResolution() ) {
            if( this.currentScope.getChildScopes().get( name ) == null && this.currentScope.resolve( name ) == null ) {
                // report error
                FEELEventListenersManager.notifyListeners( eventsManager , () -> {
                    String varName = qn.stream().collect( Collectors.joining( "." ) );
                    return new UnknownVariableErrorEvent( FEELEvent.Severity.ERROR,
                                                          "Unknown variable '" + varName + "'",
                                                          ctx.getStart().getLine(),
                                                          ctx.getStart().getCharPositionInLine(),
                                                          varName );
                                                           }
                );
            }
        }
    }

    public boolean isDynamicResolution() {
        return dynamicResolution > 0;
    }

    public void disableDynamicResolution() {
        if( dynamicResolution > 0 ) {
            this.dynamicResolution--;
        }
    }

    public void enableDynamicResolution() {
        this.dynamicResolution++;
    }
    
    public void defineVariable(ParserRuleContext ctx) {
        defineVariable( getOriginalText( ctx ) );
    }

    public void defineVariable(String variable) {
        VariableSymbol var = new VariableSymbol( variable );
        this.currentScope.define( var );
    }
    
    public void defineVariable(String variable, Type type) {
        LOG.trace("defining custom type symbol.");
        VariableSymbol var = new VariableSymbol( variable, type );
        this.currentScope.define( var );
    }

    public void startVariable(Token t) {
        this.currentScope.start( t.getText() );
    }

    public boolean followUp(Token t, boolean isPredict) {
        boolean follow = this.currentScope.followUp( t.getText(), isPredict );
        return follow;
    }

    public String getOriginalText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        Interval interval = new Interval( a, b );
        return ctx.getStart().getInputStream().getText( interval );
    }

    public static List<Token> getAllTokens(
            ParseTree ctx,
            List<Token> tokens) {
        for ( int i = 0; i < ctx.getChildCount(); i++ ) {
            ParseTree child = ctx.getChild( i );
            if ( child instanceof TerminalNode ) {
                tokens.add( ((TerminalNode) child).getSymbol() );
            } else {
                getAllTokens( child, tokens );
            }
        }
        return tokens;
    }

    public static Type determineTypeFromClass( Class<?> clazz ) {
        if( clazz == null ) {
            return BuiltInType.UNKNOWN;
        } else if( Number.class.isAssignableFrom(clazz) ) {
            return BuiltInType.NUMBER;
        } else if( String.class.isAssignableFrom(clazz) ) {
            return BuiltInType.STRING;
        } else if( LocalDate.class.isAssignableFrom(clazz) ) {
            return BuiltInType.DATE;
        } else if( LocalTime.class.isAssignableFrom(clazz) || OffsetTime.class.isAssignableFrom(clazz) ) {
            return BuiltInType.TIME;
        } else if( ZonedDateTime.class.isAssignableFrom(clazz) || OffsetDateTime.class.isAssignableFrom(clazz) || LocalDateTime.class.isAssignableFrom(clazz) ) {
            return BuiltInType.DATE_TIME;
        } else if( Duration.class.isAssignableFrom(clazz) || Period.class.isAssignableFrom(clazz) ) {
            return BuiltInType.DURATION;
        } else if( Boolean.class.isAssignableFrom(clazz) ) {
            return BuiltInType.BOOLEAN;
        } else if( UnaryTest.class.isAssignableFrom(clazz) ) {
            return BuiltInType.UNARY_TEST;
        } else if( Range.class.isAssignableFrom(clazz) ) {
            return BuiltInType.RANGE;
        } else if( FEELFunction.class.isAssignableFrom(clazz) ) {
            return BuiltInType.FUNCTION;
        } else if( List.class.isAssignableFrom(clazz) ) {
            return BuiltInType.LIST;
        } else if( Map.class.isAssignableFrom(clazz) ) {     // TODO not so sure about this one..
            return BuiltInType.CONTEXT;
        } 
        return JavaBackedType.of( clazz ); 
    }

}
