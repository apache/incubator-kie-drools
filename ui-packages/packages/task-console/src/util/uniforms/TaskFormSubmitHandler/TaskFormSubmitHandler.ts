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
import axios from 'axios';

import {
  IFormAction,
  IFormSubmitHandler
} from '../FormSubmitHandler/FormSubmitHandler';
import { FormSchema } from '../FormSchema';
import { GraphQL } from '@kogito-apps/common';
import UserTaskInstance = GraphQL.UserTaskInstance;

interface FormAssignments {
  inputs: string[];
  outputs: string[];
}

export class TaskFormSubmitHandler implements IFormSubmitHandler {
  private readonly userTaskInstance: UserTaskInstance;
  private readonly formSchema: FormSchema;
  private readonly successCallback?: (result: string) => void;
  private readonly errorCallback?: (
    errorMessage: string,
    error?: string
  ) => void;

  private readonly assignments;
  private readonly actions: IFormAction[] = [];

  private selectedPhase: string;

  constructor(
    userTaskInstance: UserTaskInstance,
    formSchema: FormSchema,
    successCallback?: (phase: string) => void,
    errorCallback?: (phase: string, errorMessage?: string) => void
  ) {
    this.userTaskInstance = userTaskInstance;
    this.formSchema = formSchema;
    this.successCallback = successCallback;
    this.errorCallback = errorCallback;

    this.assignments = readSchemaAssignments(this.formSchema);

    if (!userTaskInstance.completed && formSchema.phases) {
      this.actions = formSchema.phases.map(phase => {
        return {
          name: phase,
          execute: () => {
            this.setSelectedPhase(phase);
          }
        };
      });
    }
  }

  public setSelectedPhase(phase: string): void {
    this.selectedPhase = phase;
  }

  getActions = () => {
    return this.actions;
  };

  doSubmit = async (formData: any) => {
    if (this.actions.length === 0 || !this.selectedPhase) {
      throw new Error('Submit disabled for form');
    }

    try {
      const data = {};

      this.assignments.outputs.forEach(output => {
        if (formData[output]) {
          data[output] = formData[output];
        }
      });

      const endpoint = `${this.userTaskInstance.endpoint}?phase=${this.selectedPhase}`;

      const response = await axios.post(endpoint, data, {
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
          crossorigin: 'true',
          'Access-Control-Allow-Origin': '*'
        }
      });

      if (response.status === 200) {
        this.notifySuccess();
      } else {
        this.notifyError(response.data);
      }
    } catch (e) {
      const message = e.response ? e.response.data : e.message;
      this.notifyError(message);
    }
  };

  private notifySuccess() {
    if (this.successCallback) {
      this.successCallback(this.selectedPhase);
    }
  }

  private notifyError(errorMessage: string) {
    if (this.errorCallback) {
      this.errorCallback(this.selectedPhase, errorMessage);
    }
  }
}

function readSchemaAssignments(formSchema: FormSchema): FormAssignments {
  const formInputs = [];
  const formOutputs = [];

  for (const key of Object.keys(formSchema.properties)) {
    const property = formSchema.properties[key];
    if (property.hasOwnProperty('input')) {
      if (property.input) {
        formInputs.push(key);
      }
      delete property.input;
    }
    if (property.hasOwnProperty('output')) {
      if (property.output) {
        formOutputs.push(key);
      }
      delete property.output;
    }

    if (!formOutputs.includes(key)) {
      if (!property.uniforms) {
        property.uniforms = {};
      }
      property.uniforms.disabled = true;
    }
  }

  return {
    inputs: formInputs,
    outputs: formOutputs
  };
}
