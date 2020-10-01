import React from 'react';
import { shallow } from 'enzyme';
import PageToolbar from '../PageToolbar';
import * as Keycloak from '../../../../utils/KeycloakClient';

describe('PageToolbar component tests', () => {
  const getUserName = jest.spyOn(Keycloak, 'getUserName');
  const currentEnv = process.env;
  const isAuthEnabledMock = jest.spyOn(Keycloak, 'isAuthEnabled');

  getUserName.mockReturnValue('Ajay');
  afterEach(() => {
    process.env = currentEnv;
  });

  it('snapshot testing with kogito_auth_enabled param', () => {
    isAuthEnabledMock.mockReturnValue(true);
    const wrapper = shallow(<PageToolbar />);
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot testing with kogito-auth_enabled as null', () => {
    isAuthEnabledMock.mockReturnValue(undefined);
    const wrapper = shallow(<PageToolbar />);
    expect(wrapper).toMatchSnapshot();
  });

  it('onDropdownSelect test', () => {
    isAuthEnabledMock.mockReturnValue(true);

    const wrapper = shallow(<PageToolbar />);
    const event = {
      target: {}
    } as React.ChangeEvent<HTMLInputElement>;
    wrapper.find('Dropdown').prop('onSelect')(event);
  });
  /* tslint:disable */

  it('isDropDownToggle test', () => {
    isAuthEnabledMock.mockReturnValue(true);

    const wrapper = shallow(<PageToolbar />);
    wrapper
      .find('Dropdown')
      .prop('toggle')
      ['props']['onToggle']();
  });

  it('handleModalToggleProp test', () => {
    isAuthEnabledMock.mockReturnValue(true);

    const wrapper = shallow(<PageToolbar />);
    expect(wrapper.find('AboutModalBox').props()['isOpenProp']).toBeFalsy();
    wrapper
      .find('AboutModalBox')
      .props()
      ['handleModalToggleProp']();
    expect(wrapper.find('AboutModalBox').props()['isOpenProp']).toBeTruthy();
  });
});
