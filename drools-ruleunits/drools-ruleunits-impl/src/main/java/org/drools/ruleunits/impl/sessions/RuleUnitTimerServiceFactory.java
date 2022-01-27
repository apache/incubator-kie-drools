/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl.sessions;

import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.time.TimerService;
import org.drools.wiring.api.ComponentsFactory;

public class RuleUnitTimerServiceFactory {

    public static TimerService getTimerService(SessionConfiguration config) {
        TimerService service;
        switch (config.getClockType()) {
            case REALTIME_CLOCK:
                service = newTimerService((SessionConfigurationImpl) config);
                break;
            case PSEUDO_CLOCK:
                service = (TimerService) config.getClockType().createInstance();
                break;
            default:
                throw new IllegalArgumentException("Unsupported clock type: " + config.getClockType());
        }
        service.setTimerJobFactoryManager(config.getTimerJobFactoryManager());
        return service;
    }

    private static TimerService newTimerService(SessionConfigurationImpl config) {
        String className = config.getPropertyValue("drools.timerService", "org.drools.core.time.impl.JDKTimerService");
        if (className == null) {
            return null;
        }
        return (TimerService) ComponentsFactory.createTimerService(className);
    }
}
