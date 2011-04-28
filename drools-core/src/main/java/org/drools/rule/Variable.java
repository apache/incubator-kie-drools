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

package org.drools.rule;

public class Variable {
    public static final Variable variable = new Variable(null, -1);
    
    private Object[] values;
    
    private int index;
    private boolean set;    

    public Variable(Object[] values, 
                    int position) {
        this.values = values;
        this.index = position;
    }

    public Object getValue() {
        return this.values[this.index];
    }

    public void setValue(Object value) {
        this.set = true;
        this.values[this.index] = value;
    }
    
    public String toString() {
        if ( values != null ) {
            Object o = getValue();
            if ( o != null ) {
                return "var = " + o.toString();    
            } 
        }

        return "var = null";
    }

    public boolean isSet() {
        return set;
    }

    public void unSet() {
        this.set = false;
        this.values[this.index] = null;
    }

    public int getIndex() {
        return index;
    }       
    
}
