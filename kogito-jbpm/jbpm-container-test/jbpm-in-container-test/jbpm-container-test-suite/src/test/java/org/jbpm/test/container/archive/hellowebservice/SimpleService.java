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

package org.jbpm.test.container.archive.hellowebservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "SimpleService")
public class SimpleService {

    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String name) {
        /*
         * Wait for ASYNC calls.
         */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("[WARN] InterruptedException: " + e.getMessage());
        }
        System.out.println("[INFO] Hello " + name);
        return "Hello " + name;
    }

}
