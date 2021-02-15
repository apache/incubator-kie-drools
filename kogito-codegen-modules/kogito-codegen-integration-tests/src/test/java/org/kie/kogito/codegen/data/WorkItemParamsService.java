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
package org.kie.kogito.codegen.data;

public class WorkItemParamsService {

    public Boolean negate(Boolean b) {
        System.out.println("Boolean: " + b);
        return !b;
    }

    public Integer incrementI(Integer i) {
        System.out.println("Integer: " + i++);
        return i;
    }

    public Float incrementF(Float f) {
        System.out.println("Float: " + f++);
        return f;
    }

    public String duplicate(String s) {
        System.out.println("String: " + s);
        return s + s;
    }
}
