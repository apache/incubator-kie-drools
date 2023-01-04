import React from 'react';
import { shallow } from 'enzyme';
import ServerUnavailable from '../ServerUnavailable';
import { Button } from '@patternfly/react-core';

const props = {
  src: '.../../../../static/logo.png',
  alt: 'Logo alt text',
  pageNav: (
    <React.Component>
      <Button>something</Button>
    </React.Component>
  )
};

describe('ServerUnavailable component tests', () => {
  const location: Location = window.location;
  beforeEach(() => {
    delete window.location;
    window.location = {
      ...location,
      reload: jest.fn()
    };
  });
  afterEach(() => {
    jest.restoreAllMocks();
    window.location = location;
  });
  it('snapshot testing ', () => {
    const wrapper = shallow(<ServerUnavailable {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  /* tslint:disable */
  it('reload button click ', () => {
    const wrapper = shallow(<ServerUnavailable {...props} />);
    wrapper.find(Button).simulate('click');
    expect(window.location.reload).toHaveBeenCalledTimes(1);
  });
  it('onNav toggle test', () => {
    const wrapper = shallow(<ServerUnavailable {...props} />);
    wrapper.find('Page').props()['header']['props']['onNavToggle']();
    expect(
      wrapper.find('Page').props()['header']['props']['isNavOpen']
    ).toBeFalsy();
  });
});
