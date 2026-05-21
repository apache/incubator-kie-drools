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
package org.kie.kogito.app.jobs.quarkus;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.app.jobs.integrations.JobExceptionDetailsExtractor;
import org.kie.kogito.jobs.service.model.JobExecutionExceptionDetails;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for verifying WrappingConditionalJobExceptionDetailsExtractor correctly delegates
 * to CUSTOM implementations when provided.
 */
@QuarkusTest
@TestProfile(WrappingConditionalJobExceptionDetailsExtractorCustomTest.CustomExtractorProfile.class)
public class WrappingConditionalJobExceptionDetailsExtractorCustomTest {

    @Inject
    WrappingConditionalJobExceptionDetailsExtractor extractor;

    /**
     * Custom test extractor that returns marker values.
     * Uses @Alternative so it's only active when enabled by the test profile.
     */
    @Alternative
    @ApplicationScoped
    public static class CustomTestExtractor implements JobExceptionDetailsExtractor {
        @Override
        public JobExecutionExceptionDetails extractExceptionDetails(Exception e) {
            if (e == null) {
                return null;
            }
            return new JobExecutionExceptionDetails("CUSTOM_EXTRACTOR", "Custom implementation was used");
        }
    }

    /**
     * Test profile that enables the custom extractor alternative
     */
    public static class CustomExtractorProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(CustomTestExtractor.class);
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
