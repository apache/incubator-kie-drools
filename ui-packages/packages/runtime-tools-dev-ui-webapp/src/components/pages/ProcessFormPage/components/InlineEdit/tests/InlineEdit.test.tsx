import React from 'react';
import { mount } from 'enzyme';
import InlineEdit from '../InlineEdit';
import * as hooks from '../../../../../../channel/ProcessForm/ProcessFormContext';
import { ProcessFormGatewayApiImpl } from '../../../../../../channel/ProcessForm/ProcessFormGatewayApi';
import { Button, TextInput } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';

describe('inline edit tests', () => {
  beforeEach(() => {
    jest
      .spyOn(hooks, 'useProcessFormGatewayApi')
      .mockImplementation(() => new ProcessFormGatewayApiImpl());
  });
  it('snapshot', () => {
    const wrapper = mount(<InlineEdit />);
    expect(wrapper).toMatchSnapshot();
  });

  it('enter text and confirm', async () => {
    let wrapper = mount(<InlineEdit />);
    await act(async () => {
      wrapper
        .find(Button)
        .at(0)
        .simulate('click');
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find(TextInput).simulate('change', { target: { value: '222' } });
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper
        .find(Button)
        .at(1)
        .simulate('click');
    });
    wrapper = wrapper.update();
  });
});
