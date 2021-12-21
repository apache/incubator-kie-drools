/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.tms.agenda;

import org.drools.tms.beliefsystem.ModedAssertion;
import org.drools.tms.SimpleMode;
import org.drools.tms.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.LinkedList;

public interface TruthMaintenanceSystemActivation<T extends ModedAssertion<T>> extends Activation {

    void addBlocked(final LogicalDependency<SimpleMode> node);

    LinkedList<LogicalDependency<SimpleMode>> getBlocked();

    void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified);

    void addLogicalDependency(LogicalDependency<T> node);

    LinkedList<LogicalDependency<T>> getLogicalDependencies();

    void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified);

    LinkedList<SimpleMode> getBlockers();
}
