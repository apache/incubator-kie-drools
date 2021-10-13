/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.jaxb;

import java.time.DateTimeException;
import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal( String localDateString ) throws Exception {
        if ( localDateString == null ) {
            return null;
        }
        try {
            return LocalDate.parse( localDateString );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localDateString + ") to type ("
                    + LocalDate.class.getName() + ")." );
        }
    }

    @Override
    public String marshal( LocalDate localDateObject ) throws Exception {
        if ( localDateObject == null ) {
            return null;
        }
        return localDateObject.toString();
    }

}
