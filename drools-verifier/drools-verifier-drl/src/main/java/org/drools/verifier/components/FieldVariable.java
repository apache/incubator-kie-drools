/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.components;

public class FieldVariable extends PatternComponent implements Variable {

    private String parentPath;
    private int orderNumber;
    private String name;

    public FieldVariable(Pattern pattern) {
        super(pattern);
    }

    public String getName() {
        return name;
    }

    public VerifierComponentType getParentType() {
        return VerifierComponentType.FIELD;
    }


    public String getParentPath() {
        return parentPath;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FIELD_LEVEL_VARIABLE;
    }
}
