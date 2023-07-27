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
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { RouteComponentProps } from 'react-router-dom';
import { PageSectionHeader } from '@kogito-apps/consoles-common/dist/components/layout/PageSectionHeader';
import ProcessListContainer from '../../containers/ProcessListContainer/ProcessListContainer';
import { StaticContext } from 'react-router';
import * as H from 'history';
import '../../styles.css';
import { ProcessListState } from '@kogito-apps/management-console-shared/dist/types';

interface MatchProps {
  instanceID: string;
}

const ProcessListPage: React.FC<
  RouteComponentProps<MatchProps, StaticContext, H.LocationState> & OUIAProps
> = (props) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('process-instances');
  });
  const initialState: ProcessListState =
    props.location && (props.location.state as ProcessListState);

  return (
    <React.Fragment>
      <PageSectionHeader
        titleText="Process Instances"
        breadcrumbText={['Home', 'Processes']}
        breadcrumbPath={['/']}
        ouiaId={props.ouiaId}
      />
      <PageSection
        {...componentOuiaProps(
          props.ouiaId,
          'page-section-content',
          props.ouiaSafe
        )}
      >
        <Card className="kogito-management-console__card-size">
          <ProcessListContainer initialState={initialState} />
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default ProcessListPage;
