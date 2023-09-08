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
package org.kie.pmml.commons.model.tuples;

import java.util.Objects;

import org.kie.pmml.api.enums.OP_TYPE;

/**
 * Class to represent a <b>name/operation type</b> tuple
 */
public class KiePMMLNameOpType {

    private final String name;
    private final OP_TYPE opType;

    public KiePMMLNameOpType(String name, OP_TYPE opType) {
        this.name = name;
        this.opType = opType;
    }

    public String getName() {
        return name;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    @Override
    public String toString() {
        return "NameOpType{" +
                "name='" + name + '\'' +
                ", opType=" + opType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLNameOpType that = (KiePMMLNameOpType) o;
        return Objects.equals(name, that.name) &&
                opType == that.opType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, opType);
    }
}