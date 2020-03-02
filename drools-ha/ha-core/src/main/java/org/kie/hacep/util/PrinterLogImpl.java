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
package org.kie.hacep.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrinterLogImpl implements Printer {

    private static Logger logger = LoggerFactory.getLogger(PrinterLogImpl.class);

    public boolean prettyPrinter(String caller, String topic, int partition, long offset, String value,  boolean processed) {
        if (logger.isInfoEnabled()) {
            logger.info("Caller:{} - Processed:{} - Topic: {} - Partition: {} - Offset: {} - Value: {}\n",
                        caller, processed, topic, partition, offset, value);
            return true;
        }
        return false;
    }
}

