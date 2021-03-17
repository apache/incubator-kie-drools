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
  GraphQL,
  KogitoSpinner,
  useKogitoAppContext
} from '@kogito-apps/common';
import FormRenderer from '../../Molecules/FormRenderer/FormRenderer';
import { TaskFormSubmitHandler } from '../../../util/uniforms/TaskFormSubmitHandler/TaskFormSubmitHandler';
import { FormSchema } from '../../../util/uniforms/FormSchema';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { OUIAProps } from '@kogito-apps/common';

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

const TaskFormRenderer: React.FC<IOwnProps & OUIAProps> = ({
  task,
  formSchema,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe
}) => {
  const appContext: AppContext = useKogitoAppContext();
  const [formState, setFormState] = useState<State>(State.READY);
  const [formOutput, setFormOutput] = useState<any>(null);

  if (formState == State.SUBMITTING) {
    return (
      <KogitoSpinner
        spinnerText={'Submitting form ...'}
        ouiaId={(ouiaId ? ouiaId : 'task-form') + '-spinner-submitting'}
        ouiaSafe={ouiaSafe}
      />
    );
  }

  const notifySuccess = (phase: string) => {
    setFormState(State.SUBMITTED);
    onSubmitSuccess(phase);
  };

  const notifyError = (phase: string, error?: string) => {
    setFormState(State.SUBMITTED);
    onSubmitError(phase, error);
  };

  const formSubmitHandler = new TaskFormSubmitHandler(
    task,
    formSchema,
    appContext.getCurrentUser(),
    output => {
      setFormState(State.SUBMITTING);
      setFormOutput(output);
    },
    phase => notifySuccess(phase),
    (phase, errorMessage) => notifyError(phase, errorMessage)
  );

  const toJSON = (value: string) => {
    if (value) {
      try {
        return JSON.parse(value);
      } catch (e) {
        // do nothing
      }
    }
    return {};
  };

  const generateFormData = () => {
    const taskInputs = toJSON(task.inputs);

    if (!formOutput && !task.outputs) {
      return taskInputs;
    }

    const taskOutputs = formOutput || toJSON(task.outputs);

    return _.merge(taskInputs, taskOutputs);
  };

  const formData = generateFormData();

  const isReadOnly = (): boolean => {
    if (formState == State.SUBMITTED) {
      return true;
    }

    if (task.completed) {
      return true;
    }

    if (_.isEmpty(formSchema.phases)) {
      return true;
    }

    return false;
  };

  return (
    <FormRenderer
      formSchema={formSchema}
      model={formData}
      readOnly={isReadOnly()}
      formSubmitHandler={formSubmitHandler}
      ouiaId={(ouiaId ? ouiaId : 'task-form') + '-form-renderer'}
      ouiaSafe={ouiaSafe}
    />
  );
};

export default TaskFormRenderer;
