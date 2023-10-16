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
module.exports = {
  preset: 'ts-jest/presets/js-with-ts',
  setupFiles: [
    './config/Jest-config/test-shim.js',
    './config/Jest-config/test-setup.js',
    'core-js'
  ],
  coveragePathIgnorePatterns: [
    './src/static',
    './tests/mocks/',
    './src/components/contexts',
    './src/components/pages/index.ts',
    './src/channel/apis/index.ts',
    './src/channel/CloudEventForm/',
    './src/channel/CustomDashboardList/index.ts',
    './src/channel/CustomDashboardView/index.ts',
    './src/channel/FormDetails/index.ts',
    './src/channel/FormsList/index.ts',
    './src/channel/JobsManagement/index.ts',
    './src/channel/ProcessDefinitionList/index.ts',
    './src/channel/ProcessDetails/index.ts',
    './src/channel/ProcessForm/index.ts',
    './src/channel/ProcessList/index.ts',
    './src/channel/TaskForms/index.ts',
    './src/channel/TaskInbox/index.ts',
    './src/channel/WorkflowForm/index.ts'
  ],
  coverageReporters: [
    [
      'lcov',
      {
        projectRoot: '../../'
      }
    ]
  ],
  moduleFileExtensions: ['ts', 'tsx', 'js'],
  globals: {
    'ts-jest': {
      isolatedModules: true
    }
  },
  transformIgnorePatterns: [],
  transform: {
    '^.+.jsx?$': './config/Jest-config/babel-jest-wrapper.js',
    '^.+.(ts|tsx)$': 'ts-jest',
    '.(jpg|jpeg|png|svg)$': './config/Jest-config/fileMocks.js'
  },
  testMatch: ['**/tests/*.(ts|tsx)'],
  moduleNameMapper: {
    '\\.(scss|sass|css)$': 'identity-obj-proxy',
    'monaco-editor': '<rootDir>/__mocks__/monacoMock.js'
  }
};
