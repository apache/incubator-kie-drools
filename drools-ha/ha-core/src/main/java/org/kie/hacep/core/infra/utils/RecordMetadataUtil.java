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
package org.kie.hacep.core.infra.utils;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordMetadataUtil {

    private static final Logger logger = LoggerFactory.getLogger(RecordMetadataUtil.class);

    public static RecordMetadata logRecord(RecordMetadata recordMetadata) {
        if (recordMetadata != null && logger.isInfoEnabled()) {
            logger.info("Topic: {} - Partition: {} - Offset: {} - TimeStamp: {}\n",
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
        }

        return recordMetadata;
    }
}
