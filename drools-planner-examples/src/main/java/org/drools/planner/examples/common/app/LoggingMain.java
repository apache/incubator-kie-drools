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

import java.net.URL;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingMain {

    public static final String DEFAULT_LOGGING_CONFIG = "/org/drools/planner/examples/common/app/logback.xml";

    protected final transient Logger logger;

    public LoggingMain() {
        this(DEFAULT_LOGGING_CONFIG);
    }

    public LoggingMain(String loggingConfig) {
        configureLogback(getClass().getResource(loggingConfig));
        logger = LoggerFactory.getLogger(getClass());
    }

    public static void configureLogback(URL loggingConfigUrl) {
        // TODO workaround for http://jira.qos.ch/browse/LBCLASSIC-311
        if (loggingConfigUrl == null) {
            throw new IllegalArgumentException("The loggingConfig does not exist.");
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default configuration.
            context.reset();
            configurator.doConfigure(loggingConfigUrl);
        } catch (JoranException je) {
            throw new IllegalArgumentException("Logging configuration failed", je);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

}
