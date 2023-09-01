package org.kie.pmml.compiler.commons.implementations;

import java.util.Optional;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.mining.MiningModel;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.util.FileUtils.getFileInputStream;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getPMMLWithMiningRandomTestModel;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getPMMLWithRandomTestModel;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSources;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled;

public class KiePMMLModelRetrieverTest {

    private static final String MULTIPLE_TARGETS_SOURCE = "MultipleTargetsFieldSample.pmml";
    private static final String ONE_MINING_TARGET_SOURCE = "OneMiningTargetFieldSample.pmml";
    private static final String MINING_MODEL_WITH_NESTED_REFERS_SOURCE = "MiningWithNestedRefers.pmml";
    private PMML pmml;

    @Test
    void getFromCommonDataAndTransformationDictionaryAndModelWithSourcesWithProvider() {
        pmml = getPMMLWithRandomTestModel();
        final CommonCompilationDTO compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       pmml.getModels().get(0),
                                                                       new PMMLCompilationContextMock(), "fileName");
        final Optional<KiePMMLModel> retrieved =
                getFromCommonDataAndTransformationDictionaryAndModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getFromDataDictionaryAndModelWithSourcesWithoutProvider() throws Exception {
        String fileName = ONE_MINING_TARGET_SOURCE.substring(0, ONE_MINING_TARGET_SOURCE.lastIndexOf('.'));
        pmml = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final CommonCompilationDTO compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       pmml.getModels().get(0),
                                                                       new PMMLCompilationContextMock(), fileName);
        final Optional<KiePMMLModel> retrieved =
                getFromCommonDataAndTransformationDictionaryAndModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiledWithProvider() throws Exception {
        pmml = getPMMLWithMiningRandomTestModel();
        MiningModel parentModel = (MiningModel) pmml.getModels().get(0);
        Model model = parentModel.getSegmentation().getSegments().get(0).getModel();
        final CommonCompilationDTO compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       model,
                                                                       new PMMLCompilationContextMock(), "fileName");
        final Optional<KiePMMLModel> retrieved =
                getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled(compilationDTO);
        assertThat(retrieved).isNotNull().isPresent();
    }

    @Test
    void getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiledWithoutProvider() throws Exception {
        String fileName = MINING_MODEL_WITH_NESTED_REFERS_SOURCE.substring(0,
                                                                           MINING_MODEL_WITH_NESTED_REFERS_SOURCE.lastIndexOf('.'));
        pmml = KiePMMLUtil.load(getFileInputStream(MINING_MODEL_WITH_NESTED_REFERS_SOURCE),
                                MINING_MODEL_WITH_NESTED_REFERS_SOURCE);
        MiningModel parentModel = (MiningModel) pmml.getModels().get(0);
        Model model = parentModel.getSegmentation().getSegments().get(0).getModel();
        final CommonCompilationDTO compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       model,
                                                                       new PMMLCompilationContextMock(), fileName);
        final Optional<KiePMMLModel> retrieved =
                getFromCommonDataAndTransformationDictionaryAndModelWithSourcesCompiled(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isNotPresent();
    }

}