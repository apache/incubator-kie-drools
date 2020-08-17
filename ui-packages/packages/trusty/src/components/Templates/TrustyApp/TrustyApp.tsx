import React from 'react';
import {
  NavLink,
  Redirect,
  Route,
  Switch,
  useHistory,
  useLocation
} from 'react-router-dom';
import { Nav, NavItem, NavList, PageSidebar } from '@patternfly/react-core';
import { KogitoPageLayout } from '@kogito-apps/common';
import AuditOverview from '../AuditOverview/AuditOverview';
import kogitoLogo from '../../../../static/images/kogitoLogo.svg';

const TrustyApp = () => {
  const location = useLocation();
  const history = useHistory();

  const PageNav = (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        <NavItem isActive={location.pathname.startsWith('/audit')}>
          <NavLink to="/audit">Audit Investigation</NavLink>
        </NavItem>
        <NavItem to="http://localhost:3001/" target="_blank">
          Business Monitoring
        </NavItem>
        <NavItem to="http://localhost:3001/" target="_blank">
          Operational Monitoring
        </NavItem>
      </NavList>
    </Nav>
  );

  const sidebar = <PageSidebar nav={PageNav} isNavOpen={true} theme="dark" />;

  const handleBrandClick = () => {
    history.push('/');
  };

  return (
    <KogitoPageLayout
      PageNav={sidebar}
      BrandSrc={kogitoLogo}
      BrandAltText="Kogito TrustyAI"
      BrandClick={handleBrandClick}
    >
      <Switch>
        <Route exact path="/">
          <Redirect to="/audit" />
        </Route>
        <Route exact path="/audit">
          <AuditOverview />
        </Route>
      </Switch>
    </KogitoPageLayout>
  );
};

export default TrustyApp;
