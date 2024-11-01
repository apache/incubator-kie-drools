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
package org.kie.kogito.resource.exceptions;

import java.util.function.Function;

import static org.kie.kogito.resource.exceptions.ExceptionBodyMessageFunctions.defaultMessageException;

public class RestExceptionHandler<EXCEPTION extends Throwable, RESPONSE> {
    private final Function<EXCEPTION, ExceptionBodyMessage> messageConverter;

    private final Function<ExceptionBodyMessage, RESPONSE> responseConverter;

    private Class<EXCEPTION> type;

    public RestExceptionHandler(Class<EXCEPTION> type, Function<EXCEPTION, ExceptionBodyMessage> messageConverter, Function<ExceptionBodyMessage, RESPONSE> responseConverter) {
        this.type = type;
        this.messageConverter = messageConverter;
        this.responseConverter = responseConverter;
    }

    public Class<EXCEPTION> getType() {
        return type;
    }

    public ExceptionBodyMessage getContent(Throwable exception) {
        return messageConverter.apply(getType().cast(exception));
    }

    public RESPONSE buildResponse(ExceptionBodyMessage exceptionBodyMessage) {
        return responseConverter.apply(exceptionBodyMessage);
    }

    public static <TYPE extends Exception, RES> RestExceptionHandler<TYPE, RES> newExceptionHandler(Class<TYPE> type, Function<TYPE, ExceptionBodyMessage> contentGenerator,
            Function<ExceptionBodyMessage, RES> responseGenerator) {
        return new RestExceptionHandler<TYPE, RES>(type, contentGenerator, responseGenerator);
    }

    public static <F extends Exception, R> RestExceptionHandler<F, R> newExceptionHandler(Class<F> type, Function<ExceptionBodyMessage, R> responseGenerator) {
        return new RestExceptionHandler<F, R>(type, defaultMessageException(), responseGenerator);
    }
}