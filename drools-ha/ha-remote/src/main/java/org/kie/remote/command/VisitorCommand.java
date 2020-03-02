/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.remote.command;

public interface VisitorCommand {

    void visit(FireAllRulesCommand command);

    void visit(FireUntilHaltCommand command);

    void visit(HaltCommand command);

    void visit(InsertCommand command);

    void visit(EventInsertCommand command);

    void visit(DeleteCommand command);

    void visit(UpdateCommand command);

    void visit(ListObjectsCommand command);

    void visit(ListObjectsCommandClassType command);

    void visit(ListObjectsCommandNamedQuery command);

    void visit(FactCountCommand command);

    void visit(SnapshotOnDemandCommand command);

    void visit(GetObjectCommand command);

    void visit(UpdateKJarCommand command);

    void visit(GetKJarGAVCommand command);
}