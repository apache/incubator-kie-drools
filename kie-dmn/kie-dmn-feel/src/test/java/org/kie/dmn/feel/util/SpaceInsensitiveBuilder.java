/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpaceInsensitiveBuilder {

        public List<String> parts = new ArrayList<>();

        public SpaceInsensitiveBuilder(String start) {
            parts.add(start);
        }

        public SpaceInsensitiveBuilder append(String part) {
            parts.add(part);
            return this;
        }

        public SpacesAndStringParts build() {
            return new SpacesAndStringParts(parts);
        }

    public static class SpacesAndStringParts {

        @SuppressWarnings("rawtypes")
        final List cc;
        private static final Random RANDOM = new Random(47);

        @SuppressWarnings({"rawtypes", "unchecked"})
        public SpacesAndStringParts(List<String> parts) {
            List result = new ArrayList<>();
            for (int i = 0; i < RANDOM.nextInt(4); i++) {
                result.add(SPACES.values()[RANDOM.nextInt(SPACES.values().length)]);
            }
            for (String p : parts) {
                result.add(p);
                for (int i = 0; i < RANDOM.nextInt(4); i++) {
                    result.add(SPACES.values()[RANDOM.nextInt(SPACES.values().length)]);
                }
            }
            cc = Collections.unmodifiableList(result);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Object c : cc) {
                if (c instanceof SPACES) {
                    SPACES spaces = (SPACES) c;
                    builder.append(spaces.getSpaceStringValue());
                } else {
                    builder.append(c.toString());
                }
            }
            return builder.toString();
        }

        public String asLogical() {
            @SuppressWarnings("unchecked")
            String result = (String) cc.stream().map(c -> {
                if (c instanceof SPACES) {
                    SPACES spaces = (SPACES) c;
                    return spaces.name();
                } else {
                    return "\"" + c.toString() + "\"";
                }
            }).collect(Collectors.joining(" "));
            return "[" + result + "]";
        }

    }

    public static enum SPACES {
        SPACE(' '),
        u00A0('\u00A0'),
        u1680('\u1680');

        private char spaceStringValue;

        SPACES(char spaceStringValue) {
            this.spaceStringValue = spaceStringValue;
        }

        public char getSpaceStringValue() {
            return spaceStringValue;
        }

    }

    }