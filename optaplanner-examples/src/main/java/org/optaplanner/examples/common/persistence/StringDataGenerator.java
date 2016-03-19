/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringDataGenerator {

    private List<String[]> partValuesList = new ArrayList<>();
    private int partValuesLength;
    private int index = 0;
    private int indexLimit;

    public StringDataGenerator() {
    }

    public StringDataGenerator addPart(String... partValues) {
        if (partValuesList.isEmpty()) {
            partValuesLength = partValues.length;
        } else {
            if (partValues.length != partValuesLength) {
                throw new IllegalStateException("The partValues length (" + partValues.length
                        + ") is not the same as the partValuesLength (" + partValuesLength + ") of the others.");
            }
        }
        partValuesList.add(partValues);
        indexLimit = (int) Math.pow(partValuesLength, partValuesList.size());
        return this;
    }

    /**
     * Do  not scroll per digit (0000, 1111, 2222, 0001, 1112, 2220, 0002, 1110, 2221, ...)
     * Instead, scroll per half (0000, 1111, 2222, 0011, 1122, 2200, 0022, 1100, 2211, ...)
     */
    private int[][] halfSequenceMap = new int[][]{{}, {0}, {0, 1}, {0, 2, 1}, {0, 2, 1, 3}};

    public String generateNextValue() {
        if (index >= indexLimit) {
            throw new IllegalStateException("No more elements: the index (" + index + ") is too high.");
        }
        int listSize = partValuesList.size();
        StringBuilder result = new StringBuilder(listSize * 80);
        // Make sure we have a unique combination
        if (listSize >= halfSequenceMap.length) {
            throw new IllegalStateException("A listSize (" + listSize + ") is not yet supported.");
        }
        int[] halfSequence = halfSequenceMap[listSize];
        int[] chosens = new int[listSize];
        int previousChosen = 0;
        for (int i = 0; i < listSize; i++) {
            int chosen = (previousChosen
                    + (index % (int) Math.pow(partValuesLength, halfSequence[i] + 1)
                            / (int) Math.pow(partValuesLength, halfSequence[i])))
                    % partValuesLength;
            chosens[i] = chosen;
            previousChosen = chosen;
        }
        for (int i = 0; i < listSize; i++) {
            if (i > 0) {
                result.append(" ");
            }
            String[] partValues = partValuesList.get(i);
            result.append(partValues[chosens[i]]);
        }
        index++;
        return result.toString();
    }

    public void reset() {
        index = 0;
    }

}
