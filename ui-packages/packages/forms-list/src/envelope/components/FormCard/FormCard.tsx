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
  FormGroup,
  Form
} from '@patternfly/react-core/dist/js/components/Form';
import {
  Card,
  CardBody,
  CardHeaderMain,
  CardHeader
} from '@patternfly/react-core/dist/js/components/Card';
import {
  TextVariants,
  Text
} from '@patternfly/react-core/dist/js/components/Text';
import { Label } from '@patternfly/react-core/dist/js/components/Label';
import { FormInfo } from '../../../api/FormsListEnvelopeApi';
import { FormsListDriver } from '../../../api/FormsListDriver';
import Moment from 'react-moment';
export interface FormCardProps {
  formData: FormInfo;
  driver: FormsListDriver;
}

const FormCard: React.FC<FormCardProps & OUIAProps> = ({
  formData,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const getLabel = (): string | JSX.Element => {
    switch (formData.type) {
      case 'HTML':
        return <Label variant="outline">HTML</Label>;
      case 'TSX':
        return <Label variant="outline">REACT</Label>;
      /* istanbul ignore next */
      default:
        return '';
    }
  };

  const handleCardClick = (): void => {
    driver.openForm(formData);
  };

  return (
    <Card
      {...componentOuiaProps(ouiaId, 'forms-card', ouiaSafe)}
      isSelectable
      onClick={handleCardClick}
    >
      <CardHeader>
        <CardHeaderMain>{getLabel()}</CardHeaderMain>
      </CardHeader>
      <CardHeader>
        <Text component={TextVariants.h1} className="pf-u-font-weight-bold">
          {formData.name}
        </Text>
      </CardHeader>
      <CardBody>
        <div className="pf-u-mt-md">
          <Form>
            <FormGroup label="Type" fieldId="type">
              <Text component={TextVariants.p}>{formData.type}</Text>
            </FormGroup>
            <FormGroup label="LastModified" fieldId="lastModified">
              <Text component={TextVariants.p}>
                <Moment fromNow>{formData.lastModified}</Moment>
              </Text>
            </FormGroup>
          </Form>
        </div>
      </CardBody>
    </Card>
  );
};

export default FormCard;
