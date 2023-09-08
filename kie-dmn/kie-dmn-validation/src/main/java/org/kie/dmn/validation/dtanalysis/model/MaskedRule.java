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

import org.kie.dmn.feel.util.Generated;

public class MaskedRule {

    public final int maskedRule;
    public final int maskedBy;

    public MaskedRule(int maskedRule, int maskedBy) {
        super();
        this.maskedRule = maskedRule;
        this.maskedBy = maskedBy;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maskedBy;
        result = prime * result + maskedRule;
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MaskedRule other = (MaskedRule) obj;
        if (maskedBy != other.maskedBy) {
            return false;
        }
        if (maskedRule != other.maskedRule) {
            return false;
        }
        return true;
    }

}
