/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class KiePMMLModelUtilsTest {

    private static Map<String, String> packageNameMap;
    private static Map<String, String> classNameMap;

    @BeforeClass
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
    public void getSanitizedPackageName() {
        packageNameMap.forEach((originalName, expectedName) -> assertEquals(expectedName, KiePMMLModelUtils.getSanitizedPackageName(originalName)));
    }

    @Test
    public void getSanitizedClassName() {
        classNameMap.forEach((originalName, expectedName) -> assertEquals(expectedName, KiePMMLModelUtils.getSanitizedClassName(originalName)));
    }
}