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
package org.drools.verifier.core.checks.base;

import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PairCheckStorageListTest {

    private PairCheckStorage pairCheckStorage;

    @Mock
    private RuleInspector a;

    @Mock
    private RuleInspector b;

    @Mock
    private RuleInspector c;

    private PairCheckBundle pairCheckListOne;
    private PairCheckBundle pairCheckListTwo;

    @BeforeEach
    public void setUp() throws Exception {
        pairCheckStorage = new PairCheckStorage();
        pairCheckListOne = new PairCheckBundle(a, b, newMockList());
        pairCheckStorage.add(pairCheckListOne);
        pairCheckListTwo = new PairCheckBundle(b, a, newMockList());
        pairCheckStorage.add(pairCheckListTwo);
        pairCheckStorage.add(new PairCheckBundle(a, c, newMockList()));
        pairCheckStorage.add(new PairCheckBundle(c, a, newMockList()));
    }

    private List<Check> newMockList() {
        return List.of(mock(PairCheck.class));
    }

    @Test
    void getA() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = pairCheckStorage.get(a);
        
        assertThat(pairCheckLists).hasSize(4).contains(pairCheckListOne, pairCheckListTwo);
    }

    @Test
    void getB() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = pairCheckStorage.get(b);
        
        assertThat(pairCheckLists).hasSize(2).contains(pairCheckListOne, pairCheckListTwo);
    }

    @Test
    void removeB() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = pairCheckStorage.remove(b);
        
        assertThat(pairCheckLists).hasSize(2).contains(pairCheckListOne, pairCheckListTwo);
        assertThat(pairCheckStorage.get(b)).isEmpty();

        final Collection<PairCheckBundle> pairChecksForAList = pairCheckStorage.get(a);
        
        assertThat(pairChecksForAList).hasSize(2).doesNotContain(pairCheckListOne, pairCheckListTwo);
    }

    @Test
    void removeA() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = pairCheckStorage.remove(a);

        assertThat(pairCheckLists).hasSize(4);

        assertThat(pairCheckStorage.get(a)).isEmpty();
        assertThat(pairCheckStorage.get(b)).isEmpty();
        assertThat(pairCheckStorage.get(c)).isEmpty();
    }
}