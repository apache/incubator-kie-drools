package org.kie.pmml.models.scorecard.compiler.executor;

import java.util.Map;

import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.scorecard.compiler.ScorecardCompilationDTO;
import org.kie.pmml.models.scorecard.compiler.factories.KiePMMLScorecardModelFactory;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Scorecard</b>
 */
public class ScorecardModelImplementationProvider implements ModelImplementationProvider<Scorecard,
        KiePMMLScorecardModel> {

    private static final Logger logger = LoggerFactory.getLogger(ScorecardModelImplementationProvider.class.getName());

    @Override
    public PMML_MODEL getPMMLModelType() {
        logger.trace("getPMMLModelType");
        return PMML_MODEL.SCORECARD_MODEL;
    }

    @Override
    public Class<KiePMMLScorecardModel> getKiePMMLModelClass() {
        return KiePMMLScorecardModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<Scorecard> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(),
                     compilationDTO.getModel(),
                     compilationDTO.getPmmlContext());
        try {
            ScorecardCompilationDTO scorecardCompilationDTO =
                    ScorecardCompilationDTO.fromCompilationDTO(compilationDTO);
            return
                    KiePMMLScorecardModelFactory.getKiePMMLScorecardModelSourcesMap(scorecardCompilationDTO);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }
}
