/*
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

package org.optaplanner.benchmark.impl.report;

import java.time.Duration;
import java.util.Locale;

import freemarker.core.Environment;
import freemarker.core.TemplateFormatUtil;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateNumberFormatFactory;
import freemarker.core.TemplateValueFormatException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

final class MillisecondDurationNumberFormatFactory extends TemplateNumberFormatFactory {

    static final MillisecondDurationNumberFormatFactory INSTANCE = new MillisecondDurationNumberFormatFactory();

    @Override
    public TemplateNumberFormat get(String params, Locale locale, Environment environment) throws TemplateValueFormatException {
        TemplateFormatUtil.checkHasNoParameters(params);
        return MillisecondDurationNumberFormat.INSTANCE;
    }

    static final class MillisecondDurationNumberFormat extends TemplateNumberFormat {

        static final MillisecondDurationNumberFormat INSTANCE = new MillisecondDurationNumberFormat();

        @Override
        public String formatToPlainText(TemplateNumberModel templateNumberModel) throws TemplateModelException {
            Number n = templateNumberModel.getAsNumber();
            if (n == null) {
                return "None.";
            }
            long millis = n.longValue();
            if (millis == 0L) {
                return "0 ms.";
            }
            return processNonZeroMillis(millis);
        }

        private static String processNonZeroMillis(long millis) {
            Duration duration = Duration.ofMillis(millis);
            long daysPart = duration.toDaysPart();
            long hoursPart = duration.toHoursPart();
            long minutesPart = duration.toMinutesPart();
            double seconds = duration.toSecondsPart() + (duration.toMillisPart() / 1000.0d);
            if (daysPart > 0) {
                return String.format("%02d:%02d:%02d:%06.3f s. (%,d ms.)",
                        daysPart,
                        hoursPart,
                        minutesPart,
                        seconds,
                        millis);
            } else if (hoursPart > 0) {
                return String.format("%02d:%02d:%06.3f s. (%,d ms.)",
                        hoursPart,
                        minutesPart,
                        seconds,
                        millis);
            } else if (minutesPart > 0) {
                return String.format("%02d:%06.3f s. (%,d ms.)",
                        minutesPart,
                        seconds,
                        millis);
            } else {
                return String.format("%.3f s. (%,d ms.)",
                        seconds,
                        millis);
            }
        }

        @Override
        public boolean isLocaleBound() {
            return true;
        }

        @Override
        public String getDescription() {
            return "Millisecond Duration";
        }
    }

}
