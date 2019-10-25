import '@patternfly/patternfly/patternfly.css';
import ApolloClient from 'apollo-boost';
import React from 'react';
import { ApolloProvider } from 'react-apollo';
import ReactDOM from 'react-dom';
import BaseLayout from './components/Templates/BaseComponent/BaseLayout';

const client = new ApolloClient({
  uri: 'http://localhost:4000/graphql'
});
ReactDOM.render(
  <ApolloProvider client={client}>
    <BaseLayout />
  </ApolloProvider>,
  document.getElementById('root') as HTMLElement
);
