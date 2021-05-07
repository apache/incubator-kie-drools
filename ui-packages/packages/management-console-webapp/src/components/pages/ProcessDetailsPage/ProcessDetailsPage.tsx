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
import { PageSection } from '@patternfly/react-core';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/components-common';
import { RouteComponentProps } from 'react-router-dom';
import { PageSectionHeader } from '@kogito-apps/consoles-common';
import ProcessDetailsContainer from '../../containers/ProcessDetailsContainer/ProcessDetailsContainer';
import { StaticContext } from 'react-router';
import * as H from 'history';
import '../../styles.css';

interface MatchProps {
  instanceID: string;
}

const ProcessDetailsPage: React.FC<RouteComponentProps<
  MatchProps,
  StaticContext,
  H.LocationState
> &
  OUIAProps> = ({ ...props }) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('process-details');
  });
  const processId = props.match.params.instanceID;
  return (
    <React.Fragment>
      <PageSectionHeader
        titleText="Process Details"
        breadcrumbText={['Home', 'Process Instances']}
        breadcrumbPath={['/']}
      />
      <PageSection>
        <ProcessDetailsContainer processId={processId} />
      </PageSection>
    </React.Fragment>
  );
};

export default ProcessDetailsPage;
