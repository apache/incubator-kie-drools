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

import React, { useState } from 'react';
import {
  Alert,
  AlertActionCloseButton,
  AlertActionLink
} from '@patternfly/react-core';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/components-common';

export interface Notification {
  type: 'success' | 'error';
  message: string;
  details?: string;
  customAction?: Action;
  close: () => void;
}

export interface Action {
  label: string;
  onClick: () => void;
}

interface IOwnProps {
  notification: Notification;
}

const FormNotification: React.FC<IOwnProps & OUIAProps> = ({
  notification,
  ouiaId,
  ouiaSafe
}) => {
  const variant = notification.type === 'error' ? 'danger' : 'success';

  const [showDetails, setShowDetails] = useState<boolean>(false);

  return (
    <Alert
      isInline
      title={notification.message}
      variant={variant}
      actionLinks={
        <React.Fragment>
          {notification.details && (
            <AlertActionLink onClick={() => setShowDetails(!showDetails)}>
              View details
            </AlertActionLink>
          )}
          {notification.customAction && (
            <AlertActionLink onClick={notification.customAction.onClick}>
              {notification.customAction.label}
            </AlertActionLink>
          )}
        </React.Fragment>
      }
      actionClose={<AlertActionCloseButton onClose={notification.close} />}
      {...componentOuiaProps(ouiaId, 'form-notification-alert', ouiaSafe)}
    >
      {showDetails && notification.details && <p>{notification.details}</p>}
    </Alert>
  );
};

export default FormNotification;
