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

import Ajv, { ValidateFunction } from 'ajv';

/**
 * Defines a basic Form Validator
 *
 * @interface
 */
export interface FormValidator {
  /**
   * Validates the given model
   * @param model The model to validate
   * @returns If there are validation errors it should return an error
   * containing the errors
   */
  validate(model: any): any | undefined;
}

/**
 * Implementation of a validator using AJV
 */
export class DefaultFormValidator implements FormValidator {
  readonly schema: any;
  readonly validator: ValidateFunction;

  constructor(schema: any) {
    this.schema = schema;

    this.validator = new Ajv({ allErrors: true, useDefaults: true }).compile(
      schema
    );
  }

  validate(model: any): any | undefined {
    this.validator(model);

    if (this.validator.errors && this.validator.errors.length) {
      return { details: this.validator.errors };
    }
  }
}
