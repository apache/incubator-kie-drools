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
import React, { useEffect, useState } from 'react';
import { Card } from '@patternfly/react-core/dist/js/components/Card';
import { PageSection } from '@patternfly/react-core/dist/js/components/Page';
import { Label } from '@patternfly/react-core/dist/js/components/Label';
import {
  Text,
  TextVariants
} from '@patternfly/react-core/dist/js/components/Text';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import FormDetailsContainer from '../../containers/FormDetailsContainer/FormDetailsContainer';
import '../../styles.css';
import { useHistory } from 'react-router-dom';
import { FormInfo } from '@kogito-apps/forms-list';
import { PageTitle } from '@kogito-apps/consoles-common/dist/components/layout/PageTitle';
import {
  FormNotification,
  Notification
} from '@kogito-apps/components-common/dist/components/FormNotification';
import Moment from 'react-moment';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';
const FormDetailsPage: React.FC<OUIAProps> = () => {
  const [notification, setNotification] = useState<Notification>();

  useEffect(() => {
    return ouiaPageTypeAndObjectId('form-detail');
  });
  const history = useHistory();
  const formData: FormInfo = history.location.state['formData'];

  const onSuccess = () => {
    const message = `The form '${formData.name}.${formData.type}' has been successfully saved.`;

    showNotification('success', message);
  };

  const onError = (details?: string) => {
    const message = `The form '${formData.name}.${formData.type}' couldn't be saved.`;

    showNotification('error', message, details);
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
      close: () => {
        setNotification(null);
      }
    });
  };

  const getFormType = (type: string): string => {
    if (type.toLowerCase() === 'html') {
      return 'HTML';
    } else if (type.toLowerCase() === 'tsx') {
      return 'REACT';
    } else {
      return type;
    }
  };

  const appContext = useDevUIAppContext();

  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle
          title={formData.name}
          extra={<Label variant="outline">{getFormType(formData.type)}</Label>}
        />
        <Text component={TextVariants.p} style={{ marginTop: '10px' }}>
          <span style={{ fontWeight: 'bold' }}>Last modified:</span>{' '}
          <Moment fromNow>{formData.lastModified}</Moment>
        </Text>
        {notification && (
          <div className="kogito-task-console__task-details-page">
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection>
        <Card className="Dev-ui__card-size">
          <FormDetailsContainer
            formData={formData}
            onSuccess={onSuccess}
            onError={onError}
            targetOrigin={appContext.getDevUIUrl()}
          />
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default FormDetailsPage;
