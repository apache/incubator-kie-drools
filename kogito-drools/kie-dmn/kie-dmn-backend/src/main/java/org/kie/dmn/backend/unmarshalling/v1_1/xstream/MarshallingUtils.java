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

package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.QName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarshallingUtils {
    private final static Pattern QNAME_PAT = Pattern.compile( "((\\{([^\\}]*)\\})?([^:]*):)?(.*)" );

    public static QName parseQNameString(String qns) {
        if ( qns != null ) {
            Matcher m = QNAME_PAT.matcher( qns );
            if ( m.matches() ) {
                return new QName( m.group( 3 ), m.group( 5 ), m.group( 4 ) );
            } else {
                return new QName( null, qns, null );
            }
        } else {
            return null;
        }
    }
    
    public static String formatQName(QName qname) {
        StringBuilder sb = new StringBuilder();
        if ( qname.getPrefix() != null ) {
            sb.append(qname.getPrefix());
            sb.append(":");
        }
        if ( qname.getNamespaceURI() != null ) {
            sb.append(qname.getNamespaceURI());
            sb.append(":");
        }
        sb.append(qname.getLocalPart());
        return sb.toString();
    }
}
