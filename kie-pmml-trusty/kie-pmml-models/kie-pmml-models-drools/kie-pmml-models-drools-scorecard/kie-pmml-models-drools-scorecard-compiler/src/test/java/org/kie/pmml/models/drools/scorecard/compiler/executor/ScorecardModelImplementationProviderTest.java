package org.kie.pmml.models.drools.scorecard.compiler.executor;

import java.io.FileInputStream;
import java.io.Serializable;

import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.drools.base.util.CloneUtil;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.ExternalizableMock;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.drools.util.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ScorecardModelImplementationProviderTest {

    private static final ScorecardModelImplementationProvider PROVIDER = new ScorecardModelImplementationProvider();
    private static final String SOURCE_1 = "ScorecardSample.pmml";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";

    @Test
    void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.SCORECARD_MODEL);
    }

    @Test
    void getKiePMMLModelWithSources() throws Exception {
        final PMML pmml = getPMML(SOURCE_1);
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       (Scorecard) pmml.getModels().get(0),
                                                                       new PMMLCompilationContextMock(), "FILENAME");
        final KiePMMLDroolsModelWithSources retrieved = PROVIDER.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        commonVerifyIsDeepCloneable(retrieved);
    }

    private PMML getPMML(String source) throws Exception {
        final FileInputStream fis = FileUtils.getFileInputStream(source);
        final PMML toReturn = KiePMMLUtil.load(fis, source);
        assertThat(toReturn).isNotNull();
        assertThat(toReturn.getModels()).hasSize(1);
        assertThat(toReturn.getModels().get(0)).isInstanceOf(Scorecard.class);
        return toReturn;
    }

    private void commonVerifyIsDeepCloneable(AbstractKiePMMLComponent toVerify) {
        assertThat(toVerify).isInstanceOf(Serializable.class);
        ExternalizableMock externalizableMock = new ExternalizableMock();
        externalizableMock.setKiePMMLComponent(toVerify);
        CloneUtil.deepClone(externalizableMock);
    }
}