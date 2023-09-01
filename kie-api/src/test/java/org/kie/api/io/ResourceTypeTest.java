package org.kie.api.io;

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceTypeTest {

    @Test
    public void testBPMN2Extension() {
        final ResourceType BPMN2 = ResourceType.BPMN2;

        assertThat(BPMN2.matchesExtension("abc.bpmn")).isTrue();
        assertThat(BPMN2.matchesExtension("abc.bpmn2")).isTrue();
        assertThat(BPMN2.matchesExtension("abc.bpmn-cm")).isTrue();
        assertThat(BPMN2.matchesExtension("abc.bpmn2-cm")).isFalse();
    }

    @Test
    public void testGetAllExtensions() throws Exception {
        final ResourceType BPMN2 = ResourceType.BPMN2;
        final List<String> extensionsBPMN2 = BPMN2.getAllExtensions();

        assertThat(extensionsBPMN2.size()).isEqualTo(3);
        assertThat(extensionsBPMN2.contains("bpmn")).isTrue();
        assertThat(extensionsBPMN2.contains("bpmn2")).isTrue();
        assertThat(extensionsBPMN2.contains("bpmn-cm")).isTrue();
        assertThat(extensionsBPMN2.contains("bpmn2-cm")).isFalse();

        final ResourceType DRL = ResourceType.DRL;
        final List<String> extensionsDRL = DRL.getAllExtensions();

        assertThat(extensionsDRL.size()).isEqualTo(1);
        assertThat(extensionsDRL.contains("drl")).isTrue();
    }

    @Test
    public void testDetermineResourceType() {
        assertThat(ResourceType.determineResourceType("test.drl.xls")).isEqualTo(ResourceType.DTABLE);
        assertThat(ResourceType.determineResourceType("test.xls")).isNull();
    }
}