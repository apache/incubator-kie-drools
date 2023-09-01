package org.kie.dmn.validation.dtanalysis;

import java.util.List;
import java.util.Set;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

public interface InternalDMNDTAnalyser {

    List<DTAnalysis> analyse(DMNModel model, Set<DMNValidator.Validation> flags);

}
