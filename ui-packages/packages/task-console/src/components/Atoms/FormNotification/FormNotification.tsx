/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import { Button, Modal, Text, TextContent } from '@patternfly/react-core';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/common';

interface IOwnProps {
  message: string;
  closeAction: () => void;
}

const FormNotification: React.FC<IOwnProps & OUIAProps> = ({
  message,
  closeAction,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Modal
      variant="small"
      title="Executing Task"
      isOpen={true}
      onClose={closeAction}
      actions={[
        <Button key="confirm-selection" variant="primary" onClick={closeAction}>
          OK
        </Button>
      ]}
      {...componentOuiaProps(ouiaId, 'form-notification-modal', ouiaSafe)}
    >
      <TextContent>
        <Text>{message}</Text>
      </TextContent>
    </Modal>
  );
};

export default FormNotification;
