import {css, html, LitElement} from 'lit';
import {JsonRpc} from 'jsonrpc';
import '@vaadin/icon';
import '@vaadin/button';
import {until} from 'lit/directives/until.js';
import '@vaadin/grid';
import {columnBodyRenderer} from '@vaadin/grid/lit.js';
import '@vaadin/grid/vaadin-grid-sort-column.js';

export class ModelComponent extends LitElement {

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
        "_model": {state: true}
    }

    // Components callbacks

    /**
     * Called when displayed
     */
    connectedCallback() {
        super.connectedCallback();
        this.jsonRpc.getModelInfo().then(jsonRpcResponse => {
            this._model = {};
            this._model.solutionClass = jsonRpcResponse.result.solutionClass;
            this._model.entityInfoList = [];
            jsonRpcResponse.result.entityClassList.forEach(entityClass => {
                const entityInfo = {};
                entityInfo.name = entityClass;
                entityInfo.genuineVariableList = jsonRpcResponse.result.entityClassToGenuineVariableListMap[entityClass];
                entityInfo.shadowVariableList = jsonRpcResponse.result.entityClassToShadowVariableListMap[entityClass];
                this._model.entityInfoList.push(entityInfo);
            });
        });
    }

    /**
     * Called when it needs to render the components
     * @returns {*}
     */
    render() {
        return html`${until(this._renderModel(), html`<span>Loading model...</span>`)}`;
    }

    // View / Templates

    _renderModel() {
        if (this._model) {
            let model = this._model;
            return html`
                <div>
                    <span>Solution Class:</span>
                    <span>${model.solutionClass}</span>
                </div>
                <vaadin-grid .items="${model.entityInfoList}" class="datatable" theme="no-border">
                    <vaadin-grid-column auto-width
                                        header="Entity Class"
                                        ${columnBodyRenderer(this._entityClassNameRenderer, [])}>
                    </vaadin-grid-column>
                    <vaadin-grid-column auto-width
                                        header="Genuine Variables"
                                        ${columnBodyRenderer(this._genuineVariablesRenderer, [])}>
                    </vaadin-grid-column>
                    <vaadin-grid-column auto-width
                                        header="Shadow Variables"
                                        ${columnBodyRenderer(this._shadowVariablesRenderer, [])}>
                    </vaadin-grid-column>
                </vaadin-grid>`;
        }
    }

    _entityClassNameRenderer(entityInfo) {
        return html`
            ${entityInfo.name}`;
    }

    _genuineVariablesRenderer(entityInfo) {
        return html`
            ${entityInfo.genuineVariableList.join(', ')}`;
    }

    _shadowVariablesRenderer(entityInfo) {
        return html`
            ${entityInfo.shadowVariableList.join(', ')}`;
    }

}

customElements.define('model-component', ModelComponent);