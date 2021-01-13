
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

package java.text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface AttributedCharacterIterator {

    public static class Attribute implements Serializable {

        private String name;

        private static final Map<String, Attribute> instanceMap = new HashMap<>(7);

        protected Attribute(String name) {
        }

        public final boolean equals(Object obj) {
            return true;
        }

        public final int hashCode() {
            return 0;
        }

        public String toString() {
            return getClass().getName() + "(" + name + ")";
        }

        protected String getName() {
            return name;
        }

        protected Object readResolve() {
            return null;
        }

        public static final Attribute LANGUAGE = new Attribute("language");

        public static final Attribute READING = new Attribute("reading");

        public static final Attribute INPUT_METHOD_SEGMENT = new Attribute("input_method_segment");

        private static final long serialVersionUID = -9142742483513960612L;
    }

    ;

    public int getRunStart();

    public int getRunStart(Attribute attribute);

    public int getRunStart(Set<? extends Attribute> attributes);

    public int getRunLimit();

    public int getRunLimit(Attribute attribute);

    public int getRunLimit(Set<? extends Attribute> attributes);

    public Map<Attribute, Object> getAttributes();

    public Object getAttribute(Attribute attribute);

    public Set<Attribute> getAllAttributeKeys();
}
