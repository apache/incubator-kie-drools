/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.app.jobs.springboot;

import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.integrations.JobExceptionDetailsExtractor;
import org.kie.kogito.jobs.service.model.JobExecutionExceptionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for verifying WrappingConditionalJobExceptionDetailsExtractor correctly delegates
 * to CUSTOM implementations when provided.
 */
@SpringBootTest(classes = { WrappingConditionalJobExceptionDetailsExtractorCustomTest.TestConfiguration.class,
        WrappingConditionalJobExceptionDetailsExtractor.class })
@DirtiesContext
public class WrappingConditionalJobExceptionDetailsExtractorCustomTest {

    @Autowired
    WrappingConditionalJobExceptionDetailsExtractor extractor;

    /**
     * Test configuration that provides a custom extractor bean.
     * This bean is only active for this test class.
     */
    @Configuration
    public static class TestConfiguration {
        @Bean(name = "customTestExtractorForCustomTest")
        public JobExceptionDetailsExtractor customTestExtractor() {
            return new JobExceptionDetailsExtractor() {
                @Override
                public JobExecutionExceptionDetails extractExceptionDetails(Exception e) {
                    if (e == null) {
                        return null;
                    }
                    return new JobExecutionExceptionDetails("CUSTOM_EXTRACTOR", "Custom implementation was used");
                }
            };
        }
    }

    @Test
    public void testCustomExtractorIsUsedWhenProvided() {
        Exception testException = new RuntimeException("Test exception message");

        JobExecutionExceptionDetails result = extractor.extractExceptionDetails(testException);

        assertThat(result).isNotNull();
        assertThat(result.exceptionMessage()).isEqualTo("CUSTOM_EXTRACTOR");
        assertThat(result.exceptionDetails()).isEqualTo("Custom implementation was used");
    }
}
