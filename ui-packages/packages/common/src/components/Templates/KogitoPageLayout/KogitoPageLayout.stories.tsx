import React from 'react';
import KogitoPageLayout from './KogitoPageLayout';
import { action } from '@storybook/addon-actions';
import { PageSection, Nav, NavList, NavItem } from '@patternfly/react-core';
import managementConsoleLogo from '../../../examples/managementConsoleLogo.svg';
import KogitoAppContext from '../../../environment/context/KogitoAppContext';
export default {
  title: 'Page layout',
  decorators: [
    (Story) => {
      return (
        <KogitoAppContext.Provider
          value={{
            getCurrentUser: () => {
              return {
                id: '12345',
                groups: ['Users']
              };
            },
            userContext: {
              getCurrentUser: () => {
                return {
                  id: '12345',
                  groups: ['Users']
                };
              }
            }
          }}
        >
          <Story />
        </KogitoAppContext.Provider>
      );
    }
  ]
};
export const defaultView = () => {
  const PageNav = (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        <NavItem onClick={action('button-click')}>Process Instances</NavItem>
        <NavItem onClick={action('button-click')}>Domain Explorer</NavItem>
      </NavList>
    </Nav>
  );
  return (
    <div style={{ height: '100vh' }}>
      <KogitoPageLayout
        PageNav={PageNav}
        BrandSrc={managementConsoleLogo}
        BrandAltText="Management Console Logo"
        BrandClick={action('button-click')}
      >
        <PageSection variant="light" />
      </KogitoPageLayout>
    </div>
  );
};
