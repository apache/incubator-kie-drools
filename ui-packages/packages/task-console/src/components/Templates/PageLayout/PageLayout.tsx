import React from 'react';
import { Nav, NavList, NavItem, OUIAProps } from '@patternfly/react-core';
import { KogitoPageLayout } from '@kogito-apps/common';
import { ouiaAttribute } from '@kogito-apps/ouia-tools';
import { Redirect, Route, Link, Switch } from 'react-router-dom';
import taskConsoleLogo from '../../../static/taskConsoleLogo.svg';

import UserTaskInstanceDetailsPage from '../UserTaskInstanceDetailsPage/UserTaskInstanceDetailsPage';
import UserTaskDataTableContainer from '../UserTaskDataTableContainer/UserTaskDataTableContainer';
import { Location, History } from 'history';
import TaskInboxContainer from '../TaskInboxContainer/TaskInboxContainer';

interface IOwnProps {
  location: Location;
  history: History;
}
const PageLayout: React.FC<IOwnProps & OUIAProps> = ({ ouiaId, ...props }) => {
  const { pathname } = props.location;

  const PageNav = (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        <NavItem isActive={pathname === '/TaskInbox'}>
          <Link
            to="/TaskInbox"
            {...ouiaAttribute('data-ouia-navigation-name', 'task-inbox')}
          >
            Task Inbox
          </Link>
        </NavItem>
      </NavList>
    </Nav>
  );

  const BrandClick = () => {
    props.history.push('/');
  };

  return (
    <KogitoPageLayout
      PageNav={PageNav}
      pageNavOpen={false}
      BrandSrc={taskConsoleLogo}
      BrandAltText="Task Console Logo"
      BrandClick={BrandClick}
    >
      <Switch>
        <Route exact path="/" render={() => <Redirect to="/TaskInbox" />} />
        <Route exact path="/TaskInbox" component={TaskInboxContainer} />
        <Route
          exact
          path="/TaskDetails/:taskId"
          render={routeProps => <UserTaskInstanceDetailsPage {...routeProps} />}
        />
        <Route
          exact
          path="/UserTasksTable"
          component={UserTaskDataTableContainer}
        />
      </Switch>
    </KogitoPageLayout>
  );
};

export default PageLayout;
