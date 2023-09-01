package org.kie.internal.builder;

import com.sun.tools.xjc.Options;
import org.kie.api.internal.utils.KieService;

public interface JaxbConfigurationFactoryService extends KieService {
    JaxbConfiguration newJaxbConfiguration( Options xjcOpts, String systemId);
}
