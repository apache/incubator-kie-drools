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



public class PredicateDescr extends PatternDescr {
    private final String fieldName;
    private final String text;
    
    private final String declaration;
    private String[] declarations;
    
    private String classMethodName;
        
    public PredicateDescr(String fieldName,
                          String declaration,
                          String text) {
        this.fieldName = fieldName;
        this.declaration = declaration;
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
    
    public String getText() {
        return this.text;
    } 
    
    public String getDeclaration() {
        return this.declaration;
    }
    
    public void setDeclarations( String[] declarations) {
        this.declarations = declarations;
    }
    
    public String[] getDeclarations() {
        return this.declarations;
    }
}