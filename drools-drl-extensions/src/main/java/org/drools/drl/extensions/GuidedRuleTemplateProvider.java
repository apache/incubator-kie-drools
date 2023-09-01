package org.drools.drl.extensions;

import java.io.IOException;
import java.io.InputStream;

import org.kie.api.internal.utils.KieService;

public interface GuidedRuleTemplateProvider extends KieService {

    ResourceConversionResult loadFromInputStream(InputStream is) throws IOException;

}
