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
// need to keep triple slash directive here, Cypress does not handle well imports.
/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable {
    /**
     * Search elements by data-ouia component attributes. If type is not specified it just filters on the previous subject (does not go deeper into children).
     * Behaves as get/find depending on if is chained or not.
     * @param id string
     * @param type optional string
     * @param opts optional - config object
     */
    ouiaId(
      id: string,
      type?: string,
      opts?: Record<string, any>
    ): Chainable<Element>;

    /**
     * Search elements by data-ouia component-type attribute. Behaves as get/find depending on if is chained or not.
     * @param type string
     * @param opts optional - config object
     */
    ouiaType(type: string, opts?: Record<string, any>): Chainable<Element>;

    /**
     * Filter element based on data-ouia-safe attribute
     * @param opts optional - config object
     */
    ouiaSafe(opts?: Record<string, any>): Chainable<Element>;

    /**
     *
     * @param value string
     * @param opts optional - config object
     */
    ouiaNavigationName(
      value?: string,
      opts?: Record<string, any>
    ): Chainable<Element>;
  }
}
