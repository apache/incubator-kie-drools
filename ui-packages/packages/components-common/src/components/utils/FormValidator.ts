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
import Ajv, { ValidateFunction } from 'ajv';
import { SCHEMA_VERSION } from '../../types';
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

export function lookupValidator(schema: any): FormValidator {
  if (schema.$schema === SCHEMA_VERSION.DRAFT_7) {
    return new Draft7FormValidator(schema);
  }

  if (schema.$schema === SCHEMA_VERSION.DRAFT_2019_09) {
    // TODO: upgrade AJV version to get the DRAFT_2019_09 validator
    return new Draft2019_09Validator();
  }

  console.warn(`Cannot load validator for schema version '${schema.$schema}'.`);
  return {
    validate(model: any): any {
      // Do nothing
    }
  };
}

/**
 * Implementation of a validator using AJV for DRAFT_7
 */
export class Draft7FormValidator implements FormValidator {
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

export class Draft2019_09Validator implements FormValidator {
  validate(model: any): any {
    // TODO: upgrade AJV version to get the DRAFT_2019_09 validator
  }
}
