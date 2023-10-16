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
  TextContent,
  Text
} from '@patternfly/react-core/dist/js/components/Text';
import { Modal } from '@patternfly/react-core/dist/js/components/Modal';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { BulkList, IOperation } from '../BulkList';
interface IOwnProps {
  modalTitle: JSX.Element;
  modalContent?: string;
  handleModalToggle: () => void;
  isModalOpen: boolean;
  resetSelected?: () => void;
  operationResult?: IOperation;
  processName?: string;
}
export const ProcessInfoModal: React.FC<IOwnProps & OUIAProps> = ({
  modalContent,
  modalTitle,
  isModalOpen,
  handleModalToggle,
  resetSelected,
  operationResult,
  processName,
  ouiaId,
  ouiaSafe
}) => {
  const onOkClick = () => {
    handleModalToggle();
    operationResult && resetSelected();
  };

  const createBoldText = (text: string, shouldBeBold: string): JSX.Element => {
    if (shouldBeBold && shouldBeBold.length > 0) {
      const textArray = text.split(shouldBeBold);
      return (
        <span>
          {textArray.map((item, index) => (
            <React.Fragment key={index}>
              {item}
              {index !== textArray.length - 1 && <b>{shouldBeBold}</b>}
            </React.Fragment>
          ))}
        </span>
      );
    } else {
      return <span>{text}</span>;
    }
  };

  return (
    <Modal
      variant="small"
      title=""
      header={modalTitle}
      isOpen={isModalOpen}
      onClose={onOkClick}
      aria-label="process list modal"
      aria-labelledby="process list modal"
      actions={[
        <Button key="confirm-selection" variant="primary" onClick={onOkClick}>
          OK
        </Button>
      ]}
      {...componentOuiaProps(ouiaId, 'process-list-modal', ouiaSafe)}
    >
      {operationResult !== undefined && (
        <BulkList operationResult={operationResult} />
      )}
      <TextContent>
        <Text>{createBoldText(modalContent, processName)}</Text>
      </TextContent>
    </Modal>
  );
};
