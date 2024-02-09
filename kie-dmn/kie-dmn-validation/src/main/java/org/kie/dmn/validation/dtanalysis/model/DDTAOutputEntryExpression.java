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
package org.kie.dmn.validation.dtanalysis.model;

import java.util.Objects;

import org.kie.dmn.feel.lang.ast.ASTNode;

/**
 * When an output entry is NOT a constant/literal, but an expression (even a FQN symbol),
 * and we can only hold the node identity.
 */
public class DDTAOutputEntryExpression implements Comparable<DDTAOutputEntryExpression> {

    private final ASTNode baseNode;

    public DDTAOutputEntryExpression(ASTNode n) {
        this.baseNode = n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseNode.getText());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DDTAOutputEntryExpression other = (DDTAOutputEntryExpression) obj;
        return Objects.equals(baseNode.getText(), other.baseNode.getText());
    }

    @Override
    public int compareTo(DDTAOutputEntryExpression o) {
        return this.baseNode.getText().compareTo(o.baseNode.getText());
    }

}
