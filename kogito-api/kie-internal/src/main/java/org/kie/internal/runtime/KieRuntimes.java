package org.kie.internal.runtime;

import org.kie.internal.utils.KieService;

import java.util.Map;

public interface KieRuntimes extends KieService {
    Map<String, Object> getRuntimes();
}
