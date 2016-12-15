/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;
import org.drools.core.spi.Tuple;

public class ReactiveSet<T> extends ReactiveCollection<T, Set<T>> implements Set<T> {

    public ReactiveSet() {
        super((Set<T>) new HashSet<T>());
    }
    
    public ReactiveSet(Set<T> wrapped) {
        super(wrapped);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReactiveSet[").append(wrapped).append("]");
        return builder.toString();
    }

}
