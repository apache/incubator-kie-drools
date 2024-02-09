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
package org.kie.test.util.db;

import javax.sql.DataSource;

/**
 * Wrapper for an XA data source with pooling capabilities.
 */
public interface PoolingDataSourceWrapper extends DataSource {

    /**
     * Closes the data source; as a result, the data source will stop providing connections and will be unregistered
     * from JNDI context.
     */
    void close();

    /**
     * @return the data source JNDI name
     */
    String getUniqueName();

    /**
     * @return name of underlying XADataSource class
     */
    String getClassName();
}
