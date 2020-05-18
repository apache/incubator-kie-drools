import React from 'react';
import {
  Nav,
  NavList,
  NavItem,
  PageSection,
  Title
} from '@patternfly/react-core';
import { PageLayout } from '@kogito-apps/common/src/components';
import { withRouter } from 'react-router-dom';
import taskConsoleLogo from '../../../static/taskConsoleLogo.svg';

const PageLayoutComponent = props => {
  const PageNav = (
    <Nav aria-label="Nav" theme="dark" css="">
      <NavList>
        <NavItem>TestOption-1</NavItem>
        <NavItem isActive>TestOption-2</NavItem>
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
      <PageSection variant="light">
        <Title headingLevel="h1" size="4xl">
          Task List
        </Title>
      </PageSection>{' '}
      {''}
    </PageLayout>
  );
};

export default withRouter(PageLayoutComponent);
