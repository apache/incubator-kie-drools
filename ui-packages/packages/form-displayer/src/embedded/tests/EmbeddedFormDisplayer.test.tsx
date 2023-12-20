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
import { EmbeddedFormDisplayer } from '../EmbeddedFormDisplayer';
import { mount } from 'enzyme';
import { FormType } from '@kogito-apps/components-common/dist';

describe('EmbeddedFormDisplayer tests', () => {
  it('Snapshot', () => {
    const props = {
      targetOrigin: 'origin',
      envelopePath: '/resources/form-displayer.html',
      formContent: {
        formInfo: {
          name: 'react_hiring_HRInterview',
          lastModified: new Date('2021-08-23T13:26:02.130Z'),
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
      }
    };

    const wrapper = mount(<EmbeddedFormDisplayer {...props} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.props().targetOrigin).toStrictEqual(props.targetOrigin);
    const contentIframe = wrapper.find('iframe');

    expect(contentIframe.exists()).toBeTruthy();
  });
});
