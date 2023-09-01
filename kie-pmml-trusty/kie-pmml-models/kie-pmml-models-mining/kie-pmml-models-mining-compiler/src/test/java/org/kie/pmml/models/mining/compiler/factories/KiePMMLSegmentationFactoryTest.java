package org.kie.pmml.models.mining.compiler.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.mining.MiningModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class KiePMMLSegmentationFactoryTest extends AbstractKiePMMLFactoryTest {

    @BeforeAll
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
    }

    @Test
    void getSegmentationSourcesMap() {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final Map<String, String> retrieved = KiePMMLSegmentationFactory.getSegmentationSourcesMap(compilationDTO,
                nestedModels);
        assertThat(retrieved).isNotNull();
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertThat(nestedModels).hasSize(expectedNestedModels);
    }

}