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
    void testEquals() {
        EfestoClassKey first = new EfestoClassKey(List.class, String.class);
        EfestoClassKey second = new EfestoClassKey(List.class, String.class);
        assertThat(first.equals(second)).isTrue();
        second = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(first.equals(second)).isFalse();
        first = new EfestoClassKey(Map.class, String.class, Integer.class);
        second = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(first.equals(second)).isTrue();
        second = new EfestoClassKey(HashMap.class, String.class, Integer.class);
        assertThat(first.equals(second)).isFalse();
        second = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(first.equals(second)).isFalse();
    }

    @Test
    void testSet() {
        Set<EfestoClassKey> set = new HashSet<>();
        EfestoClassKey first = new EfestoClassKey(List.class, String.class);
        set.add(first);
        EfestoClassKey second = new EfestoClassKey(List.class, String.class);
        assertThat(set.contains(second)).isTrue();
        set.add(second);
        assertThat(set).size().isEqualTo(1);
        second = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(set.contains(second)).isFalse();
        set.add(second);
        assertThat(set).size().isEqualTo(2);

        set = new HashSet<>();
        first = new EfestoClassKey(Map.class, String.class, Integer.class);
        set.add(first);
        second = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(set.contains(second)).isTrue();
        set.add(second);
        assertThat(set).size().isEqualTo(1);
        second = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(set.contains(second)).isFalse();
        set.add(second);
        assertThat(set).size().isEqualTo(2);
    }

    @Test
    void testMap() {
        Map<EfestoClassKey, String> map = new HashMap<>();
        EfestoClassKey first = new EfestoClassKey(List.class, String.class);
        map.put(first, "");
        EfestoClassKey second = new EfestoClassKey(List.class, String.class);
        assertThat(map.containsKey(second)).isTrue();
        map.put(second, "");
        assertThat(map).size().isEqualTo(1);
        second = new EfestoClassKey(ArrayList.class, String.class);
        assertThat(map.containsKey(second)).isFalse();
        map.put(second, "");
        assertThat(map).size().isEqualTo(2);

        map = new HashMap<>();
        first = new EfestoClassKey(Map.class, String.class, Integer.class);
        map.put(first, "");
        second = new EfestoClassKey(Map.class, String.class, Integer.class);
        assertThat(map.containsKey(second)).isTrue();
        map.put(second, "");
        assertThat(map).size().isEqualTo(1);
        second = new EfestoClassKey(Map.class, Integer.class, String.class);
        assertThat(map.containsKey(second)).isFalse();
        map.put(second, "");
        assertThat(map).size().isEqualTo(2);
    }

    @Test
    void getActualTypeArguments() {
        List<Type> typeArguments = Arrays.asList(String.class, Boolean.class, Long.class);
        EfestoClassKey efestoClassKey = new EfestoClassKey(List.class, typeArguments.toArray(new Type[0]));
        Type[] retrieved = efestoClassKey.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(typeArguments.size());
        for (Type type : retrieved) {
            assertThat(typeArguments.contains(type)).isTrue();
        }

        typeArguments = Collections.emptyList();
        efestoClassKey = new EfestoClassKey(List.class, typeArguments.toArray(new Type[0]));
        retrieved = efestoClassKey.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(0);

        efestoClassKey = new EfestoClassKey(List.class);
        retrieved = efestoClassKey.getActualTypeArguments();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.length).isEqualTo(0);

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