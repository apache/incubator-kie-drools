import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import ApolloClient from 'apollo-client';
import { ApolloProvider } from 'react-apollo';
import '@patternfly/patternfly/patternfly.css';
import { Nav, NavList, NavItem } from '@patternfly/react-core';
import { HttpLink } from 'apollo-link-http';
import { onError } from 'apollo-link-error';
import { InMemoryCache, NormalizedCacheObject } from 'apollo-cache-inmemory';
import {
  appRenderWithAxiosInterceptorConfig,
  getToken,
  isAuthEnabled,
  ServerUnavailable,
  DefaultUser
} from '@kogito-apps/common';
import PageLayout from './components/Templates/PageLayout/PageLayout';
import TaskConsoleContextProvider from './context/TaskConsoleContext/TaskConsoleContextProvider';
import taskConsoleLogo from './static/taskConsoleLogo.svg';
import { setContext } from 'apollo-link-context';

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

const setGQLContext = setContext((_, { headers }) => {
  if (isAuthEnabled()) {
    const token = getToken();
    return {
      headers: {
        ...headers,
        authorization: token ? `Bearer ${token}` : ''
      }
    };
  }
});

const cache = new InMemoryCache();
const client: ApolloClient<NormalizedCacheObject> = new ApolloClient({
  cache,
  link: setGQLContext.concat(fallbackUI.concat(httpLink))
});

const appRender = () => {
  ReactDOM.render(
    <ApolloProvider client={client}>
      <TaskConsoleContextProvider
        user={new DefaultUser('test', ['group1', 'group2'])}
      >
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

appRenderWithAxiosInterceptorConfig(() => appRender());
