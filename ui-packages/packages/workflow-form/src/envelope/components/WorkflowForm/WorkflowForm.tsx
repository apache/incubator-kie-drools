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

import React, { useCallback, useState } from 'react';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { WorkflowDefinition, WorkflowFormDriver } from '../../../api';
import {
  Form,
  FormGroup,
  TextInput,
  ActionGroup,
  Button,
  Popover
} from '@patternfly/react-core';
import HelpIcon from '@patternfly/react-icons/dist/esm/icons/help-icon';
import { CodeEditor, Language } from '@patternfly/react-code-editor';

export interface WorkflowFormProps {
  workflowDefinition: WorkflowDefinition;
  driver: WorkflowFormDriver;
}

const WorkflowForm: React.FC<WorkflowFormProps & OUIAProps> = ({
  workflowDefinition,
  driver,
  ouiaId,
  ouiaSafe
}) => {
  const [type, setType] = useState<string>('');
  const [data, setData] = useState<string>('');

  const resetForm = useCallback(() => {
    driver.resetBusinessKey();
    setType('');
    setData('');
  }, []);

  const onSubmit = useCallback(() => {
    driver.startWorkflowCloudEvent({
      type,
      data
    });
    resetForm();
  }, [driver, type, data]);

  return (
    <div {...componentOuiaProps(ouiaId, 'workflow-form', ouiaSafe)}>
      <Form isHorizontal>
        <FormGroup
          label="Cloud Event Type"
          isRequired
          fieldId="formType"
          labelIcon={
            <Popover
              id="workflow-form-type-help"
              bodyContent={<div>The type of the cloud event to be triggered.</div>}
            >
              <button
                type="button"
                aria-label="More info for type field"
                onClick={e => e.preventDefault()}
                className="pf-c-form__group-label-help"
              >
                <HelpIcon noVerticalAlign />
              </button>
            </Popover>
          }
        >
          <TextInput
            value={type}
            isRequired
            type="text"
            id="formType"
            name="formType"
            onChange={setType}
          />
        </FormGroup>
        <FormGroup
          label="Cloud Event Data"
          isRequired
          fieldId="formData"
          labelIcon={
            <Popover
              id="workflow-form-data-help"
              bodyContent={
                <div>A JSON containing the data of the cloud event.</div>
              }
            >
              <button
                type="button"
                aria-label="More info for data field"
                onClick={e => e.preventDefault()}
                className="pf-c-form__group-label-help"
              >
                <HelpIcon noVerticalAlign />
              </button>
            </Popover>
          }
        >
          <CodeEditor
            isDarkTheme={false}
            isLineNumbersVisible={true}
            isReadOnly={false}
            isCopyEnabled={false}
            isMinimapVisible={true}
            isLanguageLabelVisible={false}
            code={data}
            language={Language.json}
            height="400px"
            onChange={setData}
          />
        </FormGroup>
        <ActionGroup>
          <Button variant="primary" onClick={onSubmit}>
            Start
          </Button>
          <Button variant="secondary" onClick={resetForm}>
            Reset
          </Button>
        </ActionGroup>
      </Form>
    </div>
  );
};

export default WorkflowForm;
