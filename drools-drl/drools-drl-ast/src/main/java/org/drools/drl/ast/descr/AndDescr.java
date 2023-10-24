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
package org.drools.drl.ast.descr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AndDescr extends AnnotatedBaseDescr
    implements
    ConditionalElementDescr {
    private static final long serialVersionUID = 510l;
    private List<BaseDescr>    descrs           = new ArrayList<>();

    public AndDescr() { }

    private AndDescr(BaseDescr baseDescr) {
        addDescr(baseDescr);
    }

    public void addDescr(final BaseDescr baseDescr) {
        this.descrs.add( baseDescr );
    }

    public void insertDescr(int index,
                            final BaseDescr baseDescr) {
        this.descrs.add( index,
                         baseDescr );
    }

    public void insertBeforeLast(final Class<?> clazz,
                             final BaseDescr baseDescr) {
        if ( this.descrs.isEmpty() ) {
            addDescr( baseDescr );
            return;
        }

        for ( int i = this.descrs.size() - 1; i >= 0; i-- ) {
            if ( clazz.isInstance( this.descrs.get( i ) ) ) {
                insertDescr( i,
                             baseDescr );
                return;
            }
        }
        
        addDescr( baseDescr );
    }

    public List<BaseDescr> getDescrs() {
        return this.descrs;
    }

    public List<PatternDescr> getAllPatternDescr() {
        List<PatternDescr> patterns = new ArrayList<>();
        getAllPatternDescr(this, patterns);
        return patterns;
    }

    private void getAllPatternDescr(ConditionalElementDescr elementDescr, List<PatternDescr> patterns) {
        for (BaseDescr base : elementDescr.getDescrs()) {
            if (base instanceof PatternDescr) {
                patterns.add( ( (PatternDescr) base ));
            } else if (base instanceof ConditionalElementDescr) {
                getAllPatternDescr( ( (ConditionalElementDescr) base ), patterns);
            }
        }
    }

    public void addOrMerge(final BaseDescr baseDescr) {
        if ( baseDescr instanceof AndDescr ) {
            AndDescr and = (AndDescr) baseDescr;
            for( BaseDescr descr : and.getDescrs() ) {
                addDescr( descr );
            }
            for ( String annKey : and.getAnnotationNames() ) {
                addAnnotation(and.getAnnotation(annKey));
            }
        } else {
            addDescr( baseDescr );
        }
    }

    public boolean removeDescr(BaseDescr baseDescr) {
        return baseDescr == null ? false : descrs.remove(baseDescr);
    }

    public String toString() {
        return "[AND "+descrs+" ]";
    }

    public void accept(DescrVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public BaseDescr negate() {
        if (descrs.isEmpty()) {
            return new AndDescr(new ExprConstraintDescr( "false" ));
        }

        if (descrs.size() == 1) {
            BaseDescr baseDescr = descrs.get(0);
            if (!(baseDescr instanceof ExprConstraintDescr) || baseDescr.getText().contains("||") || !baseDescr.getText().contains("&&")) {
                return new AndDescr(baseDescr.negate());
            }
        }

        boolean allExprs = descrs.stream().allMatch( ExprConstraintDescr.class::isInstance );
        if (allExprs) {
            String expr = descrs.stream()
                    .map( ExprConstraintDescr.class::cast )
                    .map( ExprConstraintDescr::getText )
                    .map( this::removeEnclosingParenthesis )
                    .flatMap( e -> Stream.of(e.split("&&")))
                    .map( e -> "!(" + e + ")" )
                    .collect( Collectors.joining( "||" ) );
            return new AndDescr( new ExprConstraintDescr(expr) );
        }

        OrDescr or = new OrDescr();
        for (BaseDescr descr : descrs) {
            or.addDescr( descr.negate() );
        }
        return or;
    }

    private String removeEnclosingParenthesis(String expr) {
        expr = expr.trim();
        return expr.startsWith("(") && expr.endsWith(")") ? removeEnclosingParenthesis(expr.substring(1, expr.length()-1)) : expr;
    }
}
