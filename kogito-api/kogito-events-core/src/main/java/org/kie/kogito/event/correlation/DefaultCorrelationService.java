/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.correlation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationEncoder;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.CorrelationService;

public class DefaultCorrelationService implements CorrelationService {

    private static final Map<String, CorrelationInstance> correlationRepository = new ConcurrentHashMap<>();
    private static final Map<String, CorrelationInstance> correlatedRepository = new ConcurrentHashMap<>();

    private CorrelationEncoder correlationEncoder = new MD5CorrelationEncoder();

    @Override
    public CorrelationInstance create(Correlation correlation, String correlatedId) {
        String encodedCorrelationId = correlationEncoder.encode(correlation);
        CorrelationInstance correlationInstance = new CorrelationInstance(encodedCorrelationId, correlatedId, correlation);
        correlationRepository.put(encodedCorrelationId, correlationInstance);
        correlatedRepository.put(correlatedId, correlationInstance);
        return correlationInstance;
    }

    @Override
    public Optional<CorrelationInstance> find(Correlation correlation) {
        return Optional.ofNullable(correlationRepository.get(correlationEncoder.encode(correlation)));
    }

    @Override
    public Optional<CorrelationInstance> findByCorrelatedId(String correlatedId) {
        return Optional.ofNullable(correlatedRepository.get(correlatedId));
    }

    @Override
    public void delete(Correlation correlation) {
        CorrelationInstance removed = correlationRepository.remove(correlationEncoder.encode(correlation));
        correlatedRepository.remove(removed.getCorrelatedId());
    }

    public final void clear() {
        correlationRepository.clear();
        correlatedRepository.clear();
    }
}
