/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.service.processing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.taskassigning.model.processing.UserAttributesProcessor;
import org.kie.kogito.taskassigning.user.service.User;

import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.TASK_ASSIGNING_PROPERTY_PREFIX;

@ApplicationScoped
public class DefaultUserAttributesProcessor extends AbstractDefaultAttributesProcessor<User> implements UserAttributesProcessor {

    private static final String PROCESSOR_PREFIX = TASK_ASSIGNING_PROPERTY_PREFIX + ".default-user-attributes-processor";

    private static final String PROCESSOR_PRIORITY_PROPERTY_NAME = PROCESSOR_PREFIX + ".priority";

    private static final String PROCESSOR_ENABLED_PROPERTY_NAME = PROCESSOR_PREFIX + ".enabled";

    private static final String USER_SKILLS_ATTRIBUTE_PROPERTY_NAME = PROCESSOR_PREFIX + ".skills";

    private static final String USER_AFFINITIES_ATTRIBUTE_PROPERTY_NAME = PROCESSOR_PREFIX + ".affinities";

    @Inject
    @ConfigProperty(name = PROCESSOR_PRIORITY_PROPERTY_NAME, defaultValue = "0")
    int priority;

    @Inject
    @ConfigProperty(name = PROCESSOR_ENABLED_PROPERTY_NAME, defaultValue = "true")
    boolean enabled;

    @Inject
    @ConfigProperty(name = USER_SKILLS_ATTRIBUTE_PROPERTY_NAME, defaultValue = "skills")
    String skillsAttribute;

    @Inject
    @ConfigProperty(name = USER_AFFINITIES_ATTRIBUTE_PROPERTY_NAME, defaultValue = "affinities")
    String affinitiesAttribute;

    @Override
    protected Object getSkillsValue(User user) {
        return user.getAttributes() != null ? user.getAttributes().get(skillsAttribute) : null;
    }

    @Override
    protected Object getAffinitiesValue(User user) {
        return user.getAttributes() != null ? user.getAttributes().get(affinitiesAttribute) : null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}