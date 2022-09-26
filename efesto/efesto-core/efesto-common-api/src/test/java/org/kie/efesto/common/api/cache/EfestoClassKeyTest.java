/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        keyListString2 = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(keyListString1).isNotEqualTo(keyListString2);
        keyListString1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        keyListString2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(keyListString1).isEqualTo(keyListString2);
        keyListString2 = new EfestoClassKey(HashMap.class, String.class, Integer.class);
        assertThat(keyListString1).isNotEqualTo(keyListString2);
        keyListString2 = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(keyListString1).isNotEqualTo(keyListString2);
    }

    @Test
    void setOfKeys() {
        Set<EfestoClassKey> set = new HashSet<>();
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        set.add(keyListString1);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        assertThat(set.contains(keyListString2)).isTrue();
        set.add(keyListString2);
        assertThat(set).hasSize(1);
        keyListString2 = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(set.contains(keyListString2)).isFalse();
        set.add(keyListString2);
        assertThat(set).hasSize(2);

        set = new HashSet<>();
        keyListString1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        set.add(keyListString1);
        keyListString2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(set.contains(keyListString2)).isTrue();
        set.add(keyListString2);
        assertThat(set).hasSize(1);
        keyListString2 = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(set.contains(keyListString2)).isFalse();
        set.add(keyListString2);
        assertThat(set).hasSize(2);
    }

    @Test
    void mapOfKeys() {
        Map<EfestoClassKey, String> map = new HashMap<>();
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        map.put(keyListString1, "");
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        assertThat(map.containsKey(keyListString2)).isTrue();
        map.put(keyListString2, "");
        assertThat(map).hasSize(1);
        keyListString2 = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(map.containsKey(keyListString2)).isFalse();
        map.put(keyListString2, "");
        assertThat(map).hasSize(2);

        map = new HashMap<>();
        keyListString1 = new EfestoClassKey(Map.class, String.class, Integer.class);
        map.put(keyListString1, "");
        keyListString2 = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(map.containsKey(keyListString2)).isTrue();
        map.put(keyListString2, "");
        assertThat(map).hasSize(1);
        keyListString2 = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(map.containsKey(keyListString2)).isFalse();
        map.put(keyListString2, "");
        assertThat(map).hasSize(2);
    }

    @Test
    void getActualTypeArguments() {
        List<Type> typeArguments = Arrays.asList(String.class, Boolean.class, Long.class);
        EfestoClassKey efestoClassKey = new EfestoClassKey(List.class, typeArguments.toArray(new Type[0]));
        Type[] retrieved = efestoClassKey.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(typeArguments);
        assertThat(typeArguments).containsAll(Arrays.asList(retrieved));
        typeArguments = Collections.emptyList();
        efestoClassKey = new EfestoClassKey(List.class, typeArguments.toArray(new Type[0]));
        retrieved = efestoClassKey.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();

        efestoClassKey = new EfestoClassKey(List.class);
        retrieved = efestoClassKey.getActualTypeArguments();
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
}