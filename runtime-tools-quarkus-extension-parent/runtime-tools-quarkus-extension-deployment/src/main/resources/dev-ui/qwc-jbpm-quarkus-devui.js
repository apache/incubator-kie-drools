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

import {html, LitElement} from 'lit';
import {
    dataIndexUrl,
    devUIUrl,
    extensionBasePath,
    isTracingEnabled,
    openapiPath,
    trustyServiceUrl,
    userData
} from 'build-time-data';
import {RouterController} from 'router-controller';

export class QwcJbpmQuarkusDevui extends LitElement {
    _routerController = new RouterController(this);

    constructor() {
        super();
    }

    render() {
        return html`
            <div id="envelope-app" style="height: 100%"></div>`;
    }

    async connectedCallback() {
        super.connectedCallback();
        await this.updateComplete
        if (!document.querySelector('#jbpm-devui-script')) {
            const script = document.createElement('script');
            script.setAttribute("async", "");
            script.setAttribute("id", "jbpm-devui-script");
            script.setAttribute("src", `${extensionBasePath}/resources/webapp/standalone.js`);
            script.addEventListener("load", () => {
                this.initUI()
            })
            document.head.appendChild(script);
        } else {
            this.initUI();
        }
    }

    initUI() {
        const metadata = this._routerController.getCurrentMetaData();
        const container = this.renderRoot.querySelector("#envelope-app");
        RuntimeToolsDevUI.open({
            container: container,
            isDataIndexAvailable: true,
            isTracingEnabled: isTracingEnabled,
            dataIndexUrl: `${dataIndexUrl ?? "http://localhost:8180"}/graphql`,
            trustyServiceUrl: `${trustyServiceUrl ?? "http://localhost:1336"}`,
            page: metadata.page ?? "Processes",
            devUIUrl: `${devUIUrl ?? window.location.origin}`,
            openApiPath: `${openapiPath ?? "q/openapi.json"}`,
            availablePages: ["Processes", "Jobs", "Tasks", "Forms"],
            users: userData ?? []
        });
    }
}

customElements.define('qwc-jbpm-quarkus-devui', QwcJbpmQuarkusDevui);