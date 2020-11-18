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

package org.optaplanner.examples.investment.domain.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InvestmentNumericUtil {

    public static final long MAXIMUM_QUANTITY_MILLIS = 1000L;

    public static final String MILLIS_NUMBER_PATTERN = "#0.000";
    public static final String MILLIS_PERCENT_PATTERN = "#0.0%";
    // TODO Remove potential multithreaded use of a thread-unsafe class
    protected static final NumberFormat MILLIS_NUMBER_FORMAT = new DecimalFormat(MILLIS_NUMBER_PATTERN);
    protected static final NumberFormat MILLIS_PERCENT_FORMAT = new DecimalFormat(MILLIS_PERCENT_PATTERN);

    protected static final BigDecimal MILLIS_DIVISOR = new BigDecimal(1000L);
    protected static final BigDecimal MICROS_DIVISOR = new BigDecimal(1000000L);

    public static String formatMillisAsNumber(long millis) {
        BigDecimal value = new BigDecimal(millis).divide(MILLIS_DIVISOR, 3, RoundingMode.HALF_UP);
        return MILLIS_NUMBER_FORMAT.format(value);
    }

    public static String formatMillisAsPercentage(long millis) {
        BigDecimal value = new BigDecimal(millis).divide(MILLIS_DIVISOR, 3, RoundingMode.HALF_UP);
        return MILLIS_PERCENT_FORMAT.format(value);
    }

    public static String formatMicrosAsPercentage(long micros) {
        BigDecimal value = new BigDecimal(micros).divide(MICROS_DIVISOR, 6, RoundingMode.HALF_UP);
        return MILLIS_PERCENT_FORMAT.format(value);
    }

    private InvestmentNumericUtil() {
    }

}
