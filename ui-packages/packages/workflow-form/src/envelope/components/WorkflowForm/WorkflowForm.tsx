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
  ActionGroup,
  Form,
  FormGroup
} from '@patternfly/react-core/dist/js/components/Form';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import { Stack, StackItem } from '@patternfly/react-core/dist/js/layouts/Stack';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { Alert } from '@patternfly/react-core/dist/js/components/Alert';
import { Popover } from '@patternfly/react-core/dist/js/components/Popover';
import { ValidatedOptions } from '@patternfly/react-core/dist/js/helpers';
import HelpIcon from '@patternfly/react-icons/dist/esm/icons/help-icon';
import {
  CodeEditor,
  Language
} from '@patternfly/react-code-editor/dist/js/components/CodeEditor';
import ExclamationCircleIcon from '@patternfly/react-icons/dist/esm/icons/exclamation-circle-icon';
import { validateWorkflowData } from './validateWorkflowData';
import { KogitoSpinner } from '@kogito-apps/components-common';

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
  const [data, setData] = useState<string>('');
  const [isValid, setIsValid] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const resetForm = useCallback(() => {
    driver.resetBusinessKey();
    setData('');
    setIsValid(true);
  }, []);

  const onSubmit = useCallback(async () => {
    const valid = validateWorkflowData(data);

    setIsValid(valid);

    if (!valid) {
      return;
    }

    setIsLoading(true);
    await driver.startWorkflow(
      workflowDefinition.endpoint,
      data.trim() ? JSON.parse(data) : {}
    );

    setIsLoading(false);
    resetForm();
  }, [driver, data]);

  if (isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner
          spinnerText="Starting workflow..."
          ouiaId="workflow-form-loading"
        />
      </Bullseye>
    );
  }

  return (
    <div {...componentOuiaProps(ouiaId, 'workflow-form', ouiaSafe)}>
      <Stack hasGutter>
        <StackItem>
          <Alert
            isInline
            variant="info"
            title="Couldn't find JSON schema to display the Start Form."
          >
            <p>
              Please type the data in JSON format in the Code Editor and press
              the <b>Start</b> to submit it and start the Workflow.
            </p>
            <p>
              If you want to use a Form to start the workflow, please provide a
              valid JSON schema in the <code>dataInputSchema</code> property in
              your workflow file.
            </p>
          </Alert>
        </StackItem>
        <StackItem>
          <Form isHorizontal>
            <FormGroup
              label="Start Workflow Data"
              isRequired
              fieldId="workflowData"
              helperTextInvalid={
                !isValid && 'The Workflow Data should have a JSON format.'
              }
              helperTextInvalidIcon={<ExclamationCircleIcon />}
              validated={
                !isValid ? ValidatedOptions.error : ValidatedOptions.default
              }
              labelIcon={
                <Popover
                  id="workflow-form-data-help"
                  bodyContent={
                    <div>
                      Fill the data to start the Workflow in JSON format.
                    </div>
                  }
                >
                  <button
                    type="button"
                    aria-label="More info for data field"
                    onClick={(e) => e.preventDefault()}
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
        </StackItem>
      </Stack>
    </div>
  );
};

export default WorkflowForm;
