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

/**
 *
 * @author Toni Rikkola
 */
public class Pattern extends RuleComponent
    implements
    Cause {

    private static final long     serialVersionUID = 510l;

    private String                objectTypePath;
    private String                name;
    private VerifierComponentType sourceType;
    private String                sourcePath;

    private boolean               isPatternNot     = false;
    private boolean               isPatternExists  = false;
    private boolean               isPatternForall  = false;

    public Pattern(VerifierRule rule) {
        super( rule );
    }

    @Override
    public String getPath() {
        String parentPath = getParentPath();

        if ( parentPath == null ) {
            return String.format( "%s/ruleComponent[@type=%s @orderNumber=%s]",
                                  getRulePath(),
                                  getVerifierComponentType().getType(),
                                  getOrderNumber() );

        } else {
            return String.format( "%s/ruleComponent[@type=%s @orderNumber=%s]",
                                  getParentPath(),
                                  getVerifierComponentType().getType(),
                                  getOrderNumber() );
        }
    }

    public boolean isPatternNot() {
        return isPatternNot;
    }

    public void setPatternNot(boolean isNot) {
        this.isPatternNot = isNot;
    }

    public boolean isPatternExists() {
        return isPatternExists;
    }

    public void setPatternExists(boolean isExists) {
        this.isPatternExists = isExists;
    }

    public String getObjectTypePath() {
        return objectTypePath;
    }

    public void setObjectTypePath(String path) {
        this.objectTypePath = path;
    }

    public boolean isPatternForall() {
        return isPatternForall;
    }

    public void setPatternForall(boolean isForall) {
        this.isPatternForall = isForall;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public VerifierComponentType getSourceType() {
        return sourceType;
    }

    public void setSourceType(VerifierComponentType sourceType) {
        this.sourceType = sourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Pattern, name: " + name;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.PATTERN;
    }

}
