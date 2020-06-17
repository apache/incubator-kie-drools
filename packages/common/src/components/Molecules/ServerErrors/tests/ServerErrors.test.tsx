import React from 'react';
import { mount } from 'enzyme';
import ServerErrors from '../ServerErrors';
import { BrowserRouter } from 'react-router-dom';
import { getWrapper } from '../../../../utils/OuiaUtils';

const mockGoBack = jest.fn();
const props = {
  error: 'some error',
  history: {
    goBack: mockGoBack
  }
};

describe('ServerErrors component tests', () => {
  it('snapshot testing ', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ServerErrors {...props} />
      </BrowserRouter>,
      'ServerErrors'
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('goback button click ', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ServerErrors {...props} />
      </BrowserRouter>,
      'ServerErrors'
    );
    wrapper
      .find('#goback-button')
      .first()
      .simulate('click');
    expect(window.location.pathname).toEqual('/');
  });

  /* tslint:disable */
  it('display error button click ', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ServerErrors {...props} />
      </BrowserRouter>
    );
    wrapper
      .find('#display-error')
      .first()
      .simulate('click');
    wrapper.update();
    expect(
      wrapper
        .find('#content-0')
        .find('pre')
        .props()['children']
    ).toEqual('"some error"');
  });
});
