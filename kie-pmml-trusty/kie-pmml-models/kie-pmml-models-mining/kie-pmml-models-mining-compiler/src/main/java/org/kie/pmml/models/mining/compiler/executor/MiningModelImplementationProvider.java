package org.kie.pmml.models.mining.compiler.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.mining.MiningModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.KiePMMLMiningModelWithSources;

import static org.kie.pmml.models.mining.model.KiePMMLMiningModel.PMML_MODEL_TYPE;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Mining</b>
 */
public class MiningModelImplementationProvider implements ModelImplementationProvider<MiningModel, KiePMMLMiningModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public Class<KiePMMLMiningModel> getKiePMMLModelClass() {
        return KiePMMLMiningModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<MiningModel> compilationDTO) {
        throw new KiePMMLException("MiningModelImplementationProvider.getSourcesMap is not meant to be invoked");
    }

    @Override
    public KiePMMLModelWithSources getKiePMMLModelWithSources(final CompilationDTO<MiningModel> compilationDTO) {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final Map<String, String> sourcesMap =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(MiningModelCompilationDTO.fromCompilationDTO(compilationDTO),
                                                                          nestedModels);
        return new KiePMMLMiningModelWithSources(compilationDTO.getFileName(),
                                                 compilationDTO.getModelName(),
                                                 compilationDTO.getPackageName(),
                                                 compilationDTO.getKieMiningFields(),
                                                 compilationDTO.getKieOutputFields(),
                                                 compilationDTO.getKieTargetFields(),
                                                 sourcesMap,
                                                 nestedModels);
    }
}
