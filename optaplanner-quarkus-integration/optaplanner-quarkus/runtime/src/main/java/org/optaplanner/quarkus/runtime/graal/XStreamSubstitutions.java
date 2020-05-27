/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.runtime.graal;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "com.thoughtworks.xstream.converters.reflection.SerializableConverter")
final class Target_SerializableConverter {

    @Substitute
    public Object doUnmarshal(final Object result, final Target_HierarchicalStreamReader reader,
            final Target_UnmarshallingContext context) {
        return null;
    }

    @Substitute
    public void doMarshal(final Object source, final Target_HierarchicalStreamWriter writer,
            final Target_MarshallingContext context) {
    }
}

@TargetClass(className = "com.thoughtworks.xstream.io.HierarchicalStreamReader")
final class Target_HierarchicalStreamReader {

}

@TargetClass(className = "com.thoughtworks.xstream.converters.UnmarshallingContext")
final class Target_UnmarshallingContext {

}

@TargetClass(className = "com.thoughtworks.xstream.io.HierarchicalStreamWriter")
final class Target_HierarchicalStreamWriter {

}

@TargetClass(className = "com.thoughtworks.xstream.converters.MarshallingContext")
final class Target_MarshallingContext {

}

@TargetClass(className = "com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider")
final class Target_PureJavaReflectionProvider {

    @Substitute
    private Object instantiateUsingSerialization(final Class type) {
        return null;
    }
}

class XStreamSubstitutions {

}
