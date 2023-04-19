/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.persistence.rocksdb;

import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RocksDBProcessInstancesFactory implements ProcessInstancesFactory, AutoCloseable {

    private final RocksDB db;

    public RocksDBProcessInstancesFactory(Options options, String dbLocation) throws RocksDBException {
        this.db = RocksDB.open(options, dbLocation);
    }

    @Override
    public RocksDBProcessInstances<?> createProcessInstances(Process<?> process) {
        return new RocksDBProcessInstances<>(process, db);
    }

    @Override
    public void close() {
        db.close();
    }
}
