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
import isPlainObject from 'lodash/isPlainObject';
import { CloudEventRequest } from '../../../api';

export interface FormValidations {
  isValid(): boolean;

  getFieldValidation(fieldId: string): undefined | string;
}

class FormValidationsImpl implements FormValidations {
  constructor(private readonly validations: Record<string, string>) {}

  getFieldValidation(fieldId: string): string | undefined {
    return this.validations[fieldId];
  }

  isValid(): boolean {
    return Object.keys(this.validations).length == 0;
  }
}

export function validateCloudEventRequest(
  eventRequest: CloudEventRequest
): FormValidations {
  const validations = {};

  if (!eventRequest.endpoint) {
    validations['endpoint'] = 'The Cloud Event endpoint cannot be empty.';
  }

  if (!eventRequest.headers.type) {
    validations['eventType'] = 'The Cloud Event type cannot be empty.';
  }

  if (eventRequest.data) {
    try {
      const json = JSON.parse(eventRequest.data);
      if (!isPlainObject(json)) {
        throw new Error('not an object');
      }
    } catch (err) {
      validations['eventData'] =
        'The Cloud Event data should have a JSON format.';
    }
  }

  return new FormValidationsImpl(validations);
}
