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
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { EmbeddedJobsManagement } from '@kogito-apps/jobs-management';
import { JobsManagementGatewayApi } from '../../../channel/JobsManagement';
import { useJobsManagementGatewayApi } from '../../../channel/JobsManagement/JobsManagementContext';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

const JobsManagementContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const gatewayApi: JobsManagementGatewayApi = useJobsManagementGatewayApi();
  const appContext = useDevUIAppContext();
  return (
    <EmbeddedJobsManagement
      driver={gatewayApi}
      targetOrigin={appContext.getDevUIUrl()}
      {...componentOuiaProps(ouiaId, 'jobs-management-container', ouiaSafe)}
    />
  );
};

export default JobsManagementContainer;
