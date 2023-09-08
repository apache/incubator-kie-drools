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
package org.drools.model.impl;

import java.util.List;
import java.util.Map;

public interface ModelComponent {
    boolean isEqualTo(ModelComponent other);

    static boolean areEqualInModel( Object o1, Object o2 ) {
        return o1 == null ? o2 == null :
                ( o1 instanceof ModelComponent ? (( ModelComponent ) o1).isEqualTo( ( ModelComponent ) o2 ) :
                                                 o1.equals( o2 ) );
    }

    static boolean areEqualInModel( Object[] array1, Object[] array2 ) {
        if (array1 == null) return array2 == null;
        if (array2 == null) return false;
        if (array1.length != array2.length) return false;
        for ( int i = 0; i < array1.length; i++ ) {
            if (!areEqualInModel(array1[i], array2[i] )) return false;
        }
        return true;
    }

    static boolean areEqualInModel( List<?> list1, List<?> list2 ) {
        if (list1 == null) return list2 == null;
        if (list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        for ( int i = 0; i < list1.size(); i++ ) {
            if (!areEqualInModel(list1.get(i), list2.get(i) )) return false;
        }
        return true;
    }

    static boolean areEqualInModel( Map<?,?> map1, Map<?,?> map2 ) {
        if (map1 == null) return map2 == null;
        if (map2 == null) return false;
        if (map1.size() != map2.size()) return false;
        for ( Map.Entry<?,?> entry : map1.entrySet() ) {
            if (!areEqualInModel(entry.getValue(), map2.get(entry.getKey()) )) return false;
        }
        return true;
    }
}
