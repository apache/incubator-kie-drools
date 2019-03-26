/*
 * Copyright 2006 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

/**
 * A descriptor for imported static functions
 */
public class AccumulateImportDescr extends ImportDescr {

    private static final long serialVersionUID = 510l;
    
    private String functionName;

    public AccumulateImportDescr() {
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String toString() {
        return "import acc " + this.getTarget()+ " " + functionName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((functionName == null) ? 0 : functionName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccumulateImportDescr other = (AccumulateImportDescr) obj;
        if (functionName == null) {
            if (other.functionName != null)
                return false;
        } else if (!functionName.equals(other.functionName))
            return false;
        return true;
    }

}
