/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.fluent.impl;

import java.util.List;

import org.drools.core.command.impl.TransactionalCommand;
import org.kie.api.runtime.Executable;

public interface InternalExecutable extends Executable {

    List<Batch> getBatches();

    default boolean canRunInTransaction() {
        return getBatches().stream()
                .flatMap(batch -> batch.getCommands().stream())
                .filter(TransactionalCommand.class::isInstance)
                .map(TransactionalCommand.class::cast)
                .allMatch(TransactionalCommand::canRunInTransaction);
    }
}
