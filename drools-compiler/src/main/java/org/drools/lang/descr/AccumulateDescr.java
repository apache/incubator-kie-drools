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

package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

/**
 * A descr class for accumulate node
 */
public class AccumulateDescr extends BaseDescr
    implements
    PatternProcessorCeDescr {

    private static final long serialVersionUID = 2831283873824863255L;

    private PatternDescr      sourcePattern;
    private PatternDescr      resultPattern;
    private String            initCode;
    private String            actionCode;
    private String            reverseCode;
    private String            resultCode;
    private String[]          declarations;
    private String            className;
    private boolean           externalFunction = false;
    private String            functionIdentifier;
    private String            expression;

    public int getLine() {
        return this.sourcePattern.getLine();
    }

    public void setSourcePattern(final PatternDescr sourcePattern) {
        this.sourcePattern = sourcePattern;
    }

    public PatternDescr getSourcePattern() {
        return this.sourcePattern;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(final String classMethodName) {
        this.className = classMethodName;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public void setDeclarations(final String[] declarations) {
        this.declarations = declarations;
    }

    public String getActionCode() {
        return this.actionCode;
    }

    public void setActionCode(final String actionCode) {
        this.actionCode = actionCode;
    }

    public String getInitCode() {
        return this.initCode;
    }

    public void setInitCode(final String initCode) {
        this.initCode = initCode;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(final String resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultPattern(final PatternDescr resultPattern) {
        this.resultPattern = resultPattern;
    }

    public PatternDescr getResultPattern() {
        return this.resultPattern;
    }

    public String toString() {
        return "[Accumulate: id=" + this.resultPattern.getIdentifier() + "; objectType=" + this.resultPattern.getObjectType() + "]";
    }

    public void addDescr(final BaseDescr patternDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());        
    }

    public List getDescrs() {
        // nothing to do
        return Collections.EMPTY_LIST;
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());        
    }

    public String getReverseCode() {
        return reverseCode;
    }

    public void setReverseCode(String reverseCode) {
        this.reverseCode = reverseCode;
    }

    public boolean isExternalFunction() {
        return externalFunction;
    }

    public void setExternalFunction(boolean externalFunction) {
        this.externalFunction = externalFunction;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getFunctionIdentifier() {
        return functionIdentifier;
    }

    public void setFunctionIdentifier(String functionIdentifier) {
        this.functionIdentifier = functionIdentifier;
    }

}
