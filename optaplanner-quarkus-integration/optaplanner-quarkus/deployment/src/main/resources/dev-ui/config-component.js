import {css, html, LitElement} from 'lit';
import {JsonRpc} from 'jsonrpc';
import '@vaadin/icon';
import '@vaadin/button';
import {until} from 'lit/directives/until.js';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-sort-column.js';

export class ConfigComponent extends LitElement {

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
        "_config": {state: true}
    }

    // Components callbacks

    /**
     * Called when displayed
     */
    connectedCallback() {
        super.connectedCallback();
        this.jsonRpc.getConfig().then(jsonRpcResponse => {
            this._config = jsonRpcResponse.result.config;
        });
    }

    /**
     * Called when it needs to render the components
     * @returns {*}
     */
    render() {
        return html`${until(this._renderConfig(), html`<span>Loading config...</span>`)}`;
    }

    // View / Templates

    _renderConfig() {
        if (this._config) {
            let config = this._config;
            return html`
                <pre>${config}</pre>`;
        }
    }
}

customElements.define('config-component', ConfigComponent);