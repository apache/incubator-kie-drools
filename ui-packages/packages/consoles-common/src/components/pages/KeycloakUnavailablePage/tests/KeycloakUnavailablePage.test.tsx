import { Button } from '@patternfly/react-core';
import { mount } from 'enzyme';
import React from 'react';
import KeyCloakUnavailablePage from '../KeycloakUnavailablePage';

describe('KeycloakUnavailablePage test', () => {
  it('render the page', () => {
    const wrapper = mount(<KeyCloakUnavailablePage />);
    expect(wrapper).toMatchSnapshot();
  });
  it('reload button is clicked', () => {
    const location: Location = window.location;
    delete window.location;
    window.location = {
      ...location,
      reload: jest.fn()
    };
    const wrapper = mount(<KeyCloakUnavailablePage />);
    wrapper.find(Button).find('button').simulate('click');
    expect(window.location.reload).toHaveBeenCalled();
    jest.restoreAllMocks();
    window.location = location;
  });
});
