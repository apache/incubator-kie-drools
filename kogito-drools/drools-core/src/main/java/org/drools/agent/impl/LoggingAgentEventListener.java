/*
 * Copyright 2012 JBoss Inc
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

package org.drools.agent.impl;

import org.drools.agent.AgentEventListener;
import org.drools.agent.RuleAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LoggingAgentEventListener implements AgentEventListener {

    protected static transient Logger logger = LoggerFactory.getLogger(LoggingAgentEventListener.class);

    private String name;

    public String time() {
        Date d = new Date();
        return d.toString();
    }

    public void exception(String message, Throwable e) {
        logger.error("RuleAgent(" + name + ") EXCEPTION (" + time() + "): " + e.getMessage() + ". Stack trace should follow.", e);
    }

    public void exception(Throwable e) {
        logger.error("RuleAgent(" + name + ") EXCEPTION (" + time() + "): " + e.getMessage() + ". Stack trace should follow.", e);
    }

    public void info(String message) {
        logger.info("RuleAgent(" + name + ") INFO (" + time() + "): " + message);
    }

    public void warning(String message) {
        logger.warn("RuleAgent(" + name + ") WARNING (" + time() + "): " + message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void setAgentName(String name) {
        this.name = name;
    }

    public void debug(String message,
                      Object object) {
        logger.debug(message);
    }

    public void info(String message,
                     Object object) {
        logger.info(message);
    }

    public void warning(String message,
                        Object object) {
        logger.warn(message);
    }

}
