/*
 * Copyright 2005 JBoss Inc
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

package org.drools.lang.descr;

/**
 * This represents a qualified identifier, like enums or subfield
 * access in variables like "$p.name". This is a constraint on a single 
 * field of a pattern. 
 * The "text" contains the content. 
 */
public class QualifiedIdentifierRestrictionDescr extends EvaluatorBasedRestrictionDescr {

    private static final long serialVersionUID = 510l;

    public QualifiedIdentifierRestrictionDescr(){
    }

    public QualifiedIdentifierRestrictionDescr(final String evaluator,
                                               final boolean isNegated,
                                               final String parameterText,
                                               final String text) {
        super( evaluator,
               isNegated,
               parameterText );
        this.setText( text );
    }

    public String toString() {
        return "[QualifiedIndentifierRestr: " + super.toString() + " " + this.getText() + " ]";
    }
}
