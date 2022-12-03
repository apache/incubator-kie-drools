/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.drools.model.codegen.execmodel.domain;

import java.math.BigDecimal;

public class ValueHolder {

    private int intValue;
    private String strValue;
    private BigDecimal bdValue;
    private Object objValue;

    private boolean primitiveBooleanValue;
    private Boolean wrapperBooleanValue;

    public ValueHolder() {}

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public BigDecimal getBdValue() {
        return bdValue;
    }

    public void setBdValue(BigDecimal bdValue) {
        this.bdValue = bdValue;
    }

    public Object getObjValue() {
        return objValue;
    }

    public void setObjValue(Object objValue) {
        this.objValue = objValue;
    }

    public boolean isPrimitiveBooleanValue() {
        return primitiveBooleanValue;
    }

    public void setPrimitiveBooleanValue(boolean primitiveBooleanValue) {
        this.primitiveBooleanValue = primitiveBooleanValue;
    }

    public Boolean getWrapperBooleanValue() {
        return wrapperBooleanValue;
    }

    public void setWrapperBooleanValue(Boolean wrapperBooleanValue) {
        this.wrapperBooleanValue = wrapperBooleanValue;
    }
}
