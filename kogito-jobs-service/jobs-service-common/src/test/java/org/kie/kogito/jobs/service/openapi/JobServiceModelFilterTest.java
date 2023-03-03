/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.openapi;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.api.Test;

import static io.cloudevents.SpecVersion.V03;
import static io.cloudevents.SpecVersion.V1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.openapi.JobServiceModelFilter.JSON_NODE_SCHEMA;
import static org.kie.kogito.jobs.service.openapi.JobServiceModelFilter.RECIPIENT_SCHEMA;
import static org.kie.kogito.jobs.service.openapi.JobServiceModelFilter.SCHEDULE_SCHEMA;
import static org.kie.kogito.jobs.service.openapi.JobServiceModelFilter.SPEC_VERSION_SCHEMA;
import static org.kie.kogito.jobs.service.openapi.JobServiceModelFilter.TYPE_PROPERTY_NAME;

class JobServiceModelFilterTest {

    @Test
    void filterOpenAPI() {
        OpenAPI openAPI = OASFactory.createOpenAPI();
        Components components = OASFactory.createComponents()
                .addSchema(JSON_NODE_SCHEMA, OASFactory.createSchema().type(Schema.SchemaType.ARRAY))
                .addSchema(SPEC_VERSION_SCHEMA, OASFactory.createSchema().enumeration(Arrays.asList("V03", "V1")))
                .addSchema(RECIPIENT_SCHEMA, OASFactory.createSchema())
                .addSchema(SCHEDULE_SCHEMA, OASFactory.createSchema());
        openAPI.components(components);

        JobServiceModelFilter filter = new JobServiceModelFilter();
        filter.filterOpenAPI(openAPI);

        Schema jsonNodeSchema = openAPI.getComponents().getSchemas().get(JSON_NODE_SCHEMA);
        assertThat(jsonNodeSchema).isNotNull();
        assertThat(jsonNodeSchema.getType()).isEqualTo(Schema.SchemaType.OBJECT);

        Schema specVersionSchema = openAPI.getComponents().getSchemas().get(SPEC_VERSION_SCHEMA);
        assertThat(specVersionSchema).isNotNull();
        assertThat(specVersionSchema.getEnumeration()).containsExactly(V03.toString(), V1.toString());

        Schema recipientSchema = openAPI.getComponents().getSchemas().get(RECIPIENT_SCHEMA);
        assertSchemaDiscriminator(recipientSchema,
                Pair.of("http", "#/components/schemas/HttpRecipient"),
                Pair.of("sink", "#/components/schemas/SinkRecipient"));

        Schema scheduleSchema = openAPI.getComponents().getSchemas().get(SCHEDULE_SCHEMA);
        assertSchemaDiscriminator(scheduleSchema,
                Pair.of("cron", "#/components/schemas/CronSchedule"),
                Pair.of("timer", "#/components/schemas/TimerSchedule"));
    }

    @SafeVarargs
    private static void assertSchemaDiscriminator(Schema schema, Pair<String, String>... mappingEntries) {
        Map<String, String> expectedEntries = Arrays.stream(mappingEntries)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        assertThat(schema).isNotNull();
        assertThat(schema.getRequired()).containsExactly(TYPE_PROPERTY_NAME);
        Discriminator discriminator = schema.getDiscriminator();
        assertThat(discriminator).isNotNull();
        assertThat(discriminator.getPropertyName()).isEqualTo(TYPE_PROPERTY_NAME);
        assertThat(discriminator.getMapping()).containsExactlyInAnyOrderEntriesOf(expectedEntries);
    }
}
