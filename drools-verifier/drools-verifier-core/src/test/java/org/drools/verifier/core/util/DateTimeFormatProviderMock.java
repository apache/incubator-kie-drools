/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.verifier.core.configuration.DateTimeFormatProvider;

public class DateTimeFormatProviderMock
        implements DateTimeFormatProvider {

    private final static String DATE_FORMAT = "dd-MMM-yyyy";

    @Override
    public String format(final Date dateValue) {
        return new SimpleDateFormat(DATE_FORMAT).format(dateValue);
    }

    @Override
    public Date parse(String dateValue) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(dateValue);
        } catch (ParseException e) {
            return null;
        }
    }
}
