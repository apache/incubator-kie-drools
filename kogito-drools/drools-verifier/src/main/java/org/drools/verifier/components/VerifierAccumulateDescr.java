/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

public class VerifierAccumulateDescr extends PatternComponentSource {

    private String   initCode;
    private String   actionCode;
    private String   reverseCode;
    private String   resultCode;
    private String[] declarations;
    private String   className;
    private boolean  externalFunction = false;
    private String   functionIdentifier;
    private String   expression;

    public VerifierAccumulateDescr(Pattern pattern) {
        super( pattern );
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String[] getDeclarations() {
        return declarations;
    }

    public void setDeclarations(String[] declarations) {
        this.declarations = declarations;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isExternalFunction() {
        return externalFunction;
    }

    public void setExternalFunction(boolean externalFunction) {
        this.externalFunction = externalFunction;
    }

    public String getFunctionIdentifier() {
        return functionIdentifier;
    }

    public void setFunctionIdentifier(String functionIdentifier) {
        this.functionIdentifier = functionIdentifier;
    }

    public String getInitCode() {
        return initCode;
    }

    public void setInitCode(String initCode) {
        this.initCode = initCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getReverseCode() {
        return reverseCode;
    }

    public void setReverseCode(String reverseCode) {
        this.reverseCode = reverseCode;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.ACCUMULATE;
    }
}
