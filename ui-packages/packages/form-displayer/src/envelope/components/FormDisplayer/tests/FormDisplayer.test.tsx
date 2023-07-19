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

import React from 'react';
import { mount } from 'enzyme';
import FormDisplayer from '../FormDisplayer';
import { FormType, Form } from '../../../../api';
import HtmlFormRenderer from '../../HtmlFormRenderer/HtmlFormRenderer';
import ReactFormRenderer from '../../ReactFormRenderer/ReactFormRenderer';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core/dist/js/layouts/Bullseye', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Bullseye: () => <MockedComponent />
  })
);

jest.mock('../../ReactFormRenderer/ReactFormRenderer');
jest.mock('../../HtmlFormRenderer/HtmlFormRenderer');

describe('FormDisplayer component tests', () => {
  it('Snapshot test with default props - TSX renderer', () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      content: {
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
      },
      onOpenForm: jest.fn(),
      data: {},
      context: {}
    };

    const wrapper = mount(<FormDisplayer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const react = wrapper.find(ReactFormRenderer);
    expect(react.exists()).toBeTruthy();
  });

  it('Snapshot test with default props - HTML renderer', () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      content: {
        formInfo: {
          lastModified: new Date('2021-08-23T13:26:02.130Z'),
          name: 'html_hiring_HRInterview',
          type: FormType.HTML
        },
        configuration: {
          resources: {
            scripts: {
              'bootstrap.min.js':
                'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js',
              'jquery.js': 'https://code.jquery.com/jquery-3.2.1.slim.min.js',
              'popper.js':
                'https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js'
            },
            styles: {
              'bootstrap.min.css':
                'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css'
            }
          },
          schema: 'json schema'
        },
        source: 'html source code'
      },
      onOpenForm: jest.fn(),
      data: {},
      context: {}
    };
    const wrapper = mount(<FormDisplayer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const html = wrapper.find(HtmlFormRenderer);
    expect(html.exists()).toBeTruthy();
  });

  it('Test beats spinner', () => {
    const props = {
      isEnvelopeConnectedToChannel: false,
      content: {} as Form,
      onOpenForm: jest.fn(),
      data: {},
      context: {}
    };
    const wrapper = mount(<FormDisplayer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const bullseye = wrapper.find(Bullseye);
    expect(bullseye.exists()).toBeTruthy();
  });
});
