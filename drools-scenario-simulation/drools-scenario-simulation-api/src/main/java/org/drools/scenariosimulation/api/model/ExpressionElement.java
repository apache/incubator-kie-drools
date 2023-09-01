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
package org.drools.scenariosimulation.api.model;

import java.util.Objects;

/**
 * Single element of a expression, i.e. in person.fullName.last each component is an ExpressionElement
 */
public class ExpressionElement {

    private String step;

    public ExpressionElement() {
    }

    public ExpressionElement(String step) {
        this.step = step;
    }

    public String getStep() {
        return step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpressionElement that = (ExpressionElement) o;
        return Objects.equals(getStep(), that.getStep());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStep());
    }
}
