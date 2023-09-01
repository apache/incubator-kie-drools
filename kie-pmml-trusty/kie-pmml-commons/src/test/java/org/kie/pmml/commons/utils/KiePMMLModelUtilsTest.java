package org.kie.pmml.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLModelUtilsTest {

    private static Map<String, String> packageNameMap;
    private static Map<String, String> classNameMap;

    @BeforeAll
    public static void setup() {
        packageNameMap = new HashMap<>();
        packageNameMap.put("a-dashed-name", "adashedname");
        packageNameMap.put("an_underscored_name", "anunderscoredname");
        packageNameMap.put("a spaced name", "aspacedname");
        packageNameMap.put("AnUpperCasedMame", "anuppercasedmame");
        packageNameMap.put("a_Mixed -name", "amixedname");
        packageNameMap.put("C:\\w-ind_ow Path", "cwindowpath");
        packageNameMap.put("a.Dotted.pA th", "a.dotted.path");

        classNameMap = new HashMap<>();
        classNameMap.put("a-dashed-name", "Adashedname");
        classNameMap.put("an_underscored_name", "Anunderscoredname");
        classNameMap.put("a spaced name", "Aspacedname");
        classNameMap.put("anUpperCasedName", "AnUpperCasedName");
        classNameMap.put("a.dotted.name", "Adottedname");
        classNameMap.put("a_.Mixed -name", "AMixedname");
        classNameMap.put("C:\\w-ind_ow Path", "CwindowPath");
    }

    @Test
    void getSanitizedPackageName() {
        packageNameMap.forEach((originalName, expectedName) -> assertThat(KiePMMLModelUtils.getSanitizedPackageName(originalName)).isEqualTo(expectedName));
    }

    @Test
    void getSanitizedClassName() {
        classNameMap.forEach((originalName, expectedName) -> assertThat(KiePMMLModelUtils.getSanitizedClassName(originalName)).isEqualTo(expectedName));
    }
}