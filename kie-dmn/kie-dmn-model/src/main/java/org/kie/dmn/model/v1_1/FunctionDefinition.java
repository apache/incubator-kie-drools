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
package org.kie.dmn.model.v1_1;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class FunctionDefinition extends Expression {

    public static final QName KIND_QNAME = new QName( DMNModelInstrumentedBase.URI_KIE, "kind" );

    public static enum Kind {
        FEEL("F"), JAVA("J"), PMML("P");

        public final String code;
        Kind( String code ) {
            this.code = code;
        }

        public static Kind determineFromString( String code ) {
            if ( code == null ) {
                return null;
            } else if ( FEEL.code.equals( code ) ) {
                return FEEL;
            } else if( JAVA.code.equals( code ) ) {
                return JAVA;
            } else if( PMML.code.equals( code ) ) {
                return PMML;
            }
            return null;
        }
    }

    private List<InformationItem> formalParameter;
    private Expression expression;

    public List<InformationItem> getFormalParameter() {
        if ( formalParameter == null ) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression( final Expression value ) {
        this.expression = value;
    }

}
