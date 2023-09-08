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
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;

public class ObjectFields
        extends FieldsBase<ObjectField> {

    public Where<FieldSelector, FieldListen> where(final Matcher matcher) {
        return new Where<FieldSelector, FieldListen>() {
            @Override
            public FieldSelector select() {
                return new FieldSelector(matcher);
            }

            @Override
            public FieldListen listen() {
                return new FieldListen(matcher);
            }
        };
    }

    public class FieldSelector
            extends Select<ObjectField> {

        public FieldSelector(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }

    public class FieldListen
            extends Listen<ObjectField> {

        public FieldListen(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
