/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.mvel.compiler.kproject;

import java.util.ArrayList;
import java.util.List;

import org.appformer.maven.support.AFReleaseIdImpl;
import org.appformer.maven.support.PomModel;
import org.assertj.core.api.Assertions;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.ReleaseIdComparator;

import static org.junit.Assert.assertSame;
import static org.kie.api.builder.ReleaseIdComparator.SortDirection.ASCENDING;
import static org.kie.api.builder.ReleaseIdComparator.SortDirection.DESCENDING;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertSame(gav0, list.get(0));
        assertSame(gav1, list.get(1));
        assertSame(gav2, list.get(2));
        assertSame(gav3, list.get(3));
        assertSame(gav4, list.get(4));
        assertSame(gav5, list.get(5));
        assertSame(gav6, list.get(6));
        assertSame(gav7, list.get(7));
    }

    @Test
    public void testAscendingSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator(ASCENDING));
        assertSame(gav0, list.get(0));
        assertSame(gav1, list.get(1));
        assertSame(gav2, list.get(2));
        assertSame(gav3, list.get(3));
        assertSame(gav4, list.get(4));
        assertSame(gav5, list.get(5));
        assertSame(gav6, list.get(6));
        assertSame(gav7, list.get(7));
    }

    @Test
    public void testDecendingSort() {
        List<ReleaseId> list = newUnsortedList();
        list.sort(new ReleaseIdComparator(DESCENDING));
        assertSame(gav7, list.get(0));
        assertSame(gav6, list.get(1));
        assertSame(gav5, list.get(2));
        assertSame(gav4, list.get(3));
        assertSame(gav3, list.get(4));
        assertSame(gav2, list.get(5));
        assertSame(gav1, list.get(6));
        assertSame(gav0, list.get(7));
    }

    @Test
    public void testGetEarliest() {
        List<ReleaseId> list = newUnsortedList();
        assertSame(gav0, ReleaseIdComparator.getEarliest(list));
    }

    @Test
    public void testGetLatest() {
        List<ReleaseId> list = newUnsortedList();
        assertSame(gav7, ReleaseIdComparator.getLatest(list));
    }

    @Test
    public void testResolveVersionPomModelNull() {
        final org.appformer.maven.support.AFReleaseIdImpl releaseId = new ReleaseIdImpl("groupId", "artifactId", "${project.version}");
        ReleaseId resultReleaseId = ReleaseIdImpl.adapt(releaseId, null);
        Assertions.assertThat(resultReleaseId.getVersion()).isEqualTo("${project.version}");
    }

    @Test
    public void testResolveVersionNoProperty() {
        final org.appformer.maven.support.AFReleaseIdImpl releaseId = new ReleaseIdImpl("groupId", "artifactId", "1.0.0");
        final PomModel pomModel = mock(PomModel.class);
        ReleaseId resultReleaseId = ReleaseIdImpl.adapt(releaseId, pomModel);
        Assertions.assertThat(resultReleaseId.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    public void testResolveVersionProjectVersion() {
        testResolveVersion(false);
    }

    @Test
    public void testResolveVersionParentVersion() {
        testResolveVersion(true);
    }

    private void testResolveVersion(final boolean parentVersion) {
        final org.appformer.maven.support.AFReleaseIdImpl dependencyReleaseId;
        if (parentVersion) {
            dependencyReleaseId = new AFReleaseIdImpl("groupId", "artifactId", "${project.parent.version}");
        } else {
            dependencyReleaseId = new AFReleaseIdImpl("groupId", "artifactId", "${project.version}");
        }
        final org.appformer.maven.support.AFReleaseIdImpl projectReleaseId = new ReleaseIdImpl("groupId", "projectArtifactId", "1.0.1");

        final PomModel pomModel = mock(PomModel.class);
        if (parentVersion) {
            when(pomModel.getParentReleaseId()).thenReturn(projectReleaseId);
        } else {
            when(pomModel.getReleaseId()).thenReturn(projectReleaseId);
        }

        ReleaseId resultReleaseId = ReleaseIdImpl.adapt(dependencyReleaseId, pomModel);
        Assertions.assertThat(resultReleaseId.getVersion()).isEqualTo("1.0.1");
    }
}
