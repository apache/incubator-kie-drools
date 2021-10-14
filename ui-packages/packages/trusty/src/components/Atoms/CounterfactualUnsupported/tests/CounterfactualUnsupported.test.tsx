import React from 'react';
import { shallow } from 'enzyme';
import CounterfactualUnsupported from '../CounterfactualUnsupported';
import { CFSupportMessage } from '../../../../types';

describe('CounterfactualUnsupported status', () => {
  test('renders messages', () => {
    const messages: CFSupportMessage[] = [
      { id: 'msg1', message: 'message1' },
      { id: 'msg2', message: 'message2' }
    ];

    const wrapper = shallow(<CounterfactualUnsupported messages={messages} />);

    expect(wrapper).toMatchSnapshot();

    const items = wrapper.find('EmptyStateBody p');
    expect(items.length).toBe(4);
    expect(items.get(0)).not.toBeUndefined();
    expect(items.get(1).props['data-ouia-component-id']).toBe('msg1');
    expect(items.get(2).props['data-ouia-component-id']).toBe('msg2');
    expect(items.get(3)).not.toBeUndefined();
  });
});
