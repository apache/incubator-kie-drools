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
package org.kie.pmml.models.drools.commons.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class KiePMMLDescrTypesFactoryTest {

    private PackageDescrBuilder builder;

    @BeforeEach
    public void setUp() throws Exception {
        builder = DescrFactory.newPackage().name(PACKAGE_NAME);
    }

    @Test
    void declareTypes() {
        List<KiePMMLDroolsType> types = new ArrayList<>();
        types.add(KiePMMLDescrTestUtils.getDroolsType());
        types.add(KiePMMLDescrTestUtils.getDottedDroolsType());
        assertThat(builder.getDescr().getTypeDeclarations()).isEmpty();
        KiePMMLDescrTypesFactory.factory(builder).declareTypes(types);
        assertThat(builder.getDescr().getTypeDeclarations()).hasSize(2);
        IntStream.range(0, types.size())
                .forEach(i -> commonVerifyTypeDeclarationDescr(Objects.requireNonNull(types.get(i)), builder.getDescr().getTypeDeclarations().get(i)));
    }

    @Test
    void declareType() {
        KiePMMLDroolsType type = KiePMMLDescrTestUtils.getDroolsType();
        KiePMMLDescrTypesFactory.factory(builder).declareType(type);
        assertThat(builder.getDescr().getTypeDeclarations()).hasSize(1);
        commonVerifyTypeDeclarationDescr(type, builder.getDescr().getTypeDeclarations().get(0));
    }

    private void commonVerifyTypeDeclarationDescr(KiePMMLDroolsType type, final TypeDeclarationDescr typeDeclarationDescr) {
        String expectedGeneratedType = type.getName();
        String expectedMappedOriginalType = type.getType();
        assertThat(typeDeclarationDescr.getTypeName()).isEqualTo(expectedGeneratedType);
        assertThat(typeDeclarationDescr.getFields()).hasSize(1);
        assertThat(typeDeclarationDescr.getFields()).containsKey("value");
        assertThat(typeDeclarationDescr.getFields().get("value").getPattern().getObjectType()).isEqualTo(expectedMappedOriginalType);
    }
}