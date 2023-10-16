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
import React from 'react';
import { act } from 'react-dom/test-utils';
import { mount } from 'enzyme';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import FormDisplayer from '../components/FormDisplayer/FormDisplayer';
import {
  FormDisplayerEnvelopeView,
  FormDisplayerEnvelopeViewApi
} from '../FormDisplayerEnvelopeView';
import { FormType } from '../../api';
import ErrorBoundary from '../components/ErrorBoundary/ErrorBoundary';

jest.mock('../components/FormDisplayer/FormDisplayer');

describe('FormDisplayerEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<FormDisplayerEnvelopeViewApi>();

    let wrapper = mount(
      <FormDisplayerEnvelopeView channelApi={channelApi} ref={forwardRef} />
    ).find('FormDisplayerEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    const formContent = {
      formInfo: {
        lastModified: new Date('2021-08-23T13:26:02.130Z'),
        name: 'react_hiring_HRInterview',
        type: FormType.TSX
      },
      configuration: {
        resources: {
          scripts: {},
          styles: {}
        },
        schema: 'json schema'
      },
      source: 'react source code'
    };

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initForm({
          form: formContent
        });
      }
    });

    wrapper = wrapper.update();

    const envelopeView = wrapper.find(FormDisplayerEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const boundary = wrapper.find(ErrorBoundary);
    expect(boundary.exists()).toBeTruthy();

    const formDisplayer = envelopeView.find(FormDisplayer);

    expect(formDisplayer.exists()).toBeTruthy();
    expect(formDisplayer.props().isEnvelopeConnectedToChannel).toBeTruthy();
  });
});
