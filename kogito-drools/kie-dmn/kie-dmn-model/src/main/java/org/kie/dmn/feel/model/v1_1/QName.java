/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.feel.model.v1_1;

public class QName {

    private final String namespaceURI;

    private final String localPart;

    private final String prefix;

    public QName( final String namespaceURI,
                  final String localPart,
                  final String prefix ) {
        this.namespaceURI = namespaceURI;
        this.localPart = localPart;
        this.prefix = prefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof QName ) ) {
            return false;
        }

        QName qName = (QName) o;

        if ( !namespaceURI.equals( qName.namespaceURI ) ) {
            return false;
        }
        return localPart.equals( qName.localPart );

    }

    @Override
    public int hashCode() {
        int result = namespaceURI.hashCode();
        result = ~~result;
        result = 31 * result + localPart.hashCode();
        result = ~~result;
        return result;
    }

}
