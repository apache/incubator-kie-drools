import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import ApolloClient from 'apollo-client';
import { ApolloProvider } from 'react-apollo';
import Keycloak from 'keycloak-js';
import axios from 'axios';
import '@patternfly/patternfly/patternfly.css';
import { Nav, NavList, NavItem } from '@patternfly/react-core';
import { HttpLink } from 'apollo-link-http';
import { onError } from 'apollo-link-error';
import { InMemoryCache, NormalizedCacheObject } from 'apollo-cache-inmemory';
import { ServerUnavailable } from '@kogito-apps/common';
import PageLayout from './components/Templates/PageLayout/PageLayout';
import TaskConsoleContextProvider from './context/TaskConsoleContext/TaskConsoleContextProvider';
import taskConsoleLogo from './static/taskConsoleLogo.svg';

const httpLink = new HttpLink({
  // @ts-ignore
  uri: window.DATA_INDEX_ENDPOINT || process.env.KOGITO_DATAINDEX_HTTP_URL
});

const PageNav = (
  <Nav aria-label="Nav" theme="dark">
    <NavList>
      <NavItem>User Tasks</NavItem>
      <NavItem>User tasks with filters</NavItem>
    </NavList>
  </Nav>
);

const fallbackUI = onError(({ networkError }: any) => {
  if (networkError && networkError.stack === 'TypeError: Failed to fetch') {
    return ReactDOM.render(
      <ApolloProvider client={client}>
        <ServerUnavailable
          PageNav={PageNav}
          src={taskConsoleLogo}
          alt={'Task Console Logo'}
        />
      </ApolloProvider>,
      document.getElementById('root')
    );
  }
});

const cache = new InMemoryCache();
const client: ApolloClient<NormalizedCacheObject> = new ApolloClient({
  cache,
  link: fallbackUI.concat(httpLink),
  // @ts-ignore
  request: operation => {
    if (process.env.KOGITO_AUTH_ENABLED) {
      const kcInfo = JSON.parse(localStorage.getItem('keycloakData'));
      const token = kcInfo.token;
      operation.setContext({
        headers: {
          authorization: token ? `Bearer ${token}` : ''
        }
      });
    }
  }
});

const appRender = () => {
  ReactDOM.render(
    <ApolloProvider client={client}>
      <TaskConsoleContextProvider>
        <BrowserRouter>
          <Switch>
            <Route path="/" component={PageLayout} />
          </Switch>
        </BrowserRouter>
      </TaskConsoleContextProvider>
    </ApolloProvider>,
    document.getElementById('root')
  );
};

if (process.env.KOGITO_AUTH_ENABLED) {
  const keycloakConf = {
    realm: process.env.KOGITO_KEYCLOAK_REALM,
    url: process.env.KOGITO_KEYCLOAK_URL + '/auth',
    clientId: process.env.KOGITO_KEYCLOAK_CLIENT_ID
  };

  const kc = Keycloak(keycloakConf);

  kc.init({ onLoad: 'login-required' }).success(authenticated => {
    if (authenticated) {
      localStorage.setItem('keycloakData', JSON.stringify(kc));
      appRender();
    }
  });

  axios.interceptors.request.use(config => {
    kc.updateToken(5).success(() => {
      config.headers.Authorization = 'Bearer ' + kc.token;
      localStorage.setItem('keycloakData', JSON.stringify(kc));
      return Promise.resolve(config);
    });
    return config;
  });
} else {
  appRender();
}
