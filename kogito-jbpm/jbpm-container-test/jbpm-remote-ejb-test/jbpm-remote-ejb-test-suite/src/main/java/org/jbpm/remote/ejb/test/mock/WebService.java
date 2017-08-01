/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.remote.ejb.test.mock;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.Endpoint;

public class WebService {

    public static final String WS_NAME = "Echo";
    public static final String WS_SERVICE_NAME = "EchoService";
    public static final String WS_NAMESPACE = "http://sepro.jboss.org";

    public static final String URL = "http://localhost:8666/" + WS_SERVICE_NAME;

    @javax.jws.WebService(name = WS_NAME, serviceName = WS_SERVICE_NAME, targetNamespace = WS_NAMESPACE)
    public static class EchoService {

        @WebMethod
        public String echo(@WebParam(name = "message") String message) {
            return message;
        }

    }

    private static Endpoint endpoint;

    private WebService() {
    }

    public static void start() {
        endpoint = Endpoint.publish(URL, new EchoService());
    }

    public static void stop() {
        if (endpoint != null) {
            endpoint.stop();
            endpoint = null;
        }
    }

}
