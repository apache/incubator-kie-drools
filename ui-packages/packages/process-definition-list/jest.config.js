/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module.exports = {
  setupFiles: [
    './config/Jest-config/test-shim.js',
    './config/Jest-config/test-setup.js',
    'core-js'
  ],
  moduleFileExtensions: ['ts', 'tsx', 'js'],
  coveragePathIgnorePatterns: [
    './src/static',
    'dist/',
    './src/envelope/index.ts',
    './src/embedded/tests/utils/Mocks.ts',
    './src/envelope/tests/mocks',
    './src/envelope/ProcessDefinitionListEnvelope.tsx'
  ],
  coverageReporters: [
    [
      'lcov',
      {
        projectRoot: '../../'
      }
    ]
  ],
  snapshotSerializers: ['enzyme-to-json/serializer'],
  transformIgnorePatterns: [],
  transform: {
    '^.+.jsx?$': './config/Jest-config/babel-jest-wrapper.js',
    '^.+.(ts|tsx)$': 'ts-jest',
    '.(jpg|jpeg|png|svg)$': './config/Jest-config/fileMocks.js'
  },
  testMatch: ['**/tests/*.(ts|tsx|js)'],
  moduleNameMapper: {
    '.(scss|sass|css)$': 'identity-obj-proxy'
  }
};
