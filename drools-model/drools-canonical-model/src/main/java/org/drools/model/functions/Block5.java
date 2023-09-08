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
package org.drools.model.functions;

import java.io.Serializable;

public interface Block5<T1, T2, T3, T4, T5> extends Serializable {

    void execute(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) throws Exception;

    default BlockN asBlockN() {
        return new Impl(this);
    }

    class Impl extends IntrospectableLambda implements BlockN {

        private final Block5 block;

        public Impl(Block5 block) {
            this.block = block;
        }

        @Override
        public void execute(Object... objs) throws Exception {
            block.execute(objs[0], objs[1], objs[2], objs[3], objs[4]);
        }

        @Override
        public Object getLambda() {
            return block;
        }
    }
}
