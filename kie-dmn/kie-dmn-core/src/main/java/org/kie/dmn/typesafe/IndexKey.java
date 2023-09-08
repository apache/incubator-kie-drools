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
package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNType;

public class IndexKey {

    public final DMNType dmnType;

    public IndexKey(DMNType dmnType) {
        this.dmnType = dmnType;
    }

    public static IndexKey from(DMNType dmnType) {
        return new IndexKey(dmnType);
    }

    public Object getName() {
        return dmnType.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dmnType == null) ? 0 : dmnType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IndexKey other = (IndexKey) obj;
        if (dmnType == null) {
            if (other.dmnType != null)
                return false;
        } else if (!dmnType.equals(other.dmnType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IndexKey [" + getName() + "]";
    }

}
