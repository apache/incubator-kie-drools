/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.asm;

import java.io.Serializable;

import org.drools.core.factmodel.BeanClassBuilder;
import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.EnumClassBuilder;
import org.drools.core.rule.TypeDeclaration;

public class DefaultClassBuilderFactory implements Serializable, ClassBuilderFactory {

    // Generic beans
    private BeanClassBuilder beanClassBuilder = new DefaultBeanClassBuilder(true);

    @Override
    public ClassBuilder getBeanClassBuilder() {
        return beanClassBuilder;
    }

    private EnumClassBuilder enumClassBuilder = new DefaultEnumClassBuilder();

    @Override
    public EnumClassBuilder getEnumClassBuilder() {
        return enumClassBuilder;
    }

    @Override
    public ClassBuilder getPropertyWrapperBuilder() {
        return null;
    }

    @Override
    public void setPropertyWrapperBuilder(ClassBuilder pcb) {

    }

    @Override
    public ClassBuilder getClassBuilder(TypeDeclaration type) {
        switch (type.getKind()) {
            case ENUM: return getEnumClassBuilder();
            case CLASS: default: return getBeanClassBuilder();
        }
    }
}
