/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.remote.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientUtils {

    public static final String CONSUMER_CONF = "consumer.properties";
    public static final String PRODUCER_CONF = "producer.properties";
    public static final String CONF = "configuration.properties";
    private static Logger logger = LoggerFactory.getLogger(ClientUtils.class);

    private ClientUtils() { }

    public static Properties getConfiguration(String filename) {
        Properties props = new Properties();
        try (InputStream in = ClientUtils.class.getClassLoader().getResourceAsStream(filename)) {
            props.load(in);
        } catch (IOException e) {
            logger.error(e.getMessage(),
                         e);
        }
        return props;
    }
}
