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
package org.kie.kogito;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHelloService.class);

    public String hello(String input) throws InterruptedException {
        //Add artificial delay to avoid knative http completing the callback too fast
        TimeUnit.SECONDS.sleep(1l);
        LOGGER.info("Executing {}.hello(\"{}\")", getClass().getName(), input);
        return "Hello " + input;
    }

    public String bye(String input) {
        LOGGER.info("Executing {}.bye(\"{}\")", getClass().getName(), input);
        return "Bye " + input;
    }

    public String helloJson(String input) {
        LOGGER.info("Executing {}.helloJson(\"{}\")", getClass().getName(), input);
        return "{\"answer\" : \"Hello " + input + "\"}";
    }
}
