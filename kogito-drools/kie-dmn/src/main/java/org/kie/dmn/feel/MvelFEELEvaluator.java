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

package org.kie.dmn.feel;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.kie.dmn.grammar.FEEL_1_1BaseListener;
import org.kie.dmn.grammar.FEEL_1_1Parser;
import org.mvel2.MVEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MvelFEELEvaluator
        extends FEEL_1_1BaseListener {
    ParseTreeProperty<String>  ptp = new ParseTreeProperty<String>();
    Map<String, Object> ass = new HashMap<String, Object>();

    public String getMVEL(ParseTree ctx) {
        return ptp.get( ctx );
    }

    public void setMVEL(ParseTree ctx, String s) {
        ptp.put( ctx, s );
    }

    public Object evaluate(ParseTree ctx, Map<String, Object> vars) {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk( this, ctx );
        String expr = getMVEL( ctx );
        System.out.println(expr);
        return MVEL.eval( expr, vars );
    }

    @Override
    public void exitExpression(FEEL_1_1Parser.ExpressionContext ctx) {
        setMVEL( ctx, getMVEL( ctx.conditionalOrExpression() ) );
    }

    @Override
    public void exitValueList(FEEL_1_1Parser.ValueListContext ctx) {
        List<String> vals = (List<String>) ass.get( "vl" );
        if ( vals == null ) {
            vals = new ArrayList<String>();
            ass.put( "vl", vals );
        }
        vals.add( ctx.additiveExpression().getText() );
    }

    @Override
    public void exitRelationalExpression_valueList(FEEL_1_1Parser.RelationalExpression_valueListContext ctx) {
        StringBuilder buf = new StringBuilder();
        String lhs = getMVEL( ctx.relationalExpression() );
        if( lhs == null ) {
            lhs = ctx.relationalExpression().getText();
        }
        List<String> vl = (List<String>) ass.remove( "vl" );
        boolean first = true;
        for( String s : vl ) {
            if( !first ) {
                buf.append( "||" );
            }
            first=false;
            buf.append( "(" )
                    .append( lhs )
                    .append( ") == (" )
                    .append( s )
                    .append( ")" );
        }
        setMVEL( ctx, buf.toString() );
    }

    @Override
    public void exitRelationalExpression_unaryList(FEEL_1_1Parser.RelationalExpression_unaryListContext ctx) {
        StringBuilder buf = new StringBuilder();
        String lhs = getMVEL( ctx.relationalExpression() );
        if( lhs == null ) {
            lhs = ctx.relationalExpression().getText();
        }
        String[] list = ctx.simplePositiveUnaryTests().getText().split( "," );
        boolean first = true;
        for( String s : list ) {
            if( !first ) {
                buf.append( "||" );
            }
            first=false;
            buf.append( "(" )
                    .append( lhs )
                    .append( "null".equals( s ) ? "=="+s : s )
                    .append( ")" );
        }
        setMVEL( ctx, buf.toString() );
    }

    @Override
    public void exitRelationalExpression_unary(FEEL_1_1Parser.RelationalExpression_unaryContext ctx) {
        String lhs = getMVEL( ctx.relationalExpression() );
        if( lhs == null ) {
            lhs = ctx.relationalExpression().getText();
        }
        setMVEL( ctx, lhs + ctx.simplePositiveUnaryTest().getText() );
    }

    @Override
    public void exitEqualityExpression(FEEL_1_1Parser.EqualityExpressionContext ctx) {
        String re = getMVEL( ctx.relationalExpression() );
        if( re == null ) {
            re = ctx.relationalExpression().getText();
        }
        if( ctx.getChildCount() == 3 ) {
            setMVEL( ctx, getMVEL( ctx.equalityExpression() ) + ctx.getChild( 1 ).getText() + re );
        } else {
            setMVEL( ctx, re );
        }
    }

    @Override
    public void exitConditionalAndExpression(FEEL_1_1Parser.ConditionalAndExpressionContext ctx) {
        if( ctx.getChildCount() == 3 ) {
            setMVEL( ctx, getMVEL( ctx.conditionalAndExpression() ) + ctx.getChild( 1 ).getText() + getMVEL( ctx.equalityExpression() ) );
        } else {
            setMVEL( ctx, getMVEL( ctx.equalityExpression() ) );
        }
    }

    @Override
    public void exitConditionalOrExpression(FEEL_1_1Parser.ConditionalOrExpressionContext ctx) {
        if( ctx.getChildCount() == 3 ) {
            setMVEL( ctx, getMVEL( ctx.conditionalOrExpression() ) + ctx.getChild( 1 ).getText() + getMVEL( ctx.conditionalAndExpression() ) );
        } else {
            setMVEL( ctx, getMVEL( ctx.conditionalAndExpression() ) );
        }
    }
}
