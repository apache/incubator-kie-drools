import { Nav, NavList, NavItem } from '@patternfly/react-core';
import React from 'react';
import { Redirect, Route, Link, Switch } from 'react-router-dom';
import {
  KogitoPageLayout,
  PageNotFound,
  NoData,
  GraphQL
} from '@kogito-apps/common';
import { ouiaAttribute, OUIAProps } from '@kogito-apps/ouia-tools';
import ProcessListPage from '../ProcessListPage/ProcessListPage';
import ProcessDetailsPage from '../ProcessDetailsPage/ProcessDetailsPage';
import DomainExplorerPage from '../DomainExplorerPage/DomainExplorerPage';
import DomainExplorerLandingPage from '../DomainExplorerLandingPage/DomainExplorerLandingPage';
import JobsManagementPage from '../JobsManagementPage/JobsManagementPage';
import './PageLayout.css';
import managementConsoleLogo from '../../../static/managementConsoleLogo.svg';
import { History, Location } from 'history';

interface IOwnProps {
  location: Location;
  history: History;
}

const PageLayout: React.FC<IOwnProps & OUIAProps> = ({ ...props }) => {
  const { pathname } = props.location;

  const PageNav = (
    <Nav aria-label="Nav" theme="dark" ouiaId="navigation-list">
      <NavList>
        <NavItem isActive={pathname === '/ProcessInstances'}>
          <Link
            to="/ProcessInstances"
            {...ouiaAttribute('data-ouia-navigation-name', 'process-instances')}
          >
            Process Instances
          </Link>
        </NavItem>
        <NavItem isActive={pathname === '/DomainExplorer'}>
          <Link
            to="/DomainExplorer"
            {...ouiaAttribute('data-ouia-navigation-name', 'domain-explorer')}
          >
            Domain Explorer
          </Link>
        </NavItem>
        <NavItem isActive={pathname === '/JobsManagement'}>
          <Link
            to="/JobsManagement"
            {...ouiaAttribute('data-ouia-navigation-name', 'jobs-management')}
          >
            Jobs
          </Link>
        </NavItem>
      </NavList>
    </Nav>
  );

  const BrandClick = () => {
    props.history.push('/ProcessInstances');
  };
  const getQuery = GraphQL.useGetQueryFieldsQuery();
  const availableDomains =
    !getQuery.loading && getQuery.data && getQuery.data.__type.fields.slice(2);
  const domains = [];
  availableDomains && availableDomains.map((item) => domains.push(item.name));
  return (
    <React.Fragment>
      <KogitoPageLayout
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
          <Route exact path="/ProcessInstances" component={ProcessListPage} />
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
            render={(_props) => (
              <DomainExplorerPage
                {..._props}
                domains={domains}
                loadingState={getQuery.loading}
              />
            )}
          />
          <Route exact path="/JobsManagement" component={JobsManagementPage} />
          <Route
            path="/NoData"
            render={(_props) => (
              <NoData
                {..._props}
                defaultPath="/ProcessInstances"
                defaultButton="Go to process instances"
              />
            )}
          />
          <Route
            path="*"
            render={(_props) => (
              <PageNotFound
                {..._props}
                defaultPath="/ProcessInstances"
                defaultButton="Go to process instances"
              />
            )}
          />
        </Switch>
      </KogitoPageLayout>
    </React.Fragment>
  );
};

export default PageLayout;
