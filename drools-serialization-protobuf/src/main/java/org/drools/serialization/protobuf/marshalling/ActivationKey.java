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
package org.drools.serialization.protobuf.marshalling;

import java.util.Arrays;

public class ActivationKey {

    private final String pkgName;
    private final String ruleName;
    private final Object[] tuple;

    public ActivationKey(String pkgName,
                         String ruleName,
                         Object[] tuple) {
        this.pkgName = pkgName;
        this.ruleName = ruleName;
        this.tuple = tuple;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
        result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
        result = prime * result + Arrays.deepHashCode( tuple );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ActivationKey other = (ActivationKey) obj;
        if ( pkgName == null ) {
            if ( other.pkgName != null ) return false;
        } else if ( !pkgName.equals( other.pkgName ) ) return false;
        if ( ruleName == null ) {
            if ( other.ruleName != null ) return false;
        } else if ( !ruleName.equals( other.ruleName ) ) return false;
        return Arrays.deepEquals( tuple, other.tuple );
    }
}
