/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.drools.commons.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KiePMMLDescrTypesFactoryTest {

    private static final String PACKAGE_NAME = "package";
    private PackageDescrBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = DescrFactory.newPackage().name(PACKAGE_NAME);
    }

    @Test
    public void declareTypes() {
        List<KiePMMLDroolsType> types = new ArrayList<>();
        types.add(KiePMMLDescrTestUtils.getDroolsType());
        types.add(KiePMMLDescrTestUtils.getDottedDroolsType());
        assertTrue(builder.getDescr().getTypeDeclarations().isEmpty());
        KiePMMLDescrTypesFactory.factory(builder).declareTypes(types);
        assertEquals(2, builder.getDescr().getTypeDeclarations().size());
        IntStream.range(0, types.size())
                .forEach(i -> commonVerifyTypeDeclarationDescr(Objects.requireNonNull(types.get(i)), builder.getDescr().getTypeDeclarations().get(i)));
    }

    @Test
    public void declareType() {
        KiePMMLDroolsType type = KiePMMLDescrTestUtils.getDroolsType();
        KiePMMLDescrTypesFactory.factory(builder).declareType(type);
        assertEquals(1, builder.getDescr().getTypeDeclarations().size());
        commonVerifyTypeDeclarationDescr(type, builder.getDescr().getTypeDeclarations().get(0));
    }

    private void commonVerifyTypeDeclarationDescr(KiePMMLDroolsType type, final TypeDeclarationDescr typeDeclarationDescr) {
        String expectedGeneratedType = type.getName();
        String expectedMappedOriginalType = type.getType();
        assertEquals(expectedGeneratedType, typeDeclarationDescr.getTypeName());
        assertEquals(1, typeDeclarationDescr.getFields().size());
        assertTrue(typeDeclarationDescr.getFields().containsKey("value"));
        assertEquals(expectedMappedOriginalType, typeDeclarationDescr.getFields().get("value").getPattern().getObjectType());
    }
}