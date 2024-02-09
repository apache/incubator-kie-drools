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
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.api.InformationItem;

public class TFunctionDefinition extends TExpression implements FunctionDefinition {

    @Deprecated
    public static final QName KIND_QNAME = new QName( KieDMNModelInstrumentedBase.URI_KIE, "kind" );

    @Deprecated
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

    @Override
    public List<InformationItem> getFormalParameter() {
        if ( formalParameter == null ) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression value) {
        this.expression = value;
    }

    /**
     * Align to DMN v1.2
     */
    @Override
    public FunctionKind getKind() {
        String kindValueOnV11 = this.getAdditionalAttributes().get(KIND_QNAME);
        if (kindValueOnV11 == null || kindValueOnV11.isEmpty()) {
            return FunctionKind.FEEL;
        } else {
            switch (kindValueOnV11) {
                case "J":
                    return FunctionKind.JAVA;
                case "P":
                    return FunctionKind.PMML;
                case "F":
                default:
                    return FunctionKind.FEEL;
            }
        }
    }

    /**
     * Align to DMN v1.2
     */
    @Override
    public void setKind(FunctionKind value) {
        this.getAdditionalAttributes().put(KIND_QNAME, value.value());
    }

}
