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

package org.jbpm.process.core.timer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.process.core.constants.CalendarConstants.BUSINESS_CALENDAR_PATH;

public class CalendarBeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(CalendarBeanFactory.class);

    public static CalendarBean createCalendarBean() {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(BUSINESS_CALENDAR_PATH);
        if (Objects.nonNull(resource)) {
            logger.debug("URL resource: {}", resource);
            Properties calendarConfiguration = new Properties();
            try (InputStream is = resource.openStream()) {
                calendarConfiguration.load(is);
                return new CalendarBean(calendarConfiguration);
            } catch (IOException e) {
                String errorMessage = "Error while loading properties for business calendar";
                logger.error(errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            } catch (IllegalArgumentException e) {
                String errorMessage = "Error while populating properties for business calendar";
                logger.error(errorMessage, e);
                throw e;
            }
        } else {
            String errorMessage = String.format("Missing %s", BUSINESS_CALENDAR_PATH);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
