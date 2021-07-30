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
import { Redirect, Route, Switch } from 'react-router-dom';
import { JobsManagementPage, ProcessListPage } from '../../pages';
import { PageNotFound, NoData } from '@kogito-apps/consoles-common';
import ProcessDetailsPage from '../../pages/ProcessDetailsPage/ProcessDetailsPage';
import TaskInboxPage from '../../pages/TaskInboxPage/TaskInboxPage';
import TaskDetailsPage from '../../pages/TaskDetailsPage/TaskDetailsPage';

interface IOwnProps {
  navigate: string;
}

const DevUIRoutes: React.FC<IOwnProps> = ({ navigate }) => {
  return (
    <Switch>
      <Route exact path="/" render={() => <Redirect to={`/${navigate}`} />} />
      <Route exact path="/ProcessInstances" component={ProcessListPage} />
      <Route exact path="/Process/:instanceID" component={ProcessDetailsPage} />
      <Route exact path="/JobsManagement" component={JobsManagementPage} />
      <Route exact path="/TaskInbox" component={TaskInboxPage} />
      <Route
        exact
        path="/TaskDetails/:taskId"
        render={routeProps => <TaskDetailsPage {...routeProps} />}
      />
      <Route
        path="/NoData"
        render={_props => (
          <NoData
            {..._props}
            defaultPath="/JobsManagement"
            defaultButton="Go to jobs management"
          />
        )}
      />
      <Route
        path="*"
        render={_props => (
          <PageNotFound
            {..._props}
            defaultPath="/JobsManagement"
            defaultButton="Go to jobs management"
          />
        )}
      />
    </Switch>
  );
};

export default DevUIRoutes;
