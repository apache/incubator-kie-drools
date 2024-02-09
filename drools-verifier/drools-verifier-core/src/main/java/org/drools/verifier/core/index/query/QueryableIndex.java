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
package org.drools.verifier.core.index.query;

import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.model.Columns;
import org.drools.verifier.core.index.model.ObjectTypes;
import org.drools.verifier.core.index.model.Rules;
import org.drools.verifier.core.index.select.QueryCallback;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.KeyTreeMap;

public class QueryableIndex {

    private Rules rules;
    private Columns columns;
    private ObjectTypes objectTypes;

    public QueryableIndex(final Rules rules,
                          final Columns columns,
                          final ObjectTypes objectTypes) {
        this.rules = rules;
        this.columns = columns;
        this.objectTypes = objectTypes;
    }

    private void queryAll(Query query,
                          QueryCallback callback) {

        callback.callback(new Select<>(getMap(query.getMapId()).get(query.getMatcher()
                                                                            .getKeyDefinition()),
                                       query.getMatcher()).all());
    }

    private KeyTreeMap getMap(final String mapId) {
        if ("Rules".equals(mapId)) {
            return rules.map;
        } else if ("Columns".equals(mapId)) {
            return columns.map;
        } else if ("ObjectTypes".equals(mapId)) {
            return objectTypes.map;
        } else {
            throw new IllegalArgumentException("Could not find map with the id: " + mapId);
        }
    }

    private void queryFirst(Query query,
                            QueryCallback callback) {

        callback.callback(new Select<>(getMap(query.getMapId()).get(query.getMatcher()
                                                                            .getKeyDefinition()),
                                       query.getMatcher()).first());
    }

    private void queryLast(Query query,
                           QueryCallback callback) {

        callback.callback(new Select<>(getMap(query.getMapId()).get(query.getMatcher()
                                                                            .getKeyDefinition()),
                                       query.getMatcher()).last());
    }

    public Where getRules() {
        return new Where("Rules");
    }

    public Where getColumns() {
        return new Where("Columns");
    }

    public Where getObjectTypes() {
        return new Where("ObjectTypes");
    }

    class Where {

        private Matcher matcher;
        private String mapId;

        public Where(final String mapId) {
            this.mapId = mapId;
        }

        public Select where(final Matcher matcher) {
            this.matcher = matcher;
            return new Select();
        }

        class Select {

            public Callbacks select() {
                return new Callbacks();
            }

            class Callbacks {

                public void all(final QueryCallback callback) {
                    queryAll(new Query(mapId,
                                       matcher),
                             callback);
                }

                public void first(final QueryCallback callback) {
                    queryFirst(new Query(mapId,
                                         matcher),
                               callback);
                }

                public void last(final QueryCallback callback) {
                    queryLast(new Query(mapId,
                                        matcher),
                              callback);
                }
            }
        }
    }
}
