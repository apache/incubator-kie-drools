/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.context.exception;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebApplicationExceptionPolicy extends AbstractHierarchyExceptionPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionPolicy.class);

    @Override
    protected boolean verify(String errorCode, Throwable exception) {
        if (exception instanceof WebApplicationException) {
            int statusCode = ((WebApplicationException) exception).getResponse().getStatus();
            String[] error = errorCode.split(":");
            if (error.length == 1) {
                // test if errorCode contains the http code, eg. "500"
                return checkStatusCode(error[0], statusCode);
            } else if (error.length == 2) {
                // test if errorCode contains the http code, eg. "HTTP:500"
                return checkStatusCode(error[1], statusCode);
            }
        }
        return false;
    }

    private boolean checkStatusCode(String errorCode, int statusCode) {
        try {
            return Integer.parseInt(errorCode) == statusCode;
        } catch (NumberFormatException e) {
            LOGGER.debug("Unable to parse numeric error code from {}", errorCode);
        }
        return false;
    }
}
