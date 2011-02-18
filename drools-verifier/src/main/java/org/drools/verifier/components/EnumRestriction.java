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

/**
 * 
 * @author Toni Rikkola
 */
public class EnumRestriction extends Restriction {

    private String enumBasePath;
    private String enumBase;
    private String enumName;

    public EnumRestriction(Pattern pattern) {
        super( pattern );
    }

    @Override
    public RestrictionType getRestrictionType() {
        return RestrictionType.ENUM;
    }

    public String getEnumBasePath() {
        return enumBasePath;
    }

    public void setEnumBasePath(String enumBasePath) {
        this.enumBasePath = enumBasePath;
    }

    public String getEnumBase() {
        return enumBase;
    }

    public void setEnumBase(String enumBase) {
        this.enumBase = enumBase;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    @Override
    public String toString() {
        return "QualifiedIdentifierRestrictionDescr enum: " + enumBase + "." + enumName;
    }
}
