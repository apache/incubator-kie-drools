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

import cloneDeep from 'lodash/cloneDeep';
import get from 'lodash/get';
import merge from 'lodash/merge';
import unset from 'lodash/unset';
import set from 'lodash/set';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { JSONSchema7 } from 'json-schema';

export type TaskDataAssignments = {
  inputs: string[];
  outputs: string[];
};

export type ParsedTaskFormSchema = {
  schema: Record<string, any>;
  assignments: TaskDataAssignments;
};

export function parseTaskSchema(
  formSchema: Record<string, any>
): ParsedTaskFormSchema {
  const schema = cloneDeep(formSchema);

  const assignments: TaskDataAssignments = {
    inputs: [],
    outputs: []
  };

  if (!formSchema.properties) {
    return {
      schema,
      assignments
    };
  }

  for (const key of Object.keys(schema.properties)) {
    const property = schema.properties[key];

    const assignmetChecker = property['allOf']
      ? checkAssignmentForAllOf
      : checkAssignment;

    if (assignmetChecker(property, 'input')) {
      assignments.inputs.push(key);
    }
    if (assignmetChecker(property, 'output')) {
      assignments.outputs.push(key);
    }

    if (!assignments.outputs.includes(key)) {
      set(property, 'uniforms.disabled', true);
    }
  }

  return {
    schema,
    assignments
  };
}

function checkAssignmentForAllOf(
  property: any,
  assignmentExpr: string
): boolean {
  const allOf: any[] = property['allOf'];

  const assignment = allOf.find((value) => value[assignmentExpr]);
  if (assignment) {
    const index = allOf.indexOf(assignment);
    allOf.splice(index, 1);
    return true;
  }
  return false;
}

function checkAssignment(property: any, assignmentExpr: string): boolean {
  if (property[assignmentExpr]) {
    delete property[assignmentExpr];
    return true;
  }
  return false;
}

export type TaskFormSchema = JSONSchema7 & {
  phases?: string[];
};

export function readSchemaAssignments(
  formSchema: Record<string, any>,
  isUniforms = true
): TaskDataAssignments {
  const assignments: TaskDataAssignments = {
    inputs: [],
    outputs: []
  };

  if (!formSchema.properties) {
    return assignments;
  }

  for (const key of Object.keys(formSchema.properties)) {
    const property = formSchema.properties[key];
    if (get(property, 'input', false)) {
      assignments.inputs.push(key);
      if (isUniforms) {
        unset(property, 'input');
      }
    }
    if (get(property, 'output', false)) {
      assignments.outputs.push(key);
      if (isUniforms) {
        unset(property, 'output');
      }
    }

    if (isUniforms && !assignments.outputs.includes(key)) {
      const uniforms = get(property, 'uniforms', {});
      uniforms.disabled = true;
      set(property, 'uniforms', uniforms);
    }
  }

  return assignments;
}

function toJSON(value: string) {
  if (value) {
    try {
      return JSON.parse(value);
    } catch (e) {
      // do nothing
    }
  }
  return {};
}

export function generateFormData(userTask: UserTaskInstance): any {
  const taskInputs = toJSON(userTask.inputs);

  if (!userTask.outputs) {
    return taskInputs;
  }

  const taskOutputs = toJSON(userTask.outputs);

  return merge(taskInputs, taskOutputs);
}
