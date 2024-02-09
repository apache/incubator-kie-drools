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

import java.util.Objects;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * Key used by efesto second-level cache based on <code>EfestoClassKey</code> and
 * <code>ModelLocalUriId</code>
 */
public class EfestoIdentifierClassKey {

    private final ModelLocalUriId modelLocalUriId;
    private final EfestoClassKey efestoClassKey;

    public EfestoIdentifierClassKey(ModelLocalUriId modelLocalUriId, EfestoClassKey efestoClassKey) {
        this.modelLocalUriId = modelLocalUriId;
        this.efestoClassKey = efestoClassKey;
    }

    @Override
    public String toString() {
        return "EfestoIdentifierClassKey{" +
                "modelLocalUriId=" + modelLocalUriId +
                ", efestoClassKey=" + efestoClassKey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EfestoIdentifierClassKey that = (EfestoIdentifierClassKey) o;
        return Objects.equals(modelLocalUriId, that.modelLocalUriId) && Objects.equals(efestoClassKey,
                                                                                       that.efestoClassKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelLocalUriId, efestoClassKey);
    }
}
