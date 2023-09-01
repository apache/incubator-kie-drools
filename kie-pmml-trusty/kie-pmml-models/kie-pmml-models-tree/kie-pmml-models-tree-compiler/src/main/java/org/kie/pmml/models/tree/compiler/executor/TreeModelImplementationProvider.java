package org.kie.pmml.models.tree.compiler.executor;

import java.util.Map;

import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.tree.compiler.dto.TreeCompilationDTO;
import org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Tree</b>
 */
public class TreeModelImplementationProvider implements ModelImplementationProvider<TreeModel, KiePMMLTreeModel> {

    private static final Logger logger = LoggerFactory.getLogger(TreeModelImplementationProvider.class.getName());

    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public Class<KiePMMLTreeModel> getKiePMMLModelClass() {
        return KiePMMLTreeModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<TreeModel> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(),
                     compilationDTO.getModel(),
                     compilationDTO.getPmmlContext());
        try {
            return KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(TreeCompilationDTO.fromCompilationDTO(compilationDTO));
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
