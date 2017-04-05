/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

import java.util.List;
import java.util.stream.Collectors;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.model.v1_1.ItemDefinition;

public final class ValidatorUtil {

    public static String rightOfHash(final String input) {
        return input.substring(input.indexOf("#") + 1);
    }

    public static ItemDefinition getRootItemDef(final ItemDefinition id) {
        ItemDefinition root = id;
        while (!(root.getParent() instanceof Definitions)) {
            root = (ItemDefinition) root.getParent();
        }
        return root;
    }

    public static String formatMessages(final List<DMNMessage> messages) {
        return messages.stream().map(Object::toString).collect( Collectors.joining( System.lineSeparator() ) );
    }

    private ValidatorUtil() {
        // It is forbidden to create new instances of util classes.
    }
}
