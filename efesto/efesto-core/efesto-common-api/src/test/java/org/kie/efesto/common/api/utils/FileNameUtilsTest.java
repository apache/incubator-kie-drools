package org.kie.efesto.common.api.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameUtilsTest {


    @Test
    void getFileName() {
        String fileName = "file_name.txt";
        String source = fileName;
        assertThat(FileNameUtils.getFileName(source)).isEqualTo(fileName);
        source = File.separator + "dir" + File.separator + fileName;
        assertThat(FileNameUtils.getFileName(source)).isEqualTo(fileName);
    }

    @Test
    void getSuffix() {
        String fileName = "file_name.model_json";
        String expected = "model_json";
        String source = fileName;
        assertThat(FileNameUtils.getSuffix(source)).isEqualTo(expected);
        source = File.separator + "dir" + File.separator + fileName;
        assertThat(FileNameUtils.getSuffix(source)).isEqualTo(expected);
    }

}