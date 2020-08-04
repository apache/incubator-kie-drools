/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.ruleunit;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.kogito.factory.KogitoInternalFactHandle;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalStoreCallback {
    void update( KogitoInternalFactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation);
    void delete( KogitoInternalFactHandle fh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState);
}