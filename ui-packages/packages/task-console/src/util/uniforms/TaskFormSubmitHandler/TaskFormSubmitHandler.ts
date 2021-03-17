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

import { GraphQL, User } from '@kogito-apps/common';
import _ from 'lodash';
import { FormSubmitHandler } from '../FormSubmitHandler/FormSubmitHandler';
import { FormSchema } from '../FormSchema';
import UserTaskInstance = GraphQL.UserTaskInstance;
import { TaskFormSubmit } from './utils/TaskFormSubmit';
import { FormAction } from '../FormActionsUtils';

interface FormAssignments {
  inputs: string[];
  outputs: string[];
}

export class TaskFormSubmitHandler implements FormSubmitHandler {
  private readonly userTaskInstance: UserTaskInstance;
  private readonly formSchema: FormSchema;
  private readonly onSubmit?: (data: any) => void;

  private taskformSubmit: TaskFormSubmit;

  private readonly assignments;

  private selectedPhase: string;

  constructor(
    userTaskInstance: UserTaskInstance,
    formSchema: FormSchema,
    user: User,
    onSubmit?: (data: any) => void,
    successCallback?: (phase: string) => void,
    errorCallback?: (phase: string, errorMessage?: string) => void
  ) {
    this.userTaskInstance = userTaskInstance;
    this.formSchema = formSchema;
    this.onSubmit = onSubmit;
    this.taskformSubmit = new TaskFormSubmit(
      userTaskInstance,
      user,
      successCallback,
      errorCallback
    );

    this.assignments = readSchemaAssignments(this.formSchema);
  }

  public setSelectedPhase(phase: string): void {
    this.selectedPhase = phase;
  }

  getActions = (): FormAction[] => {
    if (this.userTaskInstance.completed || _.isEmpty(this.formSchema.phases)) {
      return [];
    }
    return this.formSchema.phases.map(phase => {
      return {
        name: phase,
        execute: () => {
          this.setSelectedPhase(phase);
        }
      };
    });
  };

  doSubmit = async (formData: any): Promise<void> => {
    if (_.isEmpty(this.formSchema.phases) || !this.selectedPhase) {
      throw new Error('Submit disabled for form');
    }

    const data = {};

    this.assignments.outputs.forEach(output => {
      if (formData[output]) {
        data[output] = formData[output];
      }
    });

    if (this.onSubmit) {
      this.onSubmit(data);
    }

    await this.taskformSubmit.submit(this.selectedPhase, data);
  };
}

function readSchemaAssignments(formSchema: FormSchema): FormAssignments {
  const assignments: FormAssignments = {
    inputs: [],
    outputs: []
  };

  if (!formSchema.properties) {
    return assignments;
  }

  for (const key of Object.keys(formSchema.properties)) {
    const property = formSchema.properties[key];
    if (property.input) {
      assignments.inputs.push(key);
      delete property.input;
    }
    if (property.output) {
      assignments.outputs.push(key);
      delete property.output;
    }

    if (!assignments.outputs.includes(key)) {
      if (!property.uniforms) {
        property.uniforms = {};
      }
      property.uniforms.disabled = true;
    }
  }

  return assignments;
}
