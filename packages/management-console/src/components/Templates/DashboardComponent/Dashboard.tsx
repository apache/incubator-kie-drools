import {
  Page,
  SkipToContent,
  PageSidebar,
  PageHeader,
  Nav,
  NavList,
  NavItem
} from '@patternfly/react-core';
import React, { useState } from 'react';
import { Redirect, Route, Link, Switch } from 'react-router-dom';
import DataListContainer from '../DataListContainer/DataListContainer';
import ProcessDetailsPage from '../ProcessDetailsPage/ProcessDetailsPage';
import DomainExplorerDashboard from '../DomainExplorerDashboard/DomainExplorerDashboard';
import DomainExplorerLandingPage from '../DomainExplorerLandingPage/DomainExplorerLandingPage';
import Avatar from '../../Atoms/AvatarComponent/AvatarComponent';
import PageToolbarComponent from '../../Organisms/PageToolbarComponent/PageToolbarComponent';
import BrandComponent from '../../Atoms/BrandComponent/BrandComponent';
import ErrorComponent from '../../Molecules/ErrorComponent/ErrorComponent';
import NoDataComponent from '../../Molecules/NoDataComponent/NoDataComponent';
import './Dashboard.css';

import { useGetQueryFieldsQuery } from '../../../graphql/types';

const Dashboard: React.FC<{}> = (props: any) => {
  const pageId = 'main-content-page-layout-default-nav';
  const PageSkipToContent = (
    <SkipToContent href={`#${pageId}`}>Skip to Content</SkipToContent>
  );
  const [isNavOpen, setIsNavOpen] = useState(true);
  const { pathname } = props.location;

  const onNavToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

  const Header = (
    <PageHeader
      logo={<BrandComponent />}
      toolbar={<PageToolbarComponent />}
      avatar={<Avatar />}
      showNavToggle
      isNavOpen={isNavOpen}
      onNavToggle={onNavToggle}
    />
  );

  const PageNav = (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        <NavItem isActive={pathname === '/ProcessInstances'}>
          <Link to="/ProcessInstances">Process Instances</Link>
        </NavItem>
        <NavItem isActive={pathname === '/DomainExplorer'}>
          <Link to="/DomainExplorer">Domain Explorer</Link>
        </NavItem>
      </NavList>
    </Nav>
  );
  const Sidebar = (
    <PageSidebar nav={PageNav} isNavOpen={isNavOpen} theme="dark" />
  );

  const getQuery = useGetQueryFieldsQuery();
  const availableDomains =
    !getQuery.loading && getQuery.data.__type.fields.slice(2);
  const domains = [];
  availableDomains && availableDomains.map(item => domains.push(item.name));
  return (
    <React.Fragment>
      <Page
        header={Header}
        skipToContent={PageSkipToContent}
        mainContainerId={pageId}
        sidebar={Sidebar}
        isManagedSidebar
        className="kogito-management-console--dashboard-page"
      >
        <Switch>
          <Route
            exact
            path="/"
            render={() => <Redirect to="/ProcessInstances" />}
          />
          <Route exact path="/ProcessInstances" component={DataListContainer} />
          <Route
            exact
            path="/ProcessInstances/:instanceID"
            component={ProcessDetailsPage}
          />
          <Route
            exact
            path="/DomainExplorer"
            component={DomainExplorerLandingPage}
          />
          <Route
            exact
            path="/DomainExplorer/:domainName"
            render={_props => (
              <DomainExplorerDashboard {..._props} domains={domains} />
            )}
          />
          <Route path="/NoData" component={NoDataComponent} />
          <Route path="*" component={ErrorComponent} />
        </Switch>
      </Page>
    </React.Fragment>
  );
};

export default Dashboard;
