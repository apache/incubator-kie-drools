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

import org.drools.verifier.report.components.Cause;

public class ReturnValueRestriction extends Restriction
    implements
    Cause {

    private Object   content;
    private String[] declarations;
    private String   classMethodName;

    public ReturnValueRestriction(Pattern pattern) {
        super( pattern );
    }

    @Override
    public RestrictionType getRestrictionType() {
        return RestrictionType.RETURN_VALUE_RESTRICTION;
    }

    public String getClassMethodName() {
        return classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String[] getDeclarations() {
        return declarations;
    }

    public void setDeclarations(String[] declarations) {
        this.declarations = declarations;
    }

}
