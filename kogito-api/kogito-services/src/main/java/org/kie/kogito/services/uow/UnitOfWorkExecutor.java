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
package org.kie.kogito.services.uow;

import java.util.function.Supplier;

import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;

public class UnitOfWorkExecutor {

    private UnitOfWorkExecutor() {

    }

    public static <T> T executeInUnitOfWork(UnitOfWorkManager uowManager, Supplier<T> supplier) {
        T result = null;
        UnitOfWork uow = uowManager.newUnitOfWork();

        try {
            uow.start();

            result = supplier.get();
            uow.end();

            return result;
        } catch (ProcessInstanceExecutionException e) {
            uow.end();
            throw e;
        } catch (Exception e) {
            uow.abort();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }

    }
}
