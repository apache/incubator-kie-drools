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

import React, { useEffect, useRef, useState } from 'react';
import { Card, CardBody } from '@patternfly/react-core/dist/js/components/Card';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps
} from '@kogito-apps/ouia-tools';
import WorkflowFormContainer from '../../containers/WorkflowFormContainer/WorkflowFormContainer';
import '../../styles.css';
import { PageTitle } from '@kogito-apps/consoles-common';
import { FormNotification, Notification } from '@kogito-apps/components-common';
import { useHistory } from 'react-router-dom';
import { WorkflowDefinition } from '@kogito-apps/workflow-form';
import {
  InlineEdit,
  InlineEditApi
} from '../ProcessFormPage/components/InlineEdit/InlineEdit';
import { useWorkflowFormGatewayApi } from '../../../channel/WorkflowForm/WorkflowFormContext';

const WorkflowFormPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const [notification, setNotification] = useState<Notification>();
  const inlineEditRef = useRef<InlineEditApi>();

  const history = useHistory();
  const gatewayApi = useWorkflowFormGatewayApi();

  const workflowDefinition: WorkflowDefinition =
    history.location.state['workflowDefinition'];

  const goToWorkflowList = () => {
    history.push('/Processes');
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
          label: 'Go to workflow list',
          onClick: () => {
            setNotification(null);
            goToWorkflowList();
          }
        }
      ],
      close: () => {
        setNotification(null);
      }
    });
  };

  const onSubmitSuccess = (message: string): void => {
    showNotification('success', message);
  };

  const onSubmitError = (details?: string) => {
    const message = 'Failed to trigger workflow.';
    showNotification('error', message, details);
  };

  const onResetForm = () => {
    gatewayApi.setBusinessKey('');
    inlineEditRef.current!.reset();
  };

  const getBusinessKey = () => {
    return gatewayApi.getBusinessKey();
  };

  useEffect(() => {
    onResetForm();
    return ouiaPageTypeAndObjectId('workflow-form');
  }, []);

  return (
    <React.Fragment>
      <PageSection
        {...componentOuiaProps(
          `title${ouiaId ? '-' + ouiaId : ''}`,
          'workflow-form-page-section',
          ouiaSafe
        )}
        variant="light"
      >
        <PageTitle
          title={`Start New Workflow`}
          extra={
            <InlineEdit
              ref={inlineEditRef}
              setBusinessKey={(bk) => gatewayApi.setBusinessKey(bk)}
              getBusinessKey={getBusinessKey}
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
          'workflow-form-page-section',
          ouiaSafe
        )}
      >
        <Card className="Dev-ui__card-size">
          <CardBody className="pf-u-h-100">
            <WorkflowFormContainer
              workflowDefinitionData={workflowDefinition}
              onSubmitSuccess={onSubmitSuccess}
              onSubmitError={onSubmitError}
              onResetForm={onResetForm}
            />
          </CardBody>
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default WorkflowFormPage;
