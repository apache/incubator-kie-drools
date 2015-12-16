/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.compiler;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: davide
 * Date: 2/12/11
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class LegacyBean extends SuperLegacy {

    private String strField;
    private int intField;

    private Object objField;
    private Double doubleField;

    private BigDecimal decimalField;

    public String getStrField() {
        return strField;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public Object getObjField() {
        return objField;
    }

    public void setObjField(Object objField) {
        this.objField = objField;
    }

    public Double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }


    public BigDecimal getDecimalField() {
        return decimalField;
    }

    public void setDecimalField(BigDecimal decimalField) {
        this.decimalField = decimalField;
    }
}

class SuperLegacy {

        private boolean prop;

        public boolean isProp() {
            return prop;
        }

        public void setProp(boolean prop) {
            this.prop = prop;
        }


        public int myStuff() {
            return 0;
        }
    }
