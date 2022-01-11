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
import { Button, TextInput } from '@patternfly/react-core';
import { PencilAltIcon, CheckIcon, TimesIcon } from '@patternfly/react-icons';
import { useProcessFormGatewayApi } from '../../../../../channel/ProcessForm/ProcessFormContext';
import { ProcessFormGatewayApi } from '../../../../../channel/ProcessForm/ProcessFormGatewayApi';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';

const InlineEdit: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const [isEditable, setIsEditable] = useState<boolean>(false);
  const [inputValue, setInputValue] = useState<string>('');

  const gatewayApi: ProcessFormGatewayApi = useProcessFormGatewayApi();
  const businessKeyValue: string = gatewayApi.getBusinessKey();

  useEffect(() => {
    if (businessKeyValue.length === 0) {
      setInputValue('');
    }
  }, [businessKeyValue]);

  const toggleEditableMode = (): void => {
    setIsEditable(!isEditable);
  };
  const confirmBusinessKey = (isConfirmed: boolean): void => {
    if (isConfirmed) {
      gatewayApi.setBusinessKey(inputValue);
      toggleEditableMode();
    } else {
      toggleEditableMode();
    }
  };

  return (
    <div
      {...componentOuiaProps(ouiaId, 'inline-edit', ouiaSafe)}
      className={`pf-c-inline-edit ${isEditable && 'pf-m-inline-editable'}`}
      id="inline-edit-toggle-example"
    >
      <div className="pf-c-inline-edit__group">
        <div
          className="pf-c-inline-edit__value"
          id="single-editable-example-label"
        >
          {gatewayApi.getBusinessKey().length > 0 ? (
            gatewayApi.getBusinessKey()
          ) : (
            <span className="pf-u-disabled-color-100">Business key</span>
          )}
        </div>
        <div className="pf-c-inline-edit__action pf-m-enable-editable">
          <Button variant="plain" onClick={toggleEditableMode}>
            <PencilAltIcon />
          </Button>
        </div>
      </div>

      <div className="pf-c-inline-edit__group">
        <div className="pf-c-inline-edit__input">
          <TextInput
            value={inputValue}
            type="text"
            placeholder={'Enter buisness key'}
            onChange={setInputValue}
            aria-label="text input example"
          />
        </div>
        <div className="pf-c-inline-edit__group pf-m-action-group pf-m-icon-group">
          <div className="pf-c-inline-edit__action pf-m-valid">
            <Button variant="plain" onClick={() => confirmBusinessKey(true)}>
              <CheckIcon />
            </Button>
          </div>
          <div className="pf-c-inline-edit__action">
            <Button variant="plain" onClick={() => confirmBusinessKey(false)}>
              <TimesIcon />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InlineEdit;
