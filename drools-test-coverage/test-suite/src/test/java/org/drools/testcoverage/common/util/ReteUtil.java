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

package org.drools.testcoverage.common.util;

import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.ObjectTypeNode;
import org.kie.api.KieBase;

public class ReteUtil {

    public static ObjectTypeNode getObjectTypeNode(KieBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl) kbase).getRete().getObjectTypeNodes();
        for (ObjectTypeNode n : nodes) {
            if (((ClassObjectType) n.getObjectType()).getClassType() == nodeClass) {
                return n;
            }
        }
        return null;
    }

    private ReteUtil() {

    }
}
