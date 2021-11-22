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
package org.kie.kogito.jobs;

import java.text.MessageFormat;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobIdResolver {

    private static final MessageFormat format = new MessageFormat("{0}:");
    private static final Logger LOGGER = LoggerFactory.getLogger(JobIdResolver.class);

    public static JobId resolve(String value) {
        try {
            String type = (String) format.parse(value)[0];
            switch (type) {
                case AsyncJobId.TYPE:
                    return new AsyncJobId().decode(value);
                case TimerJobId.TYPE:
                default:
                    return new TimerJobId().decode(value);
            }
        } catch (ParseException e) {
            LOGGER.error("Error parsing JobId {}", value, e);
            return null;
        }
    }
}
