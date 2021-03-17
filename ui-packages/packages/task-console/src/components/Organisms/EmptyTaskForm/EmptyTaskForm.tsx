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
import _ from 'lodash';
import {
  AppContext,
  componentOuiaProps,
  GraphQL,
  KogitoSpinner,
  OUIAProps,
  useKogitoAppContext
} from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateSecondaryActions,
  Title
} from '@patternfly/react-core';
import { InfoCircleIcon } from '@patternfly/react-icons';
import { convertActionsToButton } from '../../../util/uniforms/FormActionsUtils';
import { FormSchema } from '../../../util/uniforms/FormSchema';
import { TaskFormSubmit } from '../../../util/uniforms/TaskFormSubmitHandler/utils/TaskFormSubmit';

interface IOwnProps {
  task: UserTaskInstance;
  formSchema: FormSchema;
  onSubmitSuccess: (message: string) => void;
  onSubmitError: (message: string, details?: string) => void;
}

enum State {
  READY,
  SUBMITTING,
  SUBMITTED
}

const EmptyTaskForm: React.FC<IOwnProps & OUIAProps> = ({
  task,
  formSchema,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe
}) => {
  const appContext: AppContext = useKogitoAppContext();

  const [formState, setFormState] = useState<State>(State.READY);

  const [submit] = useState<TaskFormSubmit>(
    new TaskFormSubmit(
      task,
      appContext.getCurrentUser(),
      phase => {
        setFormState(State.SUBMITTED);
        onSubmitSuccess(phase);
      },
      (phase, errorMessage) => {
        setFormState(State.SUBMITTED);
        onSubmitError(phase, errorMessage);
      }
    )
  );

  if (formState == State.SUBMITTING) {
    return (
      <KogitoSpinner
        spinnerText={'Submitting form ...'}
        ouiaId={(ouiaId ? ouiaId : 'task-form') + '-spinner-submitting'}
        ouiaSafe={ouiaSafe}
      />
    );
  }

  const canTransition = (): boolean => {
    return !task.completed && !_.isEmpty(formSchema.phases);
  };

  const doSubmit = async (phase: string) => {
    setFormState(State.SUBMITTING);
    await submit.submit(phase, {});
  };

  const buildFormActions = () => {
    return formSchema.phases.map(phase => {
      return {
        name: phase,
        execute: () => {
          doSubmit(phase);
        }
      };
    });
  };

  const actions = canTransition() ? (
    <EmptyStateSecondaryActions>
      {convertActionsToButton(buildFormActions(), formState == State.READY)}
    </EmptyStateSecondaryActions>
  ) : null;

  return (
    <EmptyState
      variant={'large'}
      {...componentOuiaProps(ouiaId, 'empty-task-form', ouiaSafe)}
    >
      <EmptyStateIcon icon={InfoCircleIcon} />
      <Title headingLevel="h4" size="lg">
        {'Cannot show task form'}
      </Title>
      <EmptyStateBody>
        <p>
          Task{' '}
          <b>
            {task.referenceName} ({task.id.substring(0, 5)})
          </b>
          &nbsp;doesn&apos;t have a form to show. This usually means that it
          doesn&apos;t require data to be filled by the user.
        </p>
        {canTransition() && (
          <>
            <br />
            <p>
              You can still use the actions bellow to move the task to the next
              phase.
            </p>
          </>
        )}
      </EmptyStateBody>
      {actions}
    </EmptyState>
  );
};

export default EmptyTaskForm;
