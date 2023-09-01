package org.kie.pmml.models.clustering.compiler.executor;

import java.io.Serializable;
import java.util.Map;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.clustering.ClusteringModel;
import org.junit.jupiter.api.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class ClusteringModelImplementationProviderTest {

    private static final String SOURCE_FILE = "SingleIrisKMeansClustering.pmml";

    private static final ClusteringModelImplementationProvider PROVIDER = new ClusteringModelImplementationProvider();

    private static ClusteringModel getModel(PMML pmml) {
        assertThat(pmml).isNotNull();
        assertThat(pmml.getModels()).hasSize(1);

        Model model = pmml.getModels().get(0);
        assertThat(model).isInstanceOf(ClusteringModel.class);

        return (ClusteringModel) model;
    }

    @Test
    void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.CLUSTERING_MODEL);
    }

    @Test
    void getKiePMMLModelWithSources() throws Exception {
        PMML pmml = TestUtils.loadFromFile(SOURCE_FILE);
        ClusteringModel model = getModel(pmml);
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       model,
                                                                       new PMMLCompilationContextMock(),
                                                                       SOURCE_FILE);
        KiePMMLModelWithSources retrieved = PROVIDER.getKiePMMLModelWithSources(compilationDTO);

        assertThat(retrieved).isNotNull();
        Map<String, String> sourcesMap = retrieved.getSourcesMap();
        assertThat(sourcesMap).isNotNull();
        assertThat(sourcesMap).isNotEmpty();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
        for (Class<?> clazz : compiled.values()) {
            assertThat(clazz).isInstanceOf(Serializable.class);
        }
    }
}
