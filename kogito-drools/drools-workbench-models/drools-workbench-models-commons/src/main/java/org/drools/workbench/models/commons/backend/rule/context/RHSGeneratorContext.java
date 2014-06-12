/*
 * Copyright 2013 JBoss Inc
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
package org.drools.workbench.models.commons.backend.rule.context;

/**
 * RHS DRL generation context object
 */
public class RHSGeneratorContext {

    private RHSGeneratorContext parent;
    private int offset;
    private boolean hasOutput;

    RHSGeneratorContext() {
    }

    RHSGeneratorContext( final RHSGeneratorContext parent,
                         final int offset ) {
        this.parent = parent;
        this.offset = offset;
    }

    public RHSGeneratorContext getParent() {
        return parent;
    }

    public boolean isHasOutput() {
        return hasOutput;
    }

    public void setHasOutput( boolean hasOutput ) {
        this.hasOutput = hasOutput;
    }

    public int getOffset() {
        return offset;
    }

}
