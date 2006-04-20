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

public class ReturnValueDescr extends PatternDescr {
    private String   fieldName;
    private String   evaluator;
    private String   text;
    private String[] declarations;

    private String   classMethodName;

    public ReturnValueDescr(String fieldName,
                            String evaluator,
                            String text) {
        this.fieldName = fieldName;
        this.evaluator = evaluator;
        this.text = text;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public String getText() {
        return this.text;
    }

    public void setDeclarations(String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public String toString() {
        return "[ReturnValue: field=" + fieldName + "; evaluator=" + evaluator + "; text=" + text + "]";
    }
}