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

import React, { useMemo } from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import { JobsManagementPage, ProcessesPage } from '../../pages';
import { NoData, PageNotFound } from '@kogito-apps/consoles-common';
import ProcessDetailsPage from '../../pages/ProcessDetailsPage/ProcessDetailsPage';
import TaskInboxPage from '../../pages/TaskInboxPage/TaskInboxPage';
import TaskDetailsPage from '../../pages/TaskDetailsPage/TaskDetailsPage';
import FormsListPage from '../../pages/FormsListPage/FormsListPage';
import FormDetailPage from '../../pages/FormDetailsPage/FormDetailsPage';
import { TrustyApp } from '@kogito-apps/trusty';
import ProcessFormPage from '../../pages/ProcessFormPage/ProcessFormPage';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';
import MonitoringPage from '../../pages/MonitoringPage/MonitoringPage';
import WorkflowFormPage from '../../pages/WorkflowFormPage/WorkflowFormPage';
import CustomDashboardListPage from '../../pages/CustomDashboardListPage/CustomDashboardListPage';
import CustomDashboardViewPage from '../../pages/CustomDashboardViewPage/CustomDashboardViewPage';
import CloudEventFormPage from '../../pages/CloudEventFormPage/CloudEventFormPage';

interface IOwnProps {
  trustyServiceUrl: string;
  dataIndexUrl: string;
  navigate: string;
}

type DevUIRoute = { enabled: () => boolean; node: React.ReactNode };

const DevUIRoutes: React.FC<IOwnProps> = ({
  trustyServiceUrl,
  dataIndexUrl,
  navigate
}) => {
  const context = useDevUIAppContext();

  const defaultPath = useMemo(() => {
    if (context.isProcessEnabled) {
      return '/JobsManagement';
    }
    if (context.isTracingEnabled) {
      return '/Audit';
    }
  }, [context.isProcessEnabled, context.isTracingEnabled]);

  const defaultButton = useMemo(() => {
    if (context.isProcessEnabled) {
      return 'Go to jobs management';
    }
    if (context.isTracingEnabled) {
      return 'Go to audit';
    }
  }, [context.isProcessEnabled, context.isTracingEnabled]);

  const routes: DevUIRoute[] = useMemo(
    () => [
      {
        enabled: () => true,
        node: (
          <Route
            key="0"
            exact
            path="/"
            render={() => <Redirect to={`/${navigate}`} />}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route key="1" exact path="/Processes" component={ProcessesPage} />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="2"
            exact
            path="/Process/:instanceID"
            component={ProcessDetailsPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="3"
            exact
            path="/JobsManagement"
            component={JobsManagementPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route key="4" exact path="/TaskInbox" component={TaskInboxPage} />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: <Route key="5" exact path="/Forms" component={FormsListPage} />
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="6"
            exact
            path="/Forms/:formName"
            component={FormDetailPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="7"
            exact
            path="/ProcessDefinition/Form/:processName"
            component={ProcessFormPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="8"
            exact
            path="/WorkflowDefinition/Form/:workflowName"
            component={WorkflowFormPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="9"
            exact
            path="/CustomDashboard"
            component={CustomDashboardListPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="10"
            exact
            path="/CustomDashboard/:customDashboardName"
            component={CustomDashboardViewPage}
          />
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route
            key="11"
            exact
            path="/TaskDetails/:taskId"
            render={(routeProps) => <TaskDetailsPage {...routeProps} />}
          />
        )
      },
      {
        enabled: () => context.isTracingEnabled,
        node: (
          <Route key="12" path="/Audit">
            <TrustyApp
              counterfactualEnabled={false}
              explanationEnabled={false}
              containerConfiguration={{
                pageWrapper: false,
                serverRoot: trustyServiceUrl,
                basePath: '/Audit',
                excludeReactRouter: true,
                useHrefLinks: false
              }}
            />
          </Route>
        )
      },
      {
        enabled: () => context.isProcessEnabled,
        node: (
          <Route key="13" path="/Monitoring">
            <MonitoringPage dataIndexUrl={dataIndexUrl} />
          </Route>
        )
      },
      {
        enabled: () => true,
        node: (
          <Route
            key="14"
            path="/NoData"
            render={(_props) => (
              <NoData
                {..._props}
                defaultPath={defaultPath}
                defaultButton={defaultButton}
              />
            )}
          />
        )
      },
      {
        enabled: () => context.isWorkflow(),
        node: (
          <Route
            key="16"
            exact
            path="/Processes/CloudEvent/:instanceId?"
            component={CloudEventFormPage}
          />
        )
      },
      {
        enabled: () => context.isWorkflow(),
        node: (
          <Route
            key="17"
            exact
            path="/WorkflowDefinitions/CloudEvent"
            component={CloudEventFormPage}
          />
        )
      },
      {
        enabled: () => true,
        node: (
          <Route
            key="18"
            path="*"
            render={(_props) => (
              <PageNotFound
                {..._props}
                defaultPath={defaultPath}
                defaultButton={defaultButton}
              />
            )}
          />
        )
      }
    ],
    [context.isProcessEnabled, context.isTracingEnabled, context.isWorkflow()]
  );

  return (
    <Switch>{routes.filter((r) => r.enabled()).map((r) => r.node)}</Switch>
  );
};

export default DevUIRoutes;
