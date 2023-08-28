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
