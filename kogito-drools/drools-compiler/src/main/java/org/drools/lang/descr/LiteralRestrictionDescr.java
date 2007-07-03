package org.drools.lang.descr;

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

/**
 * This represents a literal node in the rule language. This is
 * a constraint on a single field of a pattern. 
 * The "text" contains the content. 
 */
public class LiteralRestrictionDescr extends RestrictionDescr {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    private String            evaluator;
    private String            text;

    public LiteralRestrictionDescr(final String evaluator,
                                   final String text) {
        this.text = text;
        this.evaluator = evaluator;
    }

    public String getEvaluator() {
        return this.evaluator;
    }

    public String getText() {
        return this.text;
    }

    public String toString() {
        return this.evaluator + " " + this.text;
    }
}