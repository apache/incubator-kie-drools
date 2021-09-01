/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { ApolloProvider } from 'react-apollo';
import { ApolloClient } from 'apollo-client';
import { MemoryRouter } from 'react-router';
import { User, PageLayout } from '@kogito-apps/consoles-common';
import ConsolesNav from '../DevUINav/DevUINav';
import JobsManagementContextProvider from '../../../channel/JobsManagement/JobsManagementContextProvider';
import ProcessDetailsContextProvider from '../../../channel/ProcessDetails/ProcessDetailsContextProvider';
import ProcessListContextProvider from '../../../channel/ProcessList/ProcessListContextProvider';
import TaskConsoleContextsProvider from '../../../channel/TaskInbox/TaskInboxContextProvider';
import TaskFormContextProvider from '../../../channel/TaskForms/TaskFormContextProvider';
import FormsListContextProvider from '../../../channel/FormsList/FormsListContextProvider';
import FormDetailsContextProvider from '../../../channel/FormDetails/FormDetailsContextProvider';
import DevUIAppContextProvider from '../../contexts/DevUIAppContextProvider';

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  users: User[];
  children: React.ReactElement;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  users,
  children
}) => {
  const renderPage = routeProps => {
    return (
      <PageLayout
        pageNavOpen={true}
        withHeader={false}
        PageNav={<ConsolesNav pathname={routeProps.location.pathname} />}
      >
        {children}
      </PageLayout>
    );
  };

  return (
    <ApolloProvider client={apolloClient}>
      <DevUIAppContextProvider users={users}>
        <TaskConsoleContextsProvider apolloClient={apolloClient}>
          <TaskFormContextProvider>
            <ProcessListContextProvider apolloClient={apolloClient}>
              <ProcessDetailsContextProvider apolloClient={apolloClient}>
                <JobsManagementContextProvider apolloClient={apolloClient}>
                  <FormsListContextProvider>
                    <FormDetailsContextProvider>
                      <MemoryRouter>
                        <Switch>
                          <Route path="/" render={renderPage} />
                        </Switch>
                      </MemoryRouter>
                    </FormDetailsContextProvider>
                  </FormsListContextProvider>
                </JobsManagementContextProvider>
              </ProcessDetailsContextProvider>
            </ProcessListContextProvider>
          </TaskFormContextProvider>
        </TaskConsoleContextsProvider>
      </DevUIAppContextProvider>
    </ApolloProvider>
  );
};

export default DevUILayout;
