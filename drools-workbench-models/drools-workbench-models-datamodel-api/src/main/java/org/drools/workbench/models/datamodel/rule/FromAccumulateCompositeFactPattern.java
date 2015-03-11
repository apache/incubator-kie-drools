/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

public class FromAccumulateCompositeFactPattern extends FromCompositeFactPattern {

    public static final String USE_FUNCTION = "use_function";
    public static final String USE_CODE = "use_code";

    private IPattern sourcePattern;
    private String initCode;
    private String actionCode;
    private String reverseCode;
    private String resultCode;

    private String function;

    public FromAccumulateCompositeFactPattern() {
    }

    public String useFunctionOrCode() {
        if ( this.initCode != null && !this.initCode.trim().equals( "" ) ) {
            //if the initCode is set, we must use it.
            return FromAccumulateCompositeFactPattern.USE_CODE;
        }

        //otherwise use Function. (this is the default)
        return FromAccumulateCompositeFactPattern.USE_FUNCTION;
    }

    public void clearCodeFields() {
        this.initCode = null;
        this.actionCode = null;
        this.reverseCode = null;
        this.resultCode = null;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode( String actionCode ) {
        this.actionCode = actionCode;
    }

    public String getInitCode() {
        return initCode;
    }

    public void setInitCode( String initCode ) {
        this.initCode = initCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode( String resultCode ) {
        this.resultCode = resultCode;
    }

    public String getReverseCode() {
        return reverseCode;
    }

    public void setReverseCode( String reverseCode ) {
        this.reverseCode = reverseCode;
    }

    public IPattern getSourcePattern() {
        return sourcePattern;
    }

    public void setSourcePattern( IPattern sourcePattern ) {
        this.sourcePattern = sourcePattern;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction( String function ) {
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FromAccumulateCompositeFactPattern)) return false;
        if (!super.equals(o)) return false;

        FromAccumulateCompositeFactPattern that = (FromAccumulateCompositeFactPattern) o;

        if (actionCode != null ? !actionCode.equals(that.actionCode) : that.actionCode != null) return false;
        if (function != null ? !function.equals(that.function) : that.function != null) return false;
        if (initCode != null ? !initCode.equals(that.initCode) : that.initCode != null) return false;
        if (resultCode != null ? !resultCode.equals(that.resultCode) : that.resultCode != null) return false;
        if (reverseCode != null ? !reverseCode.equals(that.reverseCode) : that.reverseCode != null) return false;
        if (sourcePattern != null ? !sourcePattern.equals(that.sourcePattern) : that.sourcePattern != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (sourcePattern != null ? sourcePattern.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (initCode != null ? initCode.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (actionCode != null ? actionCode.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (reverseCode != null ? reverseCode.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (resultCode != null ? resultCode.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
