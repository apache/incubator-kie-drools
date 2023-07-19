import React from 'react';
import { mount } from 'enzyme';
import InlineEdit from '../InlineEdit';
import * as hooks from '../../../../../../channel/ProcessForm/ProcessFormContext';
import { ProcessFormGatewayApiImpl } from '../../../../../../channel/ProcessForm/ProcessFormGatewayApi';
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { TextInput } from '@patternfly/react-core/dist/js/components/TextInput';
import { act } from 'react-dom/test-utils';

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
    const wrapper = mount(<InlineEdit {...props} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('enter text and confirm', async () => {
    let wrapper = mount(<InlineEdit {...props} />);
    await act(async () => {
      wrapper.find(Button).at(0).simulate('click');
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find(TextInput).simulate('change', { target: { value: '222' } });
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find(Button).at(1).simulate('click');
    });
    wrapper = wrapper.update();
  });
});
