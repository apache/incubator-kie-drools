import React, { useState } from 'react';
import {
  NavLink,
  Redirect,
  Route,
  Switch,
  useLocation
} from 'react-router-dom';
import {
  Avatar,
  Brand,
  Nav,
  NavItem,
  NavList,
  Page,
  PageHeader,
  PageHeaderTools,
  PageSidebar
} from '@patternfly/react-core';
import AuditOverview from '../AuditOverview/AuditOverview';
import kogitoLogo from '../../../../static/images/kogitoLogo.svg';
import AuditDetail from '../AuditDetail/AuditDetail';
import imgAvatar from '../../../../static/images/user.svg';
import Breadcrumbs from '../../Organisms/Breadcrumbs/Breadcrumbs';
import './TrustyApp.scss';

const TrustyApp = () => {
  const location = useLocation();
  const [isMobileView, setIsMobileView] = useState(false);
  const [isNavOpenDesktop, setIsNavOpenDesktop] = useState(true);
  const [isNavOpenMobile, setIsNavOpenMobile] = useState(false);

  const onNavToggleDesktop = () => {
    setIsNavOpenDesktop(!isNavOpenDesktop);
  };

  const onNavToggleMobile = () => {
    setIsNavOpenMobile(!isNavOpenMobile);
  };

  const handlePageResize = (props: {
    windowSize: number;
    mobileView: boolean;
  }) => {
    // closing sidebar menu when resolution is < 1200
    if (props.windowSize < 1200) {
      if (!isMobileView) setIsMobileView(true);
    } else {
      if (isMobileView) setIsMobileView(false);
    }
  };

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

  const Sidebar = (
    <PageSidebar
      nav={PageNav}
      isNavOpen={isMobileView ? isNavOpenMobile : isNavOpenDesktop}
      theme="dark"
    />
  );

  const Header = (
    <PageHeader
      logo={
        <Brand src={kogitoLogo} alt="Kogito TrustyAI" className="trusty-logo" />
      }
      logoProps={{ href: '#/' }}
      headerTools={
        <PageHeaderTools>
          <Avatar src={imgAvatar} alt="Avatar image" />
        </PageHeaderTools>
      }
      showNavToggle
      onNavToggle={isMobileView ? onNavToggleMobile : onNavToggleDesktop}
      isNavOpen={isMobileView ? isNavOpenMobile : isNavOpenDesktop}
    />
  );

  return (
    <Page
      header={Header}
      sidebar={Sidebar}
      breadcrumb={<Breadcrumbs />}
      onPageResize={handlePageResize}
    >
      <Switch>
        <Route exact path="/">
          <Redirect to="/audit" />
        </Route>
        <Route exact path="/audit">
          <AuditOverview />
        </Route>
        <Route path="/audit/:executionType/:executionId">
          <AuditDetail />
        </Route>
      </Switch>
    </Page>
  );
};

export default TrustyApp;
