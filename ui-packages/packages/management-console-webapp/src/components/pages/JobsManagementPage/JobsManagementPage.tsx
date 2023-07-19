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

import React, { useEffect } from 'react';
import { Card } from '@patternfly/react-core/dist/js/components/Card';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import { OUIAProps, ouiaPageTypeAndObjectId } from '@kogito-apps/ouia-tools';
import { PageSectionHeader } from '@kogito-apps/consoles-common';
import JobsManagementContainer from '../../containers/JobsManagementContainer/JobsManagementContainer';
import '../../styles.css';

const JobsManagementPage: React.FC<OUIAProps> = () => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('jobs-management');
  });

  return (
    <React.Fragment>
      <PageSectionHeader
        titleText="Jobs Management"
        breadcrumbText={['Home', 'Jobs']}
        breadcrumbPath={['/']}
      />
      <PageSection>
        <Card className="kogito-management-console__card-size">
          <JobsManagementContainer />
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default JobsManagementPage;
