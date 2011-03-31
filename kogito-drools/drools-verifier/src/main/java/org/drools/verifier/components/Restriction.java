/*
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

import org.drools.base.evaluators.Operator;
import org.drools.verifier.report.components.Cause;

public abstract class Restriction extends PatternComponent
        implements
        Cause {

    public static class RestrictionType {
        public static final RestrictionType LITERAL = new RestrictionType("LITERAL");
        public static final RestrictionType VARIABLE = new RestrictionType("VARIABLE");
        public static final RestrictionType QUALIFIED_IDENTIFIER = new RestrictionType("QUALIFIED_IDENTIFIER");
        public static final RestrictionType RETURN_VALUE_RESTRICTION = new RestrictionType("RETURN_VALUE_RESTRICTION");
        public static final RestrictionType ENUM = new RestrictionType("ENUM");

        protected final String type;

        private RestrictionType(String t) {
            type = t;
        }
    }

    private boolean patternIsNot;

    // Id of the field that this restriction is related to.
    private String fieldPath;

    protected Operator operator;

    public abstract RestrictionType getRestrictionType();

    public Restriction(Pattern pattern) {
        super(pattern);
    }

    @Override
    public String getPath() {
        return String.format("%s/restriction[%s]",
                getParentPath(),
                getOrderNumber());
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RESTRICTION;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String path) {
        this.fieldPath = path;
    }

    public boolean isPatternIsNot() {
        return patternIsNot;
    }

    public void setPatternIsNot(boolean patternIsNot) {
        this.patternIsNot = patternIsNot;
    }
}
