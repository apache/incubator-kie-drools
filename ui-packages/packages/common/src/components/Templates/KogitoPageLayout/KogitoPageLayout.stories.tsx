import React from 'react';
import KogitoPageLayout from './KogitoPageLayout';
import { action } from '@storybook/addon-actions';
import { PageSection, Nav, NavList, NavItem } from '@patternfly/react-core';
import managementConsoleLogo from '../../../examples/managementConsoleLogo.svg';

export default {
  title: 'Page layout'
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
