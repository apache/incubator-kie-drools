package org.kie.internal.process;

import java.util.List;

import org.kie.api.internal.utils.KieService;

public interface CorrelationKeyFactory extends KieService {

    CorrelationKey newCorrelationKey(String businessKey);

    CorrelationKey newCorrelationKey(List<String> businessKeys);
}
