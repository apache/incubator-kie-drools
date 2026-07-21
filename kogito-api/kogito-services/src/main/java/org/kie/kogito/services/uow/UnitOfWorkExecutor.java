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

import org.kie.kogito.uow.UnitOfWorkManager;

public abstract class UnitOfWorkExecutor {

    private static volatile UnitOfWorkExecutor unitOfWorkExecutor;

    public static void set(UnitOfWorkExecutor executor) {
        unitOfWorkExecutor = executor;
    }

    private static UnitOfWorkExecutor getExecutor() {
        if (unitOfWorkExecutor == null) {
            synchronized (UnitOfWorkExecutor.class) {
                if (unitOfWorkExecutor == null) {
                    unitOfWorkExecutor = new DefaultUnitOfWorkExecutor();
                }
            }
        }
        return unitOfWorkExecutor;
    }

    public static <T> T executeInUnitOfWork(UnitOfWorkManager uowManager, Supplier<T> supplier) {
        return getExecutor().execute(uowManager, supplier);
    }

    abstract <T> T execute(UnitOfWorkManager uowManager, Supplier<T> supplier);
}
