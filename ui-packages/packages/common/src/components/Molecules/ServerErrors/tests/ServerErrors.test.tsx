import React from 'react';
import { mount } from 'enzyme';
import ServerErrors from '../ServerErrors';
import { BrowserRouter } from 'react-router-dom';
import { getWrapper } from '../../../../utils/OuiaUtils';

const mockGoBack = jest.fn();
const props = {
  error: 'some error',
  variant: 'large',
  history: {
    goBack: mockGoBack
  }
};

const props2 = {
  error: 'error occured',
  variant: 'small',
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
  it('snapshot testing with small variant ', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ServerErrors {...props2} />
      </BrowserRouter>,
      'ServerErrors'
    );
    expect(wrapper).toMatchSnapshot();
  });

  /* tslint:disable */
  it('display error button click with small variant ', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ServerErrors {...props2} />
      </BrowserRouter>
    );
    wrapper
      .find('#display-error')
      .first()
      .simulate('click');
    expect(wrapper.find('pre').props()['children']).toEqual('"error occured"');
  });
});
