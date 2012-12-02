/*
 * Copyright 2010 JBoss Inc
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

package org.drools.definition.process;

import java.util.Map;

/**
 * A connection is a link from one Node to another.
 */
public interface Connection {

	/**
	 * The Node the connection starts from.
	 */
    Node getFrom();

	/**
	 * The Node the connection goes to.
	 */
    Node getTo();

	/**
	 * The type of exit point of the from Node.  Defaults to "DROOLS_DEFAULT".
	 */
    String getFromType();

	/**
	 * The type of entry point of the to Node.  Defaults to "DROOLS_DEFAULT".
	 */
    String getToType();

	/**
	 * Meta data associated with this connection.
	 */
    Map<String, Object> getMetaData();

	/**
	 * Meta data associated with this connection.
	 */
    @Deprecated Object getMetaData(String name);

}
