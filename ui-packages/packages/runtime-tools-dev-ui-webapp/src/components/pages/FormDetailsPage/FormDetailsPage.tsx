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
import {
  Card,
  Label,
  PageSection,
  Text,
  TextVariants
} from '@patternfly/react-core';
import { OUIAProps, ouiaPageTypeAndObjectId } from '@kogito-apps/ouia-tools';
import FormDetailsContainer from '../../containers/FormDetailsContainer/FormDetailsContainer';
import '../../styles.css';
import { useHistory } from 'react-router-dom';
import { FormInfo } from '@kogito-apps/forms-list';
import { PageTitle } from '@kogito-apps/consoles-common';
import Moment from 'react-moment';

const FormDetailsPage: React.FC<OUIAProps> = () => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('form-detail');
  });
  const history = useHistory();
  const formData: FormInfo = history.location.state['formData'];
  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle
          title={formData.name}
          extra={<Label variant="outline">{formData.type}</Label>}
        />
        <Text component={TextVariants.p} style={{ marginTop: '10px' }}>
          <span style={{ fontWeight: 'bold' }}>Last modified:</span>{' '}
          <Moment fromNow>{formData.lastModified}</Moment>
        </Text>
      </PageSection>
      <PageSection>
        <Card className="Dev-ui__card-size">
          <FormDetailsContainer formData={formData} />
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default FormDetailsPage;
