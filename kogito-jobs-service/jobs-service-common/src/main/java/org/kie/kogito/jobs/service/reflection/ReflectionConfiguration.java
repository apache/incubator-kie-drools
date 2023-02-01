/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.reflection;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Placeholder for registering classes for reflection instead of using reflection-config.json approach or tagging
 * them individually.
 */
@RegisterForReflection(
        classNames = {
                "org.kie.kogito.event.cloudevents.SpecVersionSerializer",
                "org.kie.kogito.event.AbstractDataEvent",
                "org.kie.kogito.jobs.service.events.JobDataEvent",
                "org.kie.kogito.jobs.service.repository.marshaller.RecipientMarshaller$HTTPRecipientAccessor",
                "org.kie.kogito.jobs.service.job.model.ScheduledJobAdapter$ProcessPayload",
                "org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller$PointInTimeTriggerAccessor",
                "org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller$IntervalTriggerAccessor",
                "org.kie.kogito.jobs.api.event.CancelJobRequestEvent$JobId" })
public class ReflectionConfiguration {
}
