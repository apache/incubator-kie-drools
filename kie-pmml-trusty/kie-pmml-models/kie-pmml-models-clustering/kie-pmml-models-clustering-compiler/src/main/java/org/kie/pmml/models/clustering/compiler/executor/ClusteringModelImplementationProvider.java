package org.kie.pmml.models.clustering.compiler.executor;

import java.util.Map;

import org.dmg.pmml.clustering.ClusteringModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.clustering.compiler.dto.ClusteringCompilationDTO;
import org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringModelFactory;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Clustering</b>
 */
public class ClusteringModelImplementationProvider implements ModelImplementationProvider<ClusteringModel,KiePMMLClusteringModel>{

private static final Logger logger = LoggerFactory.getLogger(ClusteringModelImplementationProvider.class.getName());


    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.CLUSTERING_MODEL;
    }

    @Override
    public Class<KiePMMLClusteringModel> getKiePMMLModelClass() {
        return KiePMMLClusteringModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<ClusteringModel> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {}", compilationDTO);
        try {
            return KiePMMLClusteringModelFactory.getKiePMMLClusteringModelSourcesMap(ClusteringCompilationDTO.fromCompilationDTO(compilationDTO));
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
