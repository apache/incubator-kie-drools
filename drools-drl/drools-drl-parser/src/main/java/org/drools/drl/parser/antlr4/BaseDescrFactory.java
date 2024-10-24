/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;
import org.drools.drl.ast.descr.BaseDescr;
import org.kie.api.io.Resource;

import static org.drools.drl.parser.antlr4.DescrHelper.populateCommonProperties;

/**
 * Factory class for Descr instantiation.
 */
public class BaseDescrFactory {

    private BaseDescrFactory() {
        // Private constructor to prevent instantiation.
    }

    public static <T extends BaseDescr> Builder<T> builder(T toBuild) {
        return new Builder<>(toBuild);
    }

    public static class Builder<T extends BaseDescr> {

        T toReturn;

        private Builder(T toBuild) {
            this.toReturn = toBuild;
        }

        /**
         * Initializes a BaseDescr instance with common properties from the given context.
         * DRLVisitor implementations should always use this method to initialize BaseDescr instances.
         */
        public Builder<T> withParserRuleContext(ParserRuleContext ctx) {
            populateCommonProperties(toReturn, ctx);
            return this;
        }

        public Builder<T> withResource(Resource resource) {
            toReturn.setResource(resource);
            return this;
        }

        public T build() {
            return toReturn;
        }
    }
}
