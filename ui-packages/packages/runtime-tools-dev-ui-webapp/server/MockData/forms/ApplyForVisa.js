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

module.exports = ApplyForVisaForm = {
  type: 'object',
  properties: {
    trip: {
      type: 'object',
      properties: {
        city: {
          type: 'string'
        },
        country: {
          type: 'string'
        },
        begin: {
          type: 'string',
          format: 'date-time'
        },
        end: {
          type: 'string',
          format: 'date-time'
        },
        visaRequired: {
          type: 'boolean'
        }
      },
      required: ['city'],
      input: true,
      output: false
    },
    traveller: {
      type: 'object',
      properties: {
        firstName: { type: 'string' },
        lastName: { type: 'string' },
        email: { type: 'string', format: 'email' },
        nationality: { type: 'string' },
        address: {
          type: 'object',
          properties: {
            street: {
              type: 'string'
            },
            city: {
              type: 'string'
            },
            zipCode: {
              type: 'string'
            },
            country: {
              type: 'string'
            }
          }
        }
      },
      input: true,
      output: false
    },
    visaApplication: {
      type: 'object',
      properties: {
        firstName: { type: 'string' },
        lastName: { type: 'string' },
        city: {
          type: 'string'
        },
        country: {
          type: 'string'
        },
        duration: {
          type: 'integer'
        },
        passportNumber: { type: 'string' },
        nationality: { type: 'string' }
      },
      input: true,
      output: true
    }
  },
  phases: ['complete', 'release']
};
