/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package $Package$;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.kogito.decision.DecisionEventListenerConfig;
import org.kie.kogito.dmn.config.AbstractDecisionConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;

@org.springframework.stereotype.Component
class DecisionConfig extends AbstractDecisionConfig {

    @org.springframework.beans.factory.annotation.Autowired
    public DecisionConfig(
            List<DecisionEventListenerConfig> decisionEventListenerConfigs,
            List<DMNRuntimeEventListener> dmnRuntimeEventListeners) {
        super(decisionEventListenerConfigs, dmnRuntimeEventListeners);
    }

}
