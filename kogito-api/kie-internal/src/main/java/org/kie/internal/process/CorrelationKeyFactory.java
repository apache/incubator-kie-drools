package org.kie.internal.process;

import java.util.List;

public interface CorrelationKeyFactory {

    CorrelationKey newCorrelationKey(String businessKey);
    
    CorrelationKey newCorrelationKey(List<String> properties);
}
