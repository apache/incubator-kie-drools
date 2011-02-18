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
public abstract class RuleComponent extends PackageComponent
    implements
    ChildComponent {

    private String                ruleName;

    private VerifierComponentType parentType;
    private String                parentPath;
    private int                   orderNumber;

    public RuleComponent(VerifierRule rule) {
        this( rule.getPackageName(),
              rule.getName() );
    }

    RuleComponent(String packageName,
                  String ruleName) {
        super( packageName );

        setRuleName( ruleName );
    }

    /**
     * 
     * @return Rule package name + rule name.
     */
    public String getFullRulePath() {
        return getPackageName() + "/" + getRuleName();
    }

    public String getRuleName() {
        return ruleName;
    }

    protected void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRulePath() {
        return String.format( "%s/rule[@name='%s']",
                              getPackagePath(),
                              getRuleName() );
    }

    @Override
    public String getPath() {
        return String.format( "%s/ruleComponent[%s]",
                              getRulePath(),
                              getOrderNumber() );
    }

    public VerifierComponentType getParentType() {
        return parentType;
    }

    public String getParentPath() {
        return parentPath;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentType(VerifierComponentType parentType) {
        this.parentType = parentType;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

}
