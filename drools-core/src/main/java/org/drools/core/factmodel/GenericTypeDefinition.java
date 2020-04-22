/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.factmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class GenericTypeDefinition implements Serializable {
    private final String rawType;
    private final List<GenericTypeDefinition> genericTypes;

    public GenericTypeDefinition( String rawType ) {
        this(rawType, null);
    }

    private GenericTypeDefinition( String rawType, List<GenericTypeDefinition> genericTypes ) {
        this.rawType = rawType;
        this.genericTypes = genericTypes;
    }

    public static GenericTypeDefinition parseType( String type, Function<String, String> resolver ) {
        int genericsStart = type.indexOf( '<' );
        if (genericsStart < 0) {
            String resolvedType = resolver.apply( type );
            return resolvedType != null ? new GenericTypeDefinition(resolvedType, null) : null;
        }

        List<GenericTypeDefinition> genericTypes = new ArrayList<>();
        String rawType = type.substring( 0, genericsStart ).trim();
        String resolvedRawType = resolver.apply( rawType );
        if (resolvedRawType == null) {
            return null;
        }

        String generics = type.substring( genericsStart+1, type.length()-1 );
        for (String gen : generics.split( "\\," )) {
            GenericTypeDefinition genType = parseType( gen, resolver );
            if (genType == null) {
                return null;
            }
            genericTypes.add(genType);
        }

        return new GenericTypeDefinition(resolvedRawType, genericTypes);
    }

    public String getRawType() {
        return rawType;
    }

    public String getDescriptor() {
        return BuildUtils.getTypeDescriptor( rawType );
    }

    public boolean hasGenerics() {
        return genericTypes != null;
    }

    public String getSignature() {
        String descriptor = getDescriptor();
        if (genericTypes == null) {
            return descriptor;
        }
        return descriptor.substring( 0, descriptor.length()-1 ) +
                "<" + genericTypes.stream().map( GenericTypeDefinition::getSignature ).collect( joining() ) + ">;";
    }

    public GenericTypeDefinition map( Function<String, String> transformer ) {
        return new GenericTypeDefinition( transformer.apply(rawType), genericTypes != null ?
                genericTypes.stream().map( t -> t.map( transformer ) ).collect( toList() ) : null);
    }

    @Override
    public String toString() {
        return rawType + (genericTypes == null ? "" :
                "<" + genericTypes.stream().map( GenericTypeDefinition::toString ).collect( joining(", ") ) + ">" );
    }
}
