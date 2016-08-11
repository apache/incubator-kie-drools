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

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.runtime.UnaryTest;

public class UnaryTestNode
        extends BaseNode {

    private UnaryOperator operator;
    private BaseNode      value;

    public static enum UnaryOperator {
        LTE( "<=" ),
        LT( "<" ),
        GT( ">" ),
        GTE( ">=" );

        public final String symbol;

        UnaryOperator(String symbol) {
            this.symbol = symbol;
        }

        public static UnaryOperator determineOperator(String symbol) {
            for ( UnaryOperator op : UnaryOperator.values() ) {
                if ( op.symbol.equals( symbol ) ) {
                    return op;
                }
            }
            throw new IllegalArgumentException( "No operator found for symbol '" + symbol + "'" );
        }
    }

    public UnaryTestNode(ParserRuleContext ctx, String op, BaseNode value) {
        super( ctx );
        this.operator = UnaryOperator.determineOperator( op );
        this.value = value;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public void setOperator(UnaryOperator operator) {
        this.operator = operator;
    }

    public BaseNode getValue() {
        return value;
    }

    public void setValue(BaseNode value) {
        this.value = value;
    }

    @Override
    public UnaryTest evaluate(EvaluationContext ctx) {
        Comparable val = (Comparable) value.evaluate( ctx );
        switch ( operator ) {
            case LTE:
                return o -> o == null || val == null ? null : ((Comparable) o).compareTo( val ) <= 0;
            case LT:
                return o -> o == null || val == null ? null : ((Comparable) o).compareTo( val ) < 0;
            case GT:
                return o -> o == null || val == null ? null : ((Comparable) o).compareTo( val ) > 0;
            case GTE:
                return o -> o == null || val == null ? null : ((Comparable) o).compareTo( val ) >= 0;
        }
        return null;
    }
}
