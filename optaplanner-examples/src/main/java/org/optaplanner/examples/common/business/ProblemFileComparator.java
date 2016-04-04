/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.business;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class ProblemFileComparator implements Comparator<File>, Serializable {

    private static final AlphaNumericStringComparator ALPHA_NUMERIC_STRING_COMPARATOR = new AlphaNumericStringComparator();

    @Override
    public int compare(File a, File b) {
        String aLowerCaseName = a.getName().toLowerCase(Locale.US);
        String bLowerCaseName = b.getName().toLowerCase(Locale.US);
        return new CompareToBuilder()
                .append(a.getParent(), b.getParent(), ALPHA_NUMERIC_STRING_COMPARATOR)
                .append(a.isDirectory(), b.isDirectory())
                .append(!aLowerCaseName.startsWith("demo"), !bLowerCaseName.startsWith("demo"))
                .append(aLowerCaseName, bLowerCaseName, ALPHA_NUMERIC_STRING_COMPARATOR)
                .append(a.getName(), b.getName())
                .toComparison();
    }

}
