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

public class PatternVariable extends RuleComponent implements Variable {


    // TODO: Should be pattern path, type and name -Rikkola-

    private String objectTypePath;
    private String objectTypeType;
    private String objectTypeName;

    public PatternVariable(VerifierRule rule) {
        super(rule);
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    private String name;

    public String getObjectTypePath() {
        return objectTypePath;
    }

    public void setObjectTypePath(String path) {
        this.objectTypePath = path;
    }

    public void setObjectTypeType(String type) {
        // VerifierComponentType.OBJECT_TYPE dominates VerifierComponentType.FIELD.
        if (this.objectTypeType == null || !VerifierComponentType.OBJECT_TYPE.getType().equals(this.objectTypeType)) {
            this.objectTypeType = type;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectTypeType() {
        return objectTypeType;
    }

    @Override
    public String toString() {
        return "Variable name: " + name;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.VARIABLE;
    }
}
