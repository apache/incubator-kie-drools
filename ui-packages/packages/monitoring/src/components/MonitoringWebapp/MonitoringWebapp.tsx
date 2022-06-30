/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

const DATA_INDEX_URL_PARAM = 'dataIndexUrl';
const DASHBOARD_PARAM = 'import';
const WORKFLOW_ID = 'workflowId';

interface DashbuilderComponentProps {
  dataIndexUrl?: string;
  dashboard?: string;
  workflow?: string;
}
/*

The following query parameters are supported:

dataIndexUrl: the URL to the data index. It must not end with a slash (e.g. http://localhost:8180)
refresh: The refresh in seconds. Use -1 for no refresh.
import: The dashboard to be imported. 
*/
const MonitoringWebapp: React.FC<DashbuilderComponentProps> = ({
  dashboard,
  dataIndexUrl,
  workflow
}) => {
  const queryParams = new URLSearchParams();
  if (dataIndexUrl) {
    queryParams.set(DATA_INDEX_URL_PARAM, dataIndexUrl);
  }
  if (dashboard) {
    queryParams.set(DASHBOARD_PARAM, dashboard);
  }
  if (workflow) {
    queryParams.set(WORKFLOW_ID, workflow);
  }
  return (
    <iframe
      src={`resources/webapp/monitoring-webapp/index.html?${queryParams.toString()}`}
      style={{ width: '100%', height: '100%', padding: '10px' }}
    />
  );
};

export default MonitoringWebapp;
