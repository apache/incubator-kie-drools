/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.factmodel;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class GenericTypeDefinitionTraits {

    private final String rawType;
    private final List<GenericTypeDefinitionTraits> genericTypes;

    private GenericTypeDefinitionTraits(String rawType, List<GenericTypeDefinitionTraits> genericTypes) {
        this.rawType = rawType;
        this.genericTypes = genericTypes;
    }

    public GenericTypeDefinitionTraits(GenericTypeDefinition genericTypeDefinition) {
        this.rawType = genericTypeDefinition.getRawType();
        this.genericTypes = genericTypeDefinition.getGenericTypes().stream()
                .filter(Objects::nonNull)
                .map(GenericTypeDefinitionTraits::new).collect(toList());
    }

    public String getDescriptor() {
        return BuildUtils.getTypeDescriptor(rawType);
    }

    public String getSignature() {
        String descriptor = getDescriptor();
        if (genericTypes == null) {
            return descriptor;
        }
        return descriptor.substring( 0, descriptor.length()-1 ) +
                "<" + genericTypes.stream().map( GenericTypeDefinitionTraits::getSignature ).collect( joining() ) + ">;";
    }

    public GenericTypeDefinitionTraits map( Function<String, String> transformer ) {
        return new GenericTypeDefinitionTraits( transformer.apply(rawType), genericTypes != null ?
                genericTypes.stream().map( t -> t.map( transformer ) ).collect( toList() ) : null);
    }
}
