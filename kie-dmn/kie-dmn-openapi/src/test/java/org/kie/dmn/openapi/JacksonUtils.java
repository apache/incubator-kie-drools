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

package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonUtils.class);

    public static void printoutJSON(Object tree) {
        if (LOG.isDebugEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                LOG.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree));
            } catch (Exception e) {
                LOG.error("error with Jackson serialization", e);
            }
        }
    }

    private JacksonUtils() {
        // no constructor for utility classes.
    }
}
