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

package org.drools.reteoo.beta;

/**
 * DummyValueObject
 * A simple Value Object to use in test cases
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 28/02/2006
 */
public class DummyValueObject {
    private boolean booleanAttr;
    private String  stringAttr;
    private int     intAttr;
    private Object  objectAttr;

    public DummyValueObject() {
    }

    public DummyValueObject(final boolean aBoolean,
                            final String aString,
                            final int anInteger,
                            final Object anObject) {
        this.booleanAttr = aBoolean;
        this.stringAttr = aString;
        this.intAttr = anInteger;
        this.objectAttr = anObject;
    }

    /**
     * @return Returns the booleanAttr.
     */
    public boolean isBooleanAttr() {
        return this.booleanAttr;
    }

    /**
     * @param boolean1 The booleanAttr to set.
     */
    public void setBooleanAttr(final boolean boolean1) {
        this.booleanAttr = boolean1;
    }

    /**
     * @return Returns the intAttr.
     */
    public int getIntAttr() {
        return this.intAttr;
    }

    /**
     * @param intAttr The intAttr to set.
     */
    public void setIntAttr(final int anInteger) {
        this.intAttr = anInteger;
    }

    /**
     * @return Returns the objectAttr.
     */
    public Object getObjectAttr() {
        return this.objectAttr;
    }

    /**
     * @param objectAttr The objectAttr to set.
     */
    public void setObjectAttr(final Object anObject) {
        this.objectAttr = anObject;
    }

    /**
     * @return Returns the stringAttr.
     */
    public String getStringAttr() {
        return this.stringAttr;
    }

    /**
     * @param string The stringAttr to set.
     */
    public void setStringAttr(final String string) {
        this.stringAttr = string;
    }
}
