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
import { FormDetailsDriver } from '../../api';
import { FormDetailsChannelApiImpl } from '../FormDetailsChannelApiImpl';
import { MockedFormDetailsDriver } from './utils/Mocks';

let driver: FormDetailsDriver;
let api: FormDetailsChannelApiImpl;

describe('FormDetailsChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedFormDetailsDriver();
    api = new FormDetailsChannelApiImpl(driver);
  });

  it('FormDetails__getFormContent', () => {
    const formName = 'form1';
    api.formDetails__getFormContent(formName);
    expect(driver.getFormContent).toHaveBeenCalledWith(formName);
  });
});
