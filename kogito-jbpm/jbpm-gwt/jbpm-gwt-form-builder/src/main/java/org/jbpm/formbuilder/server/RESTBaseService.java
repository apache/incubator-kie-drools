/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.server;

import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class RESTBaseService {

    private static final Log log = LogFactory.getLog(RESTBaseService.class);
    
    protected Response error(String errormsg, Exception e) {
        String msg = "Error on REST service: " + errormsg;
        if (e == null) {
            log.error(msg);
        } else {
            log.error(msg, e);
        }
        return Response.serverError().build();
    }

}
