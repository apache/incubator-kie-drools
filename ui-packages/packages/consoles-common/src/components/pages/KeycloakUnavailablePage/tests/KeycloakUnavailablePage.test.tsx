import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { mount } from 'enzyme';
import React from 'react';
import { KeycloakUnavailablePage } from '../KeycloakUnavailablePage';

describe('KeycloakUnavailablePage test', () => {
  it('render the page', () => {
    const wrapper = mount(<KeycloakUnavailablePage />);
    expect(wrapper).toMatchSnapshot();
  });
  it('reload button is clicked', () => {
    const location: Location = window.location;
    delete window.location;
    window.location = {
      ...location,
      reload: jest.fn()
    };
    const wrapper = mount(<KeycloakUnavailablePage />);
    wrapper.find(Button).find('button').simulate('click');
    expect(window.location.reload).toHaveBeenCalled();
    jest.restoreAllMocks();
    window.location = location;
  });
});
