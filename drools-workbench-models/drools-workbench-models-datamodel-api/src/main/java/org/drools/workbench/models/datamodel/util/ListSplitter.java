/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.workbench.models.datamodel.util;

import java.util.ArrayList;
import java.util.List;

public class ListSplitter {

    public static String[] split(final String valueList) {
        return split("'",
                     false,
                     valueList);
    }

    /**
     *
     * @param quoteCharacter Character used to surround the items in the list.
     * @param trim If true string values between , will be trimmed. If false the spaces are included.
     * @param valueList Comma separated list of items.
     * @return An array of String items.
     */
    public static String[] split(final String quoteCharacter,
                                 final boolean trim,
                                 final String valueList) {
        final String[] split = valueList.split(",");
        return new InnerSplitter(quoteCharacter,
                                 trim,
                                 split).getSplit();
    }
}

class InnerSplitter {

    private final List<String> result = new ArrayList<>();
    private final String quoteCharacter;

    private String current = null;

    InnerSplitter(final String quoteCharacter,
                  final boolean trim,
                  final String[] split) {

        this.quoteCharacter = quoteCharacter;

        for (final String item : split) {

            if (current == null) {
                if (item.trim().startsWith(this.quoteCharacter) && item.trim().endsWith(this.quoteCharacter)) {
                    if (item.length() == 1) {
                        result.add(item);
                    } else {
                        result.add(item.substring(item.indexOf(this.quoteCharacter) + 1, item.lastIndexOf(this.quoteCharacter)));
                    }
                } else if (item.trim().startsWith(this.quoteCharacter)) {
                    current = item.substring(item.indexOf(this.quoteCharacter) + 1) + ",";
                } else {
                    if (trim) {
                        result.add(item.trim());
                    } else {
                        result.add(item);
                    }
                }
            } else {

                if (item.endsWith(this.quoteCharacter)) {
                    current += item.substring(0, item.length() - 1);
                    result.add(current);
                    current = null;
                } else {
                    current += item + ",";
                }
            }
        }
    }

    String[] getSplit() {
        return result.toArray(new String[result.size()]);
    }
}
