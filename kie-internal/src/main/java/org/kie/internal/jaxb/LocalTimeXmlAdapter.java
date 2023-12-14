/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.jaxb;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class LocalTimeXmlAdapter extends XmlAdapter<String, LocalTime> {

    private final DateTimeFormatter formatter;

    public LocalTimeXmlAdapter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern( "HH:mm:ss" )
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true )
                .toFormatter();
    }

    @Override
    public LocalTime unmarshal( String localTimeString ) throws Exception {
        if ( localTimeString == null ) {
            return null;
        }
        try {
            return LocalTime.from( formatter.parse( localTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localTimeString + ") to type ("
                    + LocalTime.class.getName() + ")." );
        }
    }

    @Override
    public String marshal( LocalTime localTimeObject ) throws Exception {
        if ( localTimeObject == null ) {
            return null;
        }
        return formatter.format( localTimeObject );
    }

}
