/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
declare namespace Cypress {
  interface Chainable {
    /**
     * Search elements by data-ouia component attributes. If type is not specified it just filters on the previous subject (does not go deeper into children).
     * @param id string
     * @param type optional string
     * @param opts optional - config object
     */
    ouiaComponentId(
      id: string,
      type?: string,
      opts?: Record<string, any>
    ): Chainable<Element>;

    /**
     *
     * @param type string
     * @param opts optional - config object
     */
    ouiaComponentType(
      type: string,
      opts?: Record<string, any>
    ): Chainable<Element>;

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
