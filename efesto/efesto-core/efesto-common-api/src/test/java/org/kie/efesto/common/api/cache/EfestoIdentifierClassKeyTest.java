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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoIdentifierClassKeyTest {

    @Test
    void sameIdentifierSameClass() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId1 = new ModelLocalUriId(parsed);
        ModelLocalUriId modelLocalUriId2 = new ModelLocalUriId(parsed);
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        EfestoIdentifierClassKey identifierKeyListString1 = new EfestoIdentifierClassKey(modelLocalUriId1,
                                                                                         keyListString1);
        EfestoIdentifierClassKey identifierKeyListString2 = new EfestoIdentifierClassKey(modelLocalUriId2,
                                                                                         keyListString2);
        assertThat(identifierKeyListString1).isEqualTo(identifierKeyListString2);
    }

    @Test
    void sameIdentifierDifferentClass() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId1 = new ModelLocalUriId(parsed);
        ModelLocalUriId modelLocalUriId2 = new ModelLocalUriId(parsed);
        EfestoClassKey keyListString = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        EfestoIdentifierClassKey identifierKeyListString1 = new EfestoIdentifierClassKey(modelLocalUriId1,
                                                                                         keyListString);
        EfestoIdentifierClassKey identifierKeyListString2 = new EfestoIdentifierClassKey(modelLocalUriId2,
                                                                                         keyArrayListString);
        assertThat(identifierKeyListString1).isNotEqualTo(identifierKeyListString2);
    }

    @Test
    void differentIdentifierSameClass() {
        String path1 = "/example/some-id/instances/some-instance-id";
        LocalUri parsed1 = LocalUri.parse(path1);
        ModelLocalUriId modelLocalUriId1 = new ModelLocalUriId(parsed1);
        String path2 = "/example/some-id/instances/another-instance-id";
        LocalUri parsed2 = LocalUri.parse(path2);
        ModelLocalUriId modelLocalUriId2 = new ModelLocalUriId(parsed2);
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        EfestoIdentifierClassKey identifierKeyListString1 = new EfestoIdentifierClassKey(modelLocalUriId1,
                                                                                         keyListString1);
        EfestoIdentifierClassKey identifierKeyListString2 = new EfestoIdentifierClassKey(modelLocalUriId2,
                                                                                         keyListString2);
        assertThat(identifierKeyListString1).isNotEqualTo(identifierKeyListString2);
    }
}