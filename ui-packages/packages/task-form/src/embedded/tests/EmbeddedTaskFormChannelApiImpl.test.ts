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
import { TaskFormDriver } from '../../api';
import { EmbeddedTaskFormChannelApiImpl } from '../EmbeddedTaskFormChannelApiImpl';
import { MockedTaskFormDriver } from './mocks/Mocks';

let driver: TaskFormDriver;
let api: EmbeddedTaskFormChannelApiImpl;

describe('EmbeddedTaskFormChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedTaskFormDriver();
    api = new EmbeddedTaskFormChannelApiImpl(driver);
  });

  it('taskForm__getTaskFormSchema', () => {
    api.taskForm__getTaskFormSchema();

    expect(driver.getTaskFormSchema).toHaveBeenCalled();
  });

  it('taskForm__getCustomForm', () => {
    api.taskForm__getCustomForm();

    expect(driver.getCustomForm).toHaveBeenCalled();
  });

  it('taskForm__doSubmit', () => {
    api.taskForm__doSubmit('complete', {});

    expect(driver.doSubmit).toHaveBeenCalledWith('complete', {});
  });
});
