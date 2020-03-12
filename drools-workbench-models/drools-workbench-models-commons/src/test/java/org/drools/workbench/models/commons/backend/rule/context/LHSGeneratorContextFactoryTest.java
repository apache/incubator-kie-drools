/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.commons.backend.rule.context;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LHSGeneratorContextFactoryTest {

    private LHSGeneratorContextFactory factory;

    @Before
    public void setup() {
        this.factory = new LHSGeneratorContextFactory();
    }

    @Test
    public void testNewGeneratorContext() {
        factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);

        factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(0);
    }

    @Test
    public void testNewChildGeneratorContextPattern() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);

        factory.newChildGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(1);

        factory.newChildGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getGeneratorContexts()).hasSize(3);
        assertThat(factory.getGeneratorContexts().get(2).getDepth()).isEqualTo(2);
    }

    @Test
    public void testNewChildGeneratorContextFieldConstraint() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);

        factory.newChildGeneratorContext(root, mock(FieldConstraint.class));

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(1);

        factory.newChildGeneratorContext(root, mock(FieldConstraint.class));

        assertThat(factory.getGeneratorContexts()).hasSize(3);
        assertThat(factory.getGeneratorContexts().get(2).getDepth()).isEqualTo(2);
    }

    @Test
    public void testNewChildGeneratorContextMixedPatternFieldConstraint() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);

        factory.newChildGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(1);

        factory.newChildGeneratorContext(root, mock(FieldConstraint.class));

        assertThat(factory.getGeneratorContexts()).hasSize(3);
        assertThat(factory.getGeneratorContexts().get(2).getDepth()).isEqualTo(2);
    }

    @Test
    public void testNewPeerGeneratorContextPattern() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(0).getOffset()).isEqualTo(0);

        final LHSGeneratorContext peer = factory.newPeerGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(1).getOffset()).isEqualTo(1);

        factory.newPeerGeneratorContext(peer, mock(IPattern.class));

        assertThat(factory.getGeneratorContexts()).hasSize(3);
        assertThat(factory.getGeneratorContexts().get(2).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(2).getOffset()).isEqualTo(2);
    }

    @Test
    public void testNewPeerGeneratorContextFieldConstraint() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(0).getOffset()).isEqualTo(0);

        final LHSGeneratorContext peer = factory.newPeerGeneratorContext(root, mock(FieldConstraint.class));

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(1).getOffset()).isEqualTo(1);

        factory.newPeerGeneratorContext(peer, mock(FieldConstraint.class));

        assertThat(factory.getGeneratorContexts()).hasSize(3);
        assertThat(factory.getGeneratorContexts().get(2).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(2).getOffset()).isEqualTo(2);
    }

    @Test
    public void testNewNewPeerGeneratorContextMixedPatternFieldConstraint() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getGeneratorContexts()).hasSize(1);
        assertThat(factory.getGeneratorContexts().get(0).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(0).getOffset()).isEqualTo(0);

        factory.newPeerGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getGeneratorContexts()).hasSize(2);
        assertThat(factory.getGeneratorContexts().get(1).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(1).getOffset()).isEqualTo(1);

        factory.newPeerGeneratorContext(root, mock(FieldConstraint.class));

        assertThat(factory.getGeneratorContexts()).hasSize(3);
        assertThat(factory.getGeneratorContexts().get(2).getDepth()).isEqualTo(0);
        assertThat(factory.getGeneratorContexts().get(2).getOffset()).isEqualTo(1);
    }

    @Test
    public void testGetMaximumDepth() {
        final LHSGeneratorContext root = factory.newGeneratorContext();
        assertThat(factory.getMaximumDepth()).isEqualTo(0);

        factory.newChildGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getMaximumDepth()).isEqualTo(1);
    }

    @Test
    public void testGetMaximumPatternDepth() {
        final LHSGeneratorContext root = factory.newGeneratorContext();

        assertThat(factory.getMaximumPatternDepth()).isEqualTo(0);

        factory.newChildGeneratorContext(root, mock(IPattern.class));

        assertThat(factory.getMaximumPatternDepth()).isEqualTo(1);

        factory.newChildGeneratorContext(root, mock(FieldConstraint.class));

        assertThat(factory.getMaximumPatternDepth()).isEqualTo(1);
    }

    @Test
    public void testGetPeers() {
        final LHSGeneratorContext root = factory.newGeneratorContext();
        final LHSGeneratorContext peer1 = factory.newPeerGeneratorContext(root, mock(FieldConstraint.class));
        final LHSGeneratorContext peer2 = factory.newPeerGeneratorContext(peer1, mock(FieldConstraint.class));

        final List<LHSGeneratorContext> peers = factory.getPeers(root);
        assertThat(peers).contains(peer1, peer2);
    }
}
