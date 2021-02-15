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
package org.kie.kogito.codegen.sample.core;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SampleModelImpl implements SampleModel {

    private final String name;
    private final String content;
    private final int numberOfCopy;

    public SampleModelImpl(String name, String content, int numberOfCopy) {
        this.name = name;
        this.content = content;
        this.numberOfCopy = numberOfCopy;
    }

    @Override
    public String execute() {
        return IntStream.range(0, numberOfCopy)
                .mapToObj(i -> content)
                .collect(Collectors.joining("-"));
    }

    @Override
    public String name() {
        return name;
    }

}