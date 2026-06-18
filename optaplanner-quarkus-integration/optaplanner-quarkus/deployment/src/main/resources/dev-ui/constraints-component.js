/*
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

import {css, html, LitElement} from 'lit';
import {JsonRpc} from 'jsonrpc';
import '@vaadin/icon';
import '@vaadin/button';
import {until} from 'lit/directives/until.js';
import '@vaadin/grid';
import {columnBodyRenderer} from '@vaadin/grid/lit.js';
import '@vaadin/grid/vaadin-grid-sort-column.js';

export class ConstraintsComponent extends LitElement {

    jsonRpc = new JsonRpc("OptaPlanner Solver");

    // Component style
    static styles = css`
      .button {
        background-color: transparent;
        cursor: pointer;
      }

      .clearIcon {
        color: orange;
      }
    `;

    // Component properties
    static properties = {
        "_constraints": {state: true}
    }

    // Components callbacks

    /**
     * Called when displayed
     */
    connectedCallback() {
        super.connectedCallback();
        this.jsonRpc.getConstraints().then(jsonRpcResponse => {
            this._constraints = [];
            jsonRpcResponse.result.forEach(c => {
                this._constraints.push(c);
            });
        });
    }

    /**
     * Called when it needs to render the components
     * @returns {*}
     */
    render() {
        return html`${until(this._renderConstraintTable(), html`<span>Loading constraints...</span>`)}`;
    }

    // View / Templates

    _renderConstraintTable() {
        if (this._constraints) {
            let constraints = this._constraints;
            return html`
                <vaadin-grid .items="${constraints}" class="datatable" theme="no-border">
                    <vaadin-grid-column auto-width
                                        header="Name"
                                        ${columnBodyRenderer(this._nameRenderer, [])}>
                    </vaadin-grid-column>
                </vaadin-grid>`;
        }
    }

    _nameRenderer(constraint) {
        return html`
            ${constraint}`;
    }

}

customElements.define('constraints-component', ConstraintsComponent);