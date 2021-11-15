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
package java.lang.reflect;

public class Modifier {

    public static final int PUBLIC = 0x00000001;

    /**
     * Return true if the integer argument includes the public modifier, false otherwise.
     *
     * @param mod – a set of modifiers
     * @return true if mod includes the public modifier; false otherwise.
     */
    public static boolean isPublic(int mod) {
        return (mod & PUBLIC) != 0;
    }
}