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
import { Redirect, Route, Link } from 'react-router-dom';
import DataListContainer from '../DataListContainer/DataListContainer';
import ProcessDetailsPage from '../ProcessDetailsPage/ProcessDetailsPage';
import DomainExplorerPage from '../DomainExplorerPage/DomainExplorerPage';
import Avatar from '../../Atoms/AvatarComponent/AvatarComponent';
import PageToolbarComponent from '../../Organisms/PageToolbarComponent/PageToolbarComponent';
import BrandComponent from '../../Atoms/BrandComponent/BrandComponent';
import './Dashboard.css';

const Dashboard: React.FC<{}> = (props: any) => {
  const pageId = 'main-content-page-layout-default-nav';
  const PageSkipToContent = (
    <SkipToContent href={`#${pageId}`}>Skip to Content</SkipToContent>
  );
  const [isNavOpen, setIsNavOpen] = useState(false);
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
  return (
    <React.Fragment>
      <Page
        header={Header}
        skipToContent={PageSkipToContent}
        mainContainerId={pageId}
        sidebar={Sidebar}
        className="kogito-management-console--dashboard-page"
      >
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
        <Route exact path="/DomainExplorer" component={DomainExplorerPage} />
      </Page>
    </React.Fragment>
  );
};

export default Dashboard;
