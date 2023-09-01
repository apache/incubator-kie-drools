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
package org.kie.efesto.common.api.cache;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoClassKeyTest {

    @Test
    void equalsMethod() {
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        assertThat(keyListString1).isEqualTo(keyListString2);

        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(keyListString1).isNotEqualTo(keyArrayListString);

        EfestoClassKey keyMapStringInteger1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        EfestoClassKey keyMapStringInteger2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(keyMapStringInteger1).isEqualTo(keyMapStringInteger2);

        EfestoClassKey keyHashMapStringInteger = new EfestoClassKey(HashMap.class, String.class, Integer.class);
        assertThat(keyMapStringInteger1).isNotEqualTo(keyHashMapStringInteger);
        EfestoClassKey keyMapIntegerString1 = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(keyMapIntegerString1).isNotEqualTo(keyMapStringInteger1);
    }

    @Test
    void equalsExtender() {
        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        EfestoClassKey keyArrayListExtender = new EfestoClassKey(ArrayListExtender.class);
        assertThat(keyArrayListString).isNotEqualTo(keyArrayListExtender);
    }

    @Test
    void equalsBaseInputMethod() {
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        assertThat(keyListString1).isEqualTo(keyListString2);

        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(keyListString1).isNotEqualTo(keyArrayListString);

        EfestoClassKey keyMapStringInteger1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        EfestoClassKey keyMapStringInteger2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(keyMapStringInteger1).isEqualTo(keyMapStringInteger2);

        EfestoClassKey keyHashMapStringInteger = new EfestoClassKey(HashMap.class, String.class, Integer.class);
        assertThat(keyMapStringInteger1).isNotEqualTo(keyHashMapStringInteger);
        EfestoClassKey keyMapIntegerString1 = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(keyMapIntegerString1).isNotEqualTo(keyMapStringInteger1);
    }

    @Test
    void setOfKeys() {
        Set<EfestoClassKey> set = new HashSet<>();
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        set.add(keyListString1);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        assertThat(set).contains(keyListString2);
        set.add(keyListString2);
        assertThat(set).hasSize(1);

        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(set).doesNotContain(keyArrayListString);
        set.add(keyArrayListString);
        assertThat(set).hasSize(2);
        assertThat(set).contains(keyArrayListString);

        set = new HashSet<>();
        EfestoClassKey keyMapStringInteger1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        set.add(keyMapStringInteger1);
        EfestoClassKey keyMapStringInteger2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(set).contains(keyMapStringInteger2);
        set.add(keyMapStringInteger2);
        assertThat(set).hasSize(1);

        EfestoClassKey keyMapIntegerString = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(set).doesNotContain(keyMapIntegerString);
        set.add(keyMapIntegerString);
        assertThat(set).hasSize(2);
        assertThat(set).contains(keyMapIntegerString);
    }

    @Test
    void mapOfKeys() {
        Map<EfestoClassKey, String> map = new HashMap<>();
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        map.put(keyListString1, "");
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        assertThat(map).containsKey(keyListString2);
        map.put(keyListString2, "");
        assertThat(map).hasSize(1);

        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(map).doesNotContainKey(keyArrayListString);
        map.put(keyArrayListString, "");
        assertThat(map).hasSize(2);
        assertThat(map).containsKey(keyArrayListString);

        map = new HashMap<>();
        EfestoClassKey keyMapStringInteger1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        map.put(keyMapStringInteger1, "");
        EfestoClassKey keyMapStringInteger2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(map).containsKey(keyMapStringInteger2);
        map.put(keyMapStringInteger2, "");
        assertThat(map).hasSize(1);

        EfestoClassKey keyMapIntegerString = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(map).doesNotContainKey(keyMapIntegerString);
        map.put(keyMapIntegerString, "");
        assertThat(map).hasSize(2);
        assertThat(map).containsKey(keyMapIntegerString);
    }

    @Test
    void getActualTypeArguments() {
        List<Type> typeArguments = Arrays.asList(String.class, Boolean.class, Long.class);
        EfestoClassKey keyListWithTypes = new EfestoClassKey(List.class, typeArguments.toArray(new Type[0]));
        Type[] retrieved = keyListWithTypes.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(typeArguments);
        assertThat(typeArguments).containsAll(Arrays.asList(retrieved));

        typeArguments = Collections.emptyList();
        EfestoClassKey keyListWithEmptyTypes = new EfestoClassKey(List.class, typeArguments.toArray(new Type[0]));
        retrieved = keyListWithEmptyTypes.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();

        EfestoClassKey keyListWithoutTypes = new EfestoClassKey(List.class);
        retrieved = keyListWithoutTypes.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
    }

    @Test
    void getRawType() {
        Type rawType = List.class;
        List<Type> typeArguments = Arrays.asList(String.class, Boolean.class, Long.class);
        EfestoClassKey efestoClassKey = new EfestoClassKey(rawType, typeArguments.toArray(new Type[0]));
        Type retrieved = efestoClassKey.getRawType();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(rawType);
    }

    static class ArrayListExtender extends ArrayList<String> {

    }
}