/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.common.app;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggingTest {

    public static final String DEFAULT_LOGGING_CONFIG = "/org/drools/planner/examples/common/app/log4j-test.xml";

    @BeforeClass
    public static void configureLogging() {
        DOMConfigurator.configure(LoggingTest.class.getResource(DEFAULT_LOGGING_CONFIG));
    }

}
