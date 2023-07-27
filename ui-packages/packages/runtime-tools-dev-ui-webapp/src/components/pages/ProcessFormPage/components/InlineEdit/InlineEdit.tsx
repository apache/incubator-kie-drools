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

import React, { useEffect, useImperativeHandle, useState } from 'react';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { TextInput } from '@patternfly/react-core/dist/js/components/TextInput';
import { PencilAltIcon } from '@patternfly/react-icons/dist/js/icons/pencil-alt-icon';
import { CheckIcon } from '@patternfly/react-icons/dist/js/icons/check-icon';
import { TimesIcon } from '@patternfly/react-icons/dist/js/icons/times-icon';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

export interface InlineEditApi {
  reset: () => void;
}

interface InlineEditProps {
  setBusinessKey: (businessKey: string) => void;
  getBusinessKey: () => string;
}

export const InlineEdit = React.forwardRef<
  InlineEditApi,
  InlineEditProps & OUIAProps
>(({ setBusinessKey, getBusinessKey, ouiaId, ouiaSafe }, forwardedRef) => {
  const [isEditable, setIsEditable] = useState<boolean>(false);
  const [inputValue, setInputValue] = useState<string>('');
  const [currentBusinessKey, setCurrentBusinessKey] = useState<string>(
    getBusinessKey()
  );

  useEffect(() => {
    if (currentBusinessKey.length === 0) {
      setInputValue('');
    }
  }, [currentBusinessKey]);

  const toggleEditableMode = (): void => {
    setIsEditable(!isEditable);
  };
  const confirmBusinessKey = (isConfirmed: boolean): void => {
    if (isConfirmed) {
      setBusinessKey(inputValue);
      setCurrentBusinessKey(inputValue);
    }

    toggleEditableMode();
  };

  useImperativeHandle(
    forwardedRef,
    () => ({
      reset: () => {
        setInputValue('');
        setCurrentBusinessKey('');
      }
    }),
    []
  );

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
          {currentBusinessKey.length > 0 ? (
            currentBusinessKey
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
            placeholder={'Enter business key'}
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
});

export default InlineEdit;
