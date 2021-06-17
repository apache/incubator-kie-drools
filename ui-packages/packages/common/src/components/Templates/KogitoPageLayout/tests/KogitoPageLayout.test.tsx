import React from 'react';
import KogitoPageLayout from '../KogitoPageLayout';
import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import * as Keycloak from '../../../../utils/KeycloakClient';
import { PageSidebar } from '@patternfly/react-core';

const props = {
  children: <React.Fragment>children rendered</React.Fragment>,
  BrandSrc: '../../../../static/kogito.png',
  PageNav: <React.Fragment>page Navigation elements</React.Fragment>,
  BrandAltText: 'Kogito logo',
  BrandClick: jest.fn()
};

jest.mock('../../../Molecules/PageToolbar/PageToolbar');

describe('KogitoPageLayout component tests', () => {
  const isAuthEnabledMock = jest.spyOn(Keycloak, 'isAuthEnabled');
  isAuthEnabledMock.mockReturnValue(false);
  it('snapshot tests', () => {
    const wrapper = mount(<KogitoPageLayout {...props} />).find(
      'KogitoPageLayout'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('open with PageSidebar closed', () => {
    const wrapper = mount(
      <KogitoPageLayout {...props} pageNavOpen={false} />
    ).find('KogitoPageLayout');
    expect(wrapper).toMatchSnapshot();

    let pageSidebar = wrapper.find(PageSidebar);
    expect(pageSidebar.exists()).toBeTruthy();
    expect(pageSidebar.props().isNavOpen).toBeFalsy();

    const event = {
      target: {}
    } as React.MouseEvent<HTMLInputElement>;
    act(() => {
      wrapper.find('Button').prop('onClick')(event);
    });

    const pageLayout = wrapper.update().find(KogitoPageLayout);
    expect(pageLayout).toMatchSnapshot();

    pageSidebar = pageLayout.find(PageSidebar);
    expect(pageSidebar.exists()).toBeTruthy();
    expect(pageSidebar.props().isNavOpen).toBeTruthy();
  });

  it('check isNavOpen boolean', () => {
    const wrapper = mount(<KogitoPageLayout {...props} />).find(
      'KogitoPageLayout'
    );
    const event = {
      target: {}
    } as React.MouseEvent<HTMLInputElement>;
    act(() => {
      wrapper.find('Button').prop('onClick')(event);
      wrapper.update();
    });
    expect(wrapper.find('PageSidebar').prop('isNavOpen')).toBeTruthy();
  });
});
