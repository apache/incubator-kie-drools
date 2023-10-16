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
import { fireEvent, render, screen } from '@testing-library/react';
import InlineEdit from '../InlineEdit';
import * as hooks from '../../../../../../channel/ProcessForm/ProcessFormContext';
import { ProcessFormGatewayApiImpl } from '../../../../../../channel/ProcessForm/ProcessFormGatewayApi';

const props = {
  getBusinessKey: () => '',
  setBusinessKey: jest.fn()
};

describe('inline edit tests', () => {
  beforeEach(() => {
    jest
      .spyOn(hooks, 'useProcessFormGatewayApi')
      .mockImplementation(() => new ProcessFormGatewayApiImpl());
  });
  it('snapshot', () => {
    const wrapper = render(<InlineEdit {...props} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('enter text and confirm', async () => {
    const container = render(<InlineEdit {...props} />).container;

    fireEvent.change(screen.getByPlaceholderText('Enter business key'), {
      target: { value: 'new value' }
    });
    const buttons = container.querySelectorAll('button');
    fireEvent.click(buttons[1]);
    expect(props.setBusinessKey).toHaveBeenCalled();
    fireEvent.click(buttons[2]);
    expect(props.setBusinessKey).toHaveBeenCalled();
  });
});
