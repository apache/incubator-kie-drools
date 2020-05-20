import React from 'react';
import {
  Nav,
  NavList,
  NavItem
} from '@patternfly/react-core';
import { PageLayout } from '@kogito-apps/common/src/components';
import { Redirect, Route, Link, Switch } from 'react-router-dom';
import taskConsoleLogo from '../../../static/taskConsoleLogo.svg';
import DataListContainerExpandable from "../DataListContainerExpandable/DataListContainerExpandable";
import DataListContainer from "../DataListContainer/DataListContainer";

const PageLayoutComponent = props => {
  const { pathname } = props.location;

  const PageNav = (
    <Nav aria-label="Nav" theme="dark" css="">
      <NavList>
        <NavItem isActive={pathname === '/UserTasks'}>
          <Link to="/UserTasks">User Tasks</Link>
        </NavItem>
        <NavItem isActive={pathname === '/UserTasksFilters'}>
          <Link to="/UserTasksFilters">User tasks with filters</Link>
        </NavItem>
      </NavList>
    </Nav>
  );

  const BrandClick = () => {
    props.history.push('/');
  };

  return (
    <PageLayout
      PageNav={PageNav}
      BrandSrc={taskConsoleLogo}
      BrandAltText="Task Console Logo"
      BrandClick={BrandClick}
    >

      <Switch>
        <Route
          exact
          path="/"
          render={() => <Redirect to="/UserTasks" />}
        />
        <Route exact path="/UserTasks" component={DataListContainerExpandable} />
        <Route exact path="/UserTasksFilters" component={DataListContainer} />
      </Switch>
    </PageLayout>
  );
};

export default PageLayoutComponent;
