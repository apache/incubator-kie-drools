/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.io.jaxb;

import java.time.Duration;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// TODO: Move the code to the jaxb-ri
public class JaxbDurationAdapter extends XmlAdapter<String, Duration> {

    @Override
    public Duration unmarshal(String durationString) {
        if (durationString == null) {
            return null;
        }
        return Duration.parse(durationString);
    }

    @Override
    public String marshal(Duration duration) {
        if (duration == null) {
            return null;
        }
        return duration.toString();
    }
}
