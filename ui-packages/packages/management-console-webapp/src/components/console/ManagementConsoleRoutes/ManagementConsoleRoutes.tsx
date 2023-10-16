/**
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
import React from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import {
  JobsManagementPage,
  ProcessListPage,
  ProcessDetailsPage
} from '../../pages';
import { PageNotFound } from '@kogito-apps/consoles-common/dist/components/pages/PageNotFound';
import { NoData } from '@kogito-apps/consoles-common/dist/components/pages/NoData';

const ManagementConsoleRoutes: React.FC = () => {
  return (
    <Switch>
      <Route
        exact
        path="/"
        render={() => <Redirect to="/ProcessInstances" />}
      />
      <Route exact path="/ProcessInstances" component={ProcessListPage} />
      <Route exact path="/JobsManagement" component={JobsManagementPage} />
      <Route exact path="/Process/:instanceID" component={ProcessDetailsPage} />
      <Route
        path="/NoData"
        render={(_props) => (
          <NoData
            {..._props}
            defaultPath="/JobsManagement"
            defaultButton="Go to jobs management"
          />
        )}
      />
      <Route
        path="*"
        render={(_props) => (
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

export default ManagementConsoleRoutes;
