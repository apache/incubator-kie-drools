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
package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * A class for the default dialect configuration.
 */
public class DefaultDialectOption implements SingleValueKieBuilderOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the default DIALECT
     */
    public static final String PROPERTY_NAME = "drools.dialect.default";

    public static OptionKey<DefaultDialectOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * dialect name
     */
    private final String dialectName;

    /**
     * Private constructor to enforce the use of the factory method
     * @param dialectName
     */
    private DefaultDialectOption( String dialectName) {
        this.dialectName = dialectName;
    }

    /**
     * This is a factory method for this DefaultDialect configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param name the name of the dialect to be configured as default
     *
     * @return the actual type safe default dialect configuration.
     */
    public static DefaultDialectOption get( String dialectName ) {
        return new DefaultDialectOption( dialectName );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public String propertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the name of the dialect configured as default
     *
     * @return
     */
    public String dialectName() {
        return dialectName;
    }

    @Override
    public String toString() {
        return "DefaultDialectOption( name=" + dialectName + " )";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dialectName == null) ? 0 : dialectName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        DefaultDialectOption other = (DefaultDialectOption) obj;
        if (dialectName == null ) {
            if (other.dialectName != null ) { return false; }
        } else if ( !dialectName.equals(other.dialectName) ) {
            return false;
        }
        return true;
    }

}
