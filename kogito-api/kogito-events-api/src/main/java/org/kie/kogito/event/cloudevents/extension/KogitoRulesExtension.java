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
package org.kie.kogito.event.cloudevents.extension;

import java.util.Set;

import io.cloudevents.CloudEventExtension;
import io.cloudevents.CloudEventExtensions;
import io.cloudevents.core.provider.ExtensionProvider;

import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.RULE_UNIT_ID;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.RULE_UNIT_QUERY;
import static org.kie.kogito.event.cloudevents.extension.KogitoExtensionUtils.readStringExtension;

public class KogitoRulesExtension implements CloudEventExtension {

    private static final Set<String> KEYS = Set.of(RULE_UNIT_ID, RULE_UNIT_QUERY);

    private String ruleUnitId;
    private String ruleUnitQuery;

    public static void register() {
        ExtensionProvider.getInstance().registerExtension(KogitoRulesExtension.class, KogitoRulesExtension::new);
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        readStringExtension(extensions, RULE_UNIT_ID, this::setRuleUnitId);
        readStringExtension(extensions, RULE_UNIT_QUERY, this::setRuleUnitQuery);
    }

    @Override
    public Object getValue(String key) throws IllegalArgumentException {
        switch (key) {
            case RULE_UNIT_ID:
                return getRuleUnitId();
            case RULE_UNIT_QUERY:
                return getRuleUnitQuery();
            default:
                return null;
        }
    }

    @Override
    public Set<String> getKeys() {
        return KEYS;
    }

    public String getRuleUnitId() {
        return ruleUnitId;
    }

    public void setRuleUnitId(String ruleUnitId) {
        this.ruleUnitId = ruleUnitId;
    }

    public String getRuleUnitQuery() {
        return ruleUnitQuery;
    }

    public void setRuleUnitQuery(String ruleUnitQuery) {
        this.ruleUnitQuery = ruleUnitQuery;
    }
}
