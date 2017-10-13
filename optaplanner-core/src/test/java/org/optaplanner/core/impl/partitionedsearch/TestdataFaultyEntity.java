/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.partitionedsearch;

import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestdataFaultyEntity extends TestdataEntity {

    private static final Logger logger = LoggerFactory.getLogger(TestdataFaultyEntity.class);

    public TestdataFaultyEntity() {
    }

    public TestdataFaultyEntity(String code) {
        super(code);
    }

    @Override
    public void setValue(TestdataValue value) {
        super.setValue(value);
        if (Thread.currentThread().getName().matches("OptaPool-\\d+-PartThread-\\d+")) {
            logger.info("Throwing exception on a partition thread.");
            throw new TestException();
        }
    }

    public static class TestException extends RuntimeException {

        public TestException() {
            super("Unexpected solver failure.");
        }
    }
}
