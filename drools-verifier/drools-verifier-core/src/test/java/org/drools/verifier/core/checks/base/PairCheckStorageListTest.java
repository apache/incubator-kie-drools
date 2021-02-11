/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.checks.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void setUp() throws Exception {
        pairCheckStorage = new PairCheckStorage();
        pairCheckListOne = new PairCheckBundle(a,
                                               b,
                                               newMockList());
        pairCheckStorage.add(pairCheckListOne);
        pairCheckListTwo = new PairCheckBundle(b,
                                               a,
                                               newMockList());
        pairCheckStorage.add(pairCheckListTwo);
        pairCheckStorage.add(new PairCheckBundle(a,
                                                 c,
                                                 newMockList()));
        pairCheckStorage.add(new PairCheckBundle(c,
                                                 a,
                                                 newMockList()));
    }

    private List<Check> newMockList() {
        final ArrayList<Check> checks = new ArrayList<>();
        checks.add(mock(PairCheck.class));
        return checks;
    }

    @Test
    public void getA() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.get(a);
        assertEquals(4, pairCheckLists.size());
        assertTrue(pairCheckLists.contains(pairCheckListOne));
        assertTrue(pairCheckLists.contains(pairCheckListTwo));
    }

    @Test
    public void getB() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.get(b);
        assertEquals(2, pairCheckLists.size());
        assertTrue(pairCheckLists.contains(pairCheckListOne));
        assertTrue(pairCheckLists.contains(pairCheckListTwo));
    }

    @Test
    public void removeB() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.remove(b);
        assertEquals(2, pairCheckLists.size());
        assertTrue(pairCheckLists.contains(pairCheckListOne));
        assertTrue(pairCheckLists.contains(pairCheckListTwo));

        assertTrue(this.pairCheckStorage.get(b)
                           .isEmpty());

        final Collection<PairCheckBundle> pairChecksForAList = this.pairCheckStorage.get(a);
        assertEquals(2, pairChecksForAList.size());
        assertFalse(pairChecksForAList.contains(pairCheckListOne));
        assertFalse(pairChecksForAList.contains(pairCheckListTwo));
    }

    @Test
    public void removeA() throws Exception {
        final Collection<PairCheckBundle> pairCheckLists = this.pairCheckStorage.remove(a);

        assertEquals(4, pairCheckLists.size());

        assertTrue(this.pairCheckStorage.get(a)
                           .isEmpty());
        assertTrue(this.pairCheckStorage.get(b)
                           .isEmpty());
        assertTrue(this.pairCheckStorage.get(c)
                           .isEmpty());
    }
}