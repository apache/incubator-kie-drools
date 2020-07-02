import React from 'react';
import { Nav, NavList, NavItem } from '@patternfly/react-core';
import ServerUnavailable from './ServerUnavailable';
import managementConsoleLogo from '../../../examples/managementConsoleLogo.svg';

export default {
  title: 'Server unavailable',
  decorators: []
};

const PageNav = (
  <Nav aria-label="Nav" theme="dark">
    <NavList>
      <NavItem>Process Instances</NavItem>
      <NavItem>Domain Explorer</NavItem>
    </NavList>
  </Nav>
);

export const defaultView = () => (
  <div style={{ height: '100vh' }}>
    <ServerUnavailable PageNav={PageNav} src={managementConsoleLogo} />
  </div>
);
