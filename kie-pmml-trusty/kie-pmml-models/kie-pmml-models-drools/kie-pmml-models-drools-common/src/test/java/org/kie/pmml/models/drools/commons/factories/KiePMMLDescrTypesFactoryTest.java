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