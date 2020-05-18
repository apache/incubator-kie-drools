import { Page, PageSidebar, PageHeader, Avatar } from '@patternfly/react-core';
import React, { useState } from 'react';
import PageToolbar from '../../Molecules/PageToolbar/PageToolbar';
import BrandLogo from '../../Atoms/BrandLogo/BrandLogo';
import { aboutLogoContext } from '../../contexts';
import './PageLayout.css';

import userImage from '../../../static/avatar.svg';

interface IOwnProps {
  children: React.ReactNode;
  BrandSrc: string;
  PageNav: React.ReactNode;
  BrandAltText: string;
  BrandClick: () => void;
}

const PageLayout: React.FC<IOwnProps> = (props: any) => {
  const pageId = 'main-content-page-layout-default-nav';
  const [isNavOpen, setIsNavOpen] = useState(true);
  const onNavToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

  const Header = (
    <PageHeader
      logo={
        <BrandLogo
          src={props.BrandSrc}
          alt={props.BrandAltText}
          brandClick={props.BrandClick}
        />
      }
      toolbar={
        <aboutLogoContext.Provider value={props.BrandSrc}>
          <PageToolbar />
        </aboutLogoContext.Provider>
      }
      avatar={<Avatar src={userImage} alt="Kogito Logo" />}
      showNavToggle
      isNavOpen={isNavOpen}
      onNavToggle={onNavToggle}
    />
  );

  const Sidebar = (
    <PageSidebar nav={props.PageNav} isNavOpen={isNavOpen} theme="dark" />
  );

  return (
    <React.Fragment>
      <Page
        header={Header}
        mainContainerId={pageId}
        sidebar={Sidebar}
        className="kogito-common--PageLayout"
      >
        {props.children}
      </Page>
    </React.Fragment>
  );
};

export default PageLayout;
