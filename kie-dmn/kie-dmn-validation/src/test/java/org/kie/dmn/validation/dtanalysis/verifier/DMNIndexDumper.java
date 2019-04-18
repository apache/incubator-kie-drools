/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis.verifier;

import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.meta.ConditionParent;
import org.drools.verifier.core.index.model.meta.ConditionParentType;
import org.kie.dmn.validation.dtanalysis.DMNAction;

public class DMNIndexDumper {

    private DMNIndexDumper() {
    }

    public static String dump(Index index) {
        StringBuilder builder = new StringBuilder();

        for (Rule rule : index.getRules()
                .where(Rule.uuid().any())
                .select()
                .all()) {

            builder.append(dump(rule));
            builder.append("\n");
        }

        return builder.toString();
    }

    public static String dump(Rule rule) {
        StringBuilder builder = new StringBuilder();

        for (Condition condition : rule.getConditions().where(Condition.uuid().any()).select().all()) {
            builder.append(dump(condition));
            builder.append(" ");
        }

        builder.append(" ### ");

        for (Action action : rule.getActions().where(Action.uuid().any()).select().all()) {
            builder.append(dump(action));
            builder.append(" ");
        }

        return builder.toString();
    }

    public static String dump(Action action) {
        StringBuilder builder = new StringBuilder();

        if (action instanceof DMNAction) {
            DMNAction dmnAction = (DMNAction) action;
            builder.append(dmnAction.getValues());
        }

        return builder.toString();
    }

    public static String dump(Condition condition) {
        StringBuilder builder = new StringBuilder();

        if (condition instanceof DMNCondition) {
            DMNCondition dmnCondition = (DMNCondition) condition;
            builder.append(dump(dmnCondition.getField()));
            builder.append(dmnCondition.getOperator());
            builder.append(dmnCondition.getValues());
        }

        return builder.toString();
    }

    public static String dump(ConditionParent field) {
        StringBuilder builder = new StringBuilder();

        if (field instanceof DMNCell) {
            DMNCell cell = (DMNCell) field;
            builder.append(dump(cell.getConditionParentType()));
            builder.append("DMNCell.id=");
            builder.append(dump(cell.getUuidKey()));
            builder.append(":");
            builder.append(cell.toHumanReadableString());
        }

        return builder.toString();
    }

    private static String dump(ConditionParentType conditionParentType) {
        StringBuilder builder = new StringBuilder();

        builder.append("conditionParentType.id=");
        builder.append(dump(conditionParentType.getUuidKey()));

        return builder.toString();
    }

    public static String dump(UUIDKey uuidKey) {
        StringBuilder builder = new StringBuilder();

        builder.append("[");
        builder.append(uuidKey.getValues());
        builder.append("]");

        return builder.toString();
    }
}
