/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.investmentallocation.domain.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InvestmentAllocationMicrosUtil {

    private static final BigDecimal MICROS_DIVISOR = new BigDecimal(1000000L);
    private static final BigDecimal PICOS_DIVISOR = new BigDecimal(1000000000000L);
    public static final NumberFormat PERCENT_FORMAT = new DecimalFormat("#0.00%");

    public static String formatMicrosAsPercentage(long micros) {
        BigDecimal percentage = new BigDecimal(micros).divide(MICROS_DIVISOR, BigDecimal.ROUND_HALF_UP);
        return PERCENT_FORMAT.format(percentage);
    }

    public static String formatPicosAsPercentage(long picos) {
        BigDecimal percentage = new BigDecimal(picos).divide(PICOS_DIVISOR, BigDecimal.ROUND_HALF_UP);
        return PERCENT_FORMAT.format(percentage);
    }

    private InvestmentAllocationMicrosUtil() {
    }

}
