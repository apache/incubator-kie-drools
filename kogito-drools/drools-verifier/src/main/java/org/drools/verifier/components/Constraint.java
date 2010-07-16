/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

/**
 *
 * @author Toni Rikkola
 */
public class Constraint extends PatternComponent
    implements
    Cause {

    private boolean patternIsNot;
    private String  fieldPath;
    private String  fieldName;
    private int     lineNumber;

    public Constraint(Pattern pattern) {
        super( pattern );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.CONSTRAINT;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean isPatternIsNot() {
        return patternIsNot;
    }

    public void setPatternIsNot(boolean patternIsNot) {
        this.patternIsNot = patternIsNot;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "Constraint field name: " + fieldName;
    }

    public void setFieldPath(String path) {
        this.fieldPath = path;
    }

    public String getFieldPath() {
        return fieldPath;
    }
}
