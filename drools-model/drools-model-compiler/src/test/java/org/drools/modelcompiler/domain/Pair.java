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
package org.drools.modelcompiler.domain;

public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public Pair(Pair<? extends K, ? extends V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public K getFirst() {
        return this.key;
    }

    public V getSecond() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            boolean var10000;
            label43: {
                label29: {
                    Pair<?, ?> oP = (Pair)o;
                    if (this.key == null) {
                        if (oP.key != null) {
                            break label29;
                        }
                    } else if (!this.key.equals(oP.key)) {
                        break label29;
                    }

                    if (this.value == null) {
                        if (oP.value == null) {
                            break label43;
                        }
                    } else if (this.value.equals(oP.value)) {
                        break label43;
                    }
                }

                var10000 = false;
                return var10000;
            }

            var10000 = true;
            return var10000;
        }
    }

    public int hashCode() {
        int result = this.key == null ? 0 : this.key.hashCode();
        int h = this.value == null ? 0 : this.value.hashCode();
        result = 37 * result + h ^ h >>> 16;
        return result;
    }

    public String toString() {
        return "[" + this.getKey() + ", " + this.getValue() + "]";
    }

    public static <K, V> Pair<K, V> create(K k, V v) {
        return new Pair(k, v);
    }
}
