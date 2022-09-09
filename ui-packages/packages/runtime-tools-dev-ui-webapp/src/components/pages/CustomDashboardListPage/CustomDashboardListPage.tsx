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

import React, { useEffect } from 'react';
import { Card, PageSection } from '@patternfly/react-core';
import { OUIAProps, ouiaPageTypeAndObjectId } from '@kogito-apps/ouia-tools';
import { PageSectionHeader } from '@kogito-apps/consoles-common';
import CustomDashboardListContainer from '../../containers/CustomDashboardListContainer/CustomDashboardListContainer';
import '../../styles.css';

const CustomDashboardListPage: React.FC<OUIAProps> = () => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('custom-dashboard-list');
  });

  return (
    <React.Fragment>
      <PageSectionHeader titleText="Dashboards" />
      <PageSection>
        <Card className="Dev-ui__card-size">
          <CustomDashboardListContainer />
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default CustomDashboardListPage;
