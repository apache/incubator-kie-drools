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
import NotFound from '../NotFound/NotFound';
import ApplicationError from '../ApplicationError/ApplicationError';
import { TrustyContextValue } from '../../../types';
import './TrustyApp.scss';

type TrustyAppProps = {
  counterfactualEnabled: boolean;
  explanationEnabled: boolean;
};

const TrustyApp = (props: TrustyAppProps) => {
  const { counterfactualEnabled, explanationEnabled } = props;
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
        <NavItem
          isActive={location.pathname.startsWith('/audit')}
          ouiaId="audit-item"
        >
          <NavLink to="/audit">Audit investigation</NavLink>
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
      showNavToggle={isMobileView}
      onNavToggle={isMobileView ? onNavToggleMobile : onNavToggleDesktop}
      isNavOpen={isMobileView ? isNavOpenMobile : isNavOpenDesktop}
    />
  );

  return (
    <TrustyContext.Provider
      value={{ config: { counterfactualEnabled, explanationEnabled } }}
    >
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
          <Route exact path="/error">
            <ApplicationError />
          </Route>
          <Route path="/not-found" component={NotFound} />
          <Redirect to="/not-found" />
        </Switch>
      </Page>
    </TrustyContext.Provider>
  );
};

export default TrustyApp;

export const TrustyContext = React.createContext<TrustyContextValue>(null);
