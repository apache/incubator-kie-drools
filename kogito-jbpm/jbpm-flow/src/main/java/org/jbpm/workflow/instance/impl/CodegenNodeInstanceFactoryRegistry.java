/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workflow.instance.impl;

import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.instance.node.LambdaSubProcessNodeInstance;

public class CodegenNodeInstanceFactoryRegistry extends NodeInstanceFactoryRegistry {

    @Override
    protected NodeInstanceFactory get(Class<?> clazz) {
        if (SubProcessNode.class == clazz) {
            return factory(LambdaSubProcessNodeInstance::new);
        }
        return super.get(clazz);
    }
}
