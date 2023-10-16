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
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  TextVariants,
  Text
} from '@patternfly/react-core/dist/js/components/Text';
import {
  Card,
  CardBody,
  CardHeaderMain,
  CardHeader
} from '@patternfly/react-core/dist/js/components/Card';
import {
  FormGroup,
  Form
} from '@patternfly/react-core/dist/js/components/Form';
import { CustomDashboardInfo } from '../../../api/CustomDashboardListEnvelopeApi';
import { CustomDashboardListDriver } from '../../../api/CustomDashboardListDriver';
import Moment from 'react-moment';
export interface CustomDashboardCardProps {
  customDashboardData: CustomDashboardInfo;
  driver: CustomDashboardListDriver;
}

const CustomDashboardCard: React.FC<CustomDashboardCardProps & OUIAProps> = ({
  customDashboardData,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const handleCardClick = (): void => {
    driver.openDashboard(customDashboardData);
  };

  return (
    <Card
      {...componentOuiaProps(ouiaId, 'customDashboard-card', ouiaSafe)}
      isSelectable
      onClick={handleCardClick}
      data-testid="card"
    >
      <CardHeader>
        <CardHeaderMain>Empty</CardHeaderMain>
      </CardHeader>
      <CardHeader>
        <Text component={TextVariants.h1} className="pf-u-font-weight-bold">
          {customDashboardData.name}
        </Text>
      </CardHeader>
      <CardBody>
        <div className="pf-u-mt-md">
          <Form>
            <FormGroup label="Path" fieldId="path">
              <Text component={TextVariants.p}>{customDashboardData.path}</Text>
            </FormGroup>
            <FormGroup label="LastModified" fieldId="lastModified">
              <Text component={TextVariants.p}>
                <Moment fromNow>{customDashboardData.lastModified}</Moment>
              </Text>
            </FormGroup>
          </Form>
        </div>
      </CardBody>
    </Card>
  );
};

export default CustomDashboardCard;
