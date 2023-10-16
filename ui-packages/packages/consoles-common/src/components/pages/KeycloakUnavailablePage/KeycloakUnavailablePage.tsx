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
import {
  Card,
  CardBody,
  CardHeader,
  CardHeaderMain,
  CardTitle
} from '@patternfly/react-core/dist/js/components/Card';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Brand } from '@patternfly/react-core/dist/js/components/Brand';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import React from 'react';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import kogitoLogo from '../../../static/kogito.png';
export const KeycloakUnavailablePage: React.FC<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        paddingTop: '5%',
        background: '#868686',
        height: '100%'
      }}
      {...componentOuiaProps(ouiaId, 'server-unavailable', ouiaSafe)}
    >
      {' '}
      <Card
        style={{
          maxHeight: '300px'
        }}
      >
        <CardHeader>
          <CardHeaderMain>
            <Brand
              src={kogitoLogo}
              alt="Kogito keycloak"
              style={{ height: '30px' }}
            />
          </CardHeaderMain>
        </CardHeader>
        <CardBody isFilled={false}></CardBody>
        <CardTitle>Error:503 - Server unavailable</CardTitle>
        <CardBody isFilled={false}></CardBody>
        <CardBody>Sorry.. the keycloak server seems to be down</CardBody>
        <CardBody isFilled={false}></CardBody>
        <Bullseye>
          <span>
            Please contact administrator or{' '}
            <Button
              variant="link"
              onClick={() => window.location.reload()}
              isInline
            >
              click here to retry
            </Button>
          </span>
        </Bullseye>
        <CardBody isFilled={false}></CardBody>
      </Card>
    </div>
  );
};
