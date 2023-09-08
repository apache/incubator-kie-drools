/**
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
package org.drools.commands;

import java.util.Arrays;
import java.util.List;

import org.drools.commands.fluent.Batch;
import org.drools.commands.fluent.BatchImpl;
import org.drools.commands.fluent.InternalExecutable;
import org.drools.commands.impl.NotTransactionalCommand;
import org.junit.Test;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalExecutableTest {

    @Test
    public void notTransactionalCommandTest() {
        final Batch batch = new BatchImpl();
        batch.addCommand(new TransactionalCommandTest());
        batch.addCommand(new NotTransactionalCommandTest());
        final InternalExecutable internalExecutableImplTest = new InternalExecutableImplTest(batch);
        assertThat(internalExecutableImplTest.canRunInTransaction()).isFalse();
    }

    @Test
    public void transactionalCommandTest() {
        final Batch batch = new BatchImpl();
        batch.addCommand(new TransactionalCommandTest());
        batch.addCommand(new TransactionalCommandTest());
        final InternalExecutable internalExecutableImplTest = new InternalExecutableImplTest(batch);
        assertThat(internalExecutableImplTest.canRunInTransaction()).isTrue();
    }

    @Test
    public void emptyCommandTest() {
        final Batch batch = new BatchImpl();
        final InternalExecutable internalExecutableImplTest = new InternalExecutableImplTest(batch);
        assertThat(internalExecutableImplTest.canRunInTransaction()).isTrue();
    }

    class TransactionalCommandTest implements ExecutableCommand<Void> {

        @Override
        public Void execute(Context context) {
            return null;
        }
    }

    class NotTransactionalCommandTest implements NotTransactionalCommand<Void> {

        @Override
        public Void execute(Context context) {
            return null;
        }
    }

    class InternalExecutableImplTest implements InternalExecutable {

        final private List<Batch> batches;

        @Override
        public List<Batch> getBatches() {
            return batches;
        }

        InternalExecutableImplTest(Batch... batches) {
            this.batches = Arrays.asList(batches);
        }
    }
}
