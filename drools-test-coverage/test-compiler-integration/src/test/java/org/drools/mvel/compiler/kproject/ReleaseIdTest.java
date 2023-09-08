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
package org.drools.mvel.compiler.kproject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.ReleaseIdComparator;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.builder.ReleaseIdComparator.SortDirection.ASCENDING;
import static org.kie.api.builder.ReleaseIdComparator.SortDirection.DESCENDING;

public class ReleaseIdTest {

    private static final ReleaseId gav0 = newReleaseId("abc.def:ghi:9.0.1.GA");
    private static final ReleaseId gav1 = newReleaseId("com.test:foo:1.0.0-SNAPSHOT");
    private static final ReleaseId gav2 = newReleaseId("com.test:foo:1.0.0.Final");
    private static final ReleaseId gav3 = newReleaseId("com.test:foo:2.0.0-SNAPSHOT");
    private static final ReleaseId gav4 = newReleaseId("com.test:foo:2.0.0.Alpha1");
    private static final ReleaseId gav5 = newReleaseId("com.test:foo:2.0.0.Beta2");
    private static final ReleaseId gav6 = newReleaseId("org.example:test:0.0.1-SNAPSHOT");
    private static final ReleaseId gav7 = newReleaseId("org.example:test:1.0");

    private static final ReleaseId newReleaseId(String releaseId) {
        String[] gav = releaseId.split(":");
        return KieServices.Factory.get().newReleaseId(gav[0], gav[1], gav[2]);
    }

    private List<ReleaseId> newUnsortedList() {
        List<ReleaseId> list = new ArrayList<ReleaseId>();
        list.add(gav4);
        list.add(gav2);
        list.add(gav6);
        list.add(gav3);
        list.add(gav7);
        list.add(gav1);
        list.add(gav0);
        list.add(gav5);
        return list;
    }

    @Test
    public void testDefaultSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator());
        assertThat(list.get(0)).isSameAs(gav0);
        assertThat(list.get(1)).isSameAs(gav1);
        assertThat(list.get(2)).isSameAs(gav2);
        assertThat(list.get(3)).isSameAs(gav3);
        assertThat(list.get(4)).isSameAs(gav4);
        assertThat(list.get(5)).isSameAs(gav5);
        assertThat(list.get(6)).isSameAs(gav6);
        assertThat(list.get(7)).isSameAs(gav7);
    }

    @Test
    public void testAscendingSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator(ASCENDING));
        assertThat(list.get(0)).isSameAs(gav0);
        assertThat(list.get(1)).isSameAs(gav1);
        assertThat(list.get(2)).isSameAs(gav2);
        assertThat(list.get(3)).isSameAs(gav3);
        assertThat(list.get(4)).isSameAs(gav4);
        assertThat(list.get(5)).isSameAs(gav5);
        assertThat(list.get(6)).isSameAs(gav6);
        assertThat(list.get(7)).isSameAs(gav7);
    }

    @Test
    public void testDecendingSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator(DESCENDING));
        assertThat(list.get(0)).isSameAs(gav7);
        assertThat(list.get(1)).isSameAs(gav6);
        assertThat(list.get(2)).isSameAs(gav5);
        assertThat(list.get(3)).isSameAs(gav4);
        assertThat(list.get(4)).isSameAs(gav3);
        assertThat(list.get(5)).isSameAs(gav2);
        assertThat(list.get(6)).isSameAs(gav1);
        assertThat(list.get(7)).isSameAs(gav0);
    }

    @Test
    public void testGetEarliest() {
        List<ReleaseId> list = newUnsortedList();
        assertThat(ReleaseIdComparator.getEarliest(list)).isSameAs(gav0);
    }

    @Test
    public void testGetLatest() {
        List<ReleaseId> list = newUnsortedList();
        assertThat(ReleaseIdComparator.getLatest(list)).isSameAs(gav7);
    }

    @Test
    public void testResolveVersionPomModelNull() {
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("groupId", "artifactId", "${project.version}");
        assertThat(releaseId.getVersion()).isEqualTo("${project.version}");
    }

    @Test
    public void testResolveVersionNoProperty() {
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("groupId", "artifactId", "1.0.0");
        assertThat(releaseId.getVersion()).isEqualTo("1.0.0");
    }
}
