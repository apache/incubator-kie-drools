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

import React, { useEffect, useState } from 'react';
import { Card, CardBody } from '@patternfly/react-core/dist/js/components/Card';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps
} from '@kogito-apps/ouia-tools';
import ProcessFormContainer from '../../containers/ProcessFormContainer/ProcessFormContainer';
import '../../styles.css';
import { useHistory } from 'react-router-dom';
import { ProcessDefinition } from '@kogito-apps/process-definition-list';
import { PageTitle } from '@kogito-apps/consoles-common';
import { FormNotification, Notification } from '@kogito-apps/components-common';
import InlineEdit from './components/InlineEdit/InlineEdit';
import { useProcessFormGatewayApi } from '../../../channel/ProcessForm/ProcessFormContext';

const ProcessFormPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const [notification, setNotification] = useState<Notification>();

  const history = useHistory();
  const gatewayApi = useProcessFormGatewayApi();

  const processDefinition: ProcessDefinition =
    history.location.state['processDefinition'];

  let processId: string;

  useEffect(() => {
    return ouiaPageTypeAndObjectId('process-form');
  });

  const goToProcessDefinition = () => {
    history.push('/Processes');
  };

  const goToProcessDetails = () => {
    history.push(`/Process/${processId}`);
  };

  const showNotification = (
    notificationType: 'error' | 'success',
    submitMessage: string,
    notificationDetails?: string
  ) => {
    setNotification({
      type: notificationType,
      message: submitMessage,
      details: notificationDetails,
      customActions: [
        {
          label: 'Go to process list',
          onClick: () => {
            setNotification(null);
            goToProcessDefinition();
          }
        },
        {
          label: 'Go to Process details',
          onClick: () => {
            setNotification(null);
            goToProcessDetails();
          }
        }
      ],
      close: () => {
        setNotification(null);
      }
    });
  };

  const onSubmitSuccess = (id: string): void => {
    processId = id;
    const message = `The process with id: ${id} has started successfully`;
    showNotification('success', message);
  };

  const onSubmitError = (details?: string) => {
    const message = 'Failed to start the process.';
    showNotification('error', message, details);
  };

  return (
    <React.Fragment>
      <PageSection
        {...componentOuiaProps(
          `title${ouiaId ? '-' + ouiaId : ''}`,
          'process-form-page-section',
          ouiaSafe
        )}
        variant="light"
      >
        <PageTitle
          title={`Start ${processDefinition.processName}`}
          extra={
            <InlineEdit
              setBusinessKey={(bk) => gatewayApi.setBusinessKey(bk)}
              getBusinessKey={() => gatewayApi.getBusinessKey()}
            />
          }
        />
        {notification && (
          <div>
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection
        {...componentOuiaProps(
          `content${ouiaId ? '-' + ouiaId : ''}`,
          'process-form-page-section',
          ouiaSafe
        )}
      >
        <Card className="Dev-ui__card-size">
          <CardBody className="pf-u-h-100">
            <ProcessFormContainer
              processDefinitionData={processDefinition}
              onSubmitSuccess={onSubmitSuccess}
              onSubmitError={onSubmitError}
            />
          </CardBody>
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default ProcessFormPage;
