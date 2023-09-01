package org.kie.scanner;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KieModuleMetaDataImplTest {

    @Test
    public void testIsProcessFile() {
        assertThat(KieModuleMetaDataImpl.isProcessFile("abc.bpmn")).isTrue();
        assertThat(KieModuleMetaDataImpl.isProcessFile("abc.bpmn2")).isTrue();
        assertThat(KieModuleMetaDataImpl.isProcessFile("abc.bpmn-cm")).isTrue();
        assertThat(KieModuleMetaDataImpl.isProcessFile("abc.bpmn2-cm")).isFalse();
    }

    @Test
    public void testIsFormFile() {
        assertThat(KieModuleMetaDataImpl.isFormFile("abc.frm")).isTrue();
        assertThat(KieModuleMetaDataImpl.isFormFile("abc.form")).isFalse();
    }
}