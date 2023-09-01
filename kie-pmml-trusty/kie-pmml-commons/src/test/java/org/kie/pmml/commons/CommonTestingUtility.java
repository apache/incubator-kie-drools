package org.kie.pmml.commons;

import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.identifiers.LocalComponentIdRedirectPmml;
import org.kie.pmml.api.identifiers.PmmlIdFactory;
import org.kie.pmml.api.identifiers.PmmlIdRedirectFactory;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class CommonTestingUtility {

    public static ProcessingDTO getProcessingDTO(KiePMMLModel model, List<KiePMMLNameValue> kiePMMLNameValues) {
        return new ProcessingDTO(model, kiePMMLNameValues);
    }

    public static ProcessingDTO getProcessingDTO(List<KiePMMLNameValue> kiePMMLNameValues) {
        return new ProcessingDTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                 Collections.emptyList(), kiePMMLNameValues, Collections.emptyList(),
                                 Collections.emptyList());
    }

    public static ProcessingDTO getProcessingDTO(List<KiePMMLDerivedField> derivedFields, List<KiePMMLNameValue> kiePMMLNameValues) {
        return new ProcessingDTO(Collections.emptyList(), derivedFields, Collections.emptyList(),
                                 Collections.emptyList(), kiePMMLNameValues, Collections.emptyList(),
                                 Collections.emptyList());
    }

    public static ProcessingDTO getProcessingDTO(List<KiePMMLOutputField> outputFields,
                                                 List<KiePMMLNameValue> kiePMMLNameValues, List<String> reasonCodes) {
        return new ProcessingDTO(Collections.emptyList(), Collections.emptyList(), outputFields,
                                 Collections.emptyList(), kiePMMLNameValues, Collections.emptyList(), reasonCodes);
    }

    public static ProcessingDTO getProcessingDTO(List<KiePMMLDefineFunction> defineFunctions,
                                                 List<KiePMMLDerivedField> derivedFields,
                                                 List<KiePMMLNameValue> kiePMMLNameValues,
                                                 List<MiningField> miningFields) {
        return new ProcessingDTO(defineFunctions, derivedFields, Collections.emptyList(), Collections.emptyList(),
                                 kiePMMLNameValues, miningFields, Collections.emptyList());
    }

    public static LocalComponentIdPmml getModelLocalUriIdFromPmmlIdFactory(String fileName, String modelName) {
        return new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdFactory.class)
                .get(fileName, getSanitizedClassName(modelName));
    }

    public static LocalComponentIdRedirectPmml getModelLocalUriIdFromPmmlIdRedirectFactory(String redirectModel,
                                                                                           String fileName,
                                                                                           String modelName) {
        return new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdRedirectFactory.class)
                .get(redirectModel, fileName, getSanitizedClassName(modelName));
    }
}
