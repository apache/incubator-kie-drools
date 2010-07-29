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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

/**
 * A descr class for accumulate node
 */
public class AccumulateDescr extends PatternSourceDescr
    implements
    ConditionalElementDescr,
    PatternDestinationDescr,
    MultiPatternDestinationDescr {

    private static final long serialVersionUID = 510l;

    private BaseDescr         input;
    private String            initCode;
    private String            actionCode;
    private String            reverseCode;
    private String            resultCode;
    private String[]          declarations;
    private String            className;
    private boolean           externalFunction = false;
    private String            functionIdentifier;
    private String            expression;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        input           = (BaseDescr)in.readObject();
        initCode        = (String)in.readObject();
        actionCode      = (String)in.readObject();
        reverseCode     = (String)in.readObject();
        resultCode      = (String)in.readObject();
        declarations    = (String[])in.readObject();
        className       = (String)in.readObject();
        externalFunction    = in.readBoolean();
        functionIdentifier  = (String)in.readObject();
        expression          = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(input);
        out.writeObject(initCode);
        out.writeObject(actionCode);
        out.writeObject(reverseCode);
        out.writeObject(resultCode);
        out.writeObject(declarations);
        out.writeObject(className);
        out.writeBoolean(externalFunction);
        out.writeObject(functionIdentifier);
        out.writeObject(expression);
    }
    
    public int getLine() {
        return this.input.getLine();
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

    public String toString() {
        return "[Accumulate: input=" + this.input.toString() + "]";
    }

    public void addDescr(final BaseDescr patternDescr) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public void insertBeforeLast(final Class clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public List getDescrs() {
        // nothing to do
        return Collections.EMPTY_LIST;
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
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

    public PatternDescr getInputPattern() {
        if( isSinglePattern() ) {
            return (PatternDescr) this.input;
        }
        return null;
    }

    public void setInputPattern(final PatternDescr inputPattern) {
        this.input = inputPattern;
    }

    public BaseDescr getInput() {
        return input;
    }

    public void setInput(BaseDescr input) {
        this.input = input;
    }

    public boolean isSinglePattern() {
        return this.input instanceof PatternDescr;
    }

    public boolean isMultiPattern() {
        return ! ( this.input instanceof PatternDescr );
    }

    public boolean hasValidInput() {
        // TODO: need to check that there are no OR occurences
        return this.input != null;
    }
}
