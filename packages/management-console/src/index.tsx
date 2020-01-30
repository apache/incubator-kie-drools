import '@patternfly/patternfly/patternfly.css';
import React from 'react';
import ReactDOM from 'react-dom';
import ApolloClient from 'apollo-boost';
import { ApolloProvider } from 'react-apollo';
import Keycloak from "keycloak-js";
import axios from "axios";
import BaseLayout from "./components/Templates/BaseComponent/BaseLayout";

const client = new ApolloClient({
  uri: process.env.KOGITO_DATAINDEX_HTTP_URL + "/graphql",
  request: (operation) => {
    if (process.env.KOGITO_AUTH_ENABLED) {
      const kcInfo = JSON.parse(localStorage.getItem("keycloakData"));
      const token = kcInfo.token;
      operation.setContext({
        headers: {
          authorization: token ? `Bearer ${token}` : ''
        }
      })
    }
  }
});

const appRender = () => {
  ReactDOM.render(
    <ApolloProvider client={client}>
      <BaseLayout />
    </ApolloProvider>,
    document.getElementById('root')
  )
};

if (process.env.KOGITO_AUTH_ENABLED) {
  const keycloakConf = {
    realm: process.env.KOGITO_KEYCLOAK_REALM,
    url: process.env.KOGITO_KEYCLOAK_URL + "/auth",
    clientId: process.env.KOGITO_KEYCLOAK_CLIENT_ID
  };

  const kc = Keycloak(keycloakConf);

  kc.init({ onLoad: "login-required" })
    .success(authenticated => {
      if (authenticated) {
        localStorage.setItem("keycloakData", JSON.stringify(kc));
        appRender();
      }
    });

  axios.interceptors.request.use(config => {
    kc.updateToken(5)
      .success(() => {
        config.headers.Authorization = 'Bearer ' + kc.token;
        localStorage.setItem("keycloakData", JSON.stringify(kc));
        return Promise.resolve(config)
      })
    return config;
  });

} else {
  appRender();
}