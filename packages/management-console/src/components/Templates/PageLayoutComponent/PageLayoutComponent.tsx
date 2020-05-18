import { Nav, NavList, NavItem } from '@patternfly/react-core';
import React from 'react';
import { Redirect, Route, Link, Switch } from 'react-router-dom';
import {
  PageLayout,
  PageNotFound,
  NoData
} from '@kogito-apps/common/src/components';
import DataListContainer from '../DataListContainer/DataListContainer';
import ProcessDetailsPage from '../ProcessDetailsPage/ProcessDetailsPage';
import DomainExplorerDashboard from '../DomainExplorerDashboard/DomainExplorerDashboard';
import DomainExplorerLandingPage from '../DomainExplorerLandingPage/DomainExplorerLandingPage';
import './PageLayoutComponent.css';
import managementConsoleLogo from '../../../static/managementConsoleLogo.svg';

import { useGetQueryFieldsQuery } from '../../../graphql/types';

const PageLayoutComponent: React.FC<{}> = (props: any) => {
  const { pathname } = props.location;

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

  const BrandClick = () => {
    props.history.push('/ProcessInstances');
  };

  const getQuery = useGetQueryFieldsQuery();
  const availableDomains =
    !getQuery.loading && getQuery.data.__type.fields.slice(2);
  const domains = [];
  availableDomains && availableDomains.map(item => domains.push(item.name));
  return (
    <React.Fragment>
      <PageLayout
        PageNav={PageNav}
        BrandSrc={managementConsoleLogo}
        BrandAltText="Management Console Logo"
        BrandClick={BrandClick}
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
            path="/Process/:instanceID"
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
          <Route
            path="/NoData"
            render={_props => (
              <NoData
                {..._props}
                defaultPath="/ProcessInstances"
                defaultButton="Go to process instances"
              />
            )}
          />
          <Route
            path="*"
            render={_props => (
              <PageNotFound
                {..._props}
                defaultPath="/ProcessInstances"
                defaultButton="Go to process instances"
              />
            )}
          />
        </Switch>
      </PageLayout>
    </React.Fragment>
  );
};

export default PageLayoutComponent;
