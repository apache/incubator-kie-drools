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
package org.drools.model.codegen.execmodel.domain;

public class ChildFactComplex {

    private final int id;
    private final int parentId;

    private final boolean isTravelDocReady;
    private final boolean isCheeseReady;

    private final EnumFact1 enum1Value;
    private final EnumFact2 enum2Value;

    public ChildFactComplex(final int id, final int parentId,
                            final boolean isTravelDocReady,
                            final boolean isCheeseReady,
                            final EnumFact1 enum1Value,
                            final EnumFact2 enum2Value) {
        this.id = id;
        this.parentId = parentId;
        this.isTravelDocReady = isTravelDocReady;
        this.isCheeseReady = isCheeseReady;
        this.enum1Value = enum1Value;
        this.enum2Value = enum2Value;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isTravelDocReady() {
        return isTravelDocReady;
    }

    public boolean isCheeseReady() {
        return isCheeseReady;
    }

    public EnumFact1 getEnum1Value() {
        return enum1Value;
    }

    public EnumFact2 getEnum2Value() {
        return enum2Value;
    }

    public Short getIdAsShort() {
        return (short) id;
    }
}
