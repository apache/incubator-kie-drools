/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.lang.descr;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;


/**
 * This represents a literal node in the rule language. This is
 * a constraint on a single field of a pattern. 
 * The "text" contains the content. 
 */
public class LiteralRestrictionDescr extends EvaluatorBasedRestrictionDescr {
    public static final int   TYPE_NULL        = 1;
    public static final int   TYPE_NUMBER      = 2;
    public static final int   TYPE_STRING      = 3;
    public static final int   TYPE_BOOLEAN     = 4;

    private static final long serialVersionUID = 510l;
    private int               type;

    public LiteralRestrictionDescr(){
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final String text) {
        this( evaluator,
              false,
              (List<String>) null,
              text,
              TYPE_STRING );// default type is string if not specified
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final boolean isNegated,
                                   final String text) {
        this( evaluator,
              isNegated,
              (List<String>) null,
              text,
              TYPE_STRING );// default type is string if not specified
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final boolean isNegated,
                                   final String parameterText,
                                   final String text,
                                   final int type) {
        super( evaluator,
               isNegated,
               parameterText );
        this.setText( text );
        this.type = type;
    }

    public LiteralRestrictionDescr(final String evaluator,
                                   final boolean isNegated,
                                   final List<String> parameters,
                                   final String text,
                                   final int type) {
        super( evaluator,
               isNegated,
               parameters );
        this.setText( text );
        this.type = type;
    }

    public String toString() {
        return super.toString() + " " + this.getText();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        switch ( this.type ) {
            case TYPE_NUMBER :
                try {
                    // in the DRL, we always use US number formatting 
                    return DecimalFormat.getInstance(Locale.US).parse( this.getText() );
                } catch ( ParseException e ) {
                    // return String anyway
                    return this.getText();
                }
            case TYPE_BOOLEAN :
                return Boolean.valueOf( this.getText() );
            default :
                return this.getText();
        }
    }
}
