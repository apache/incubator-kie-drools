import {
  Page,
  PageSidebar,
  PageHeader,
  Avatar,
  InjectedOuiaProps,
  withOuiaContext
} from '@patternfly/react-core';
import React, { useState, useEffect } from 'react';
import PageToolbar from '../../Molecules/PageToolbar/PageToolbar';
import BrandLogo from '../../Atoms/BrandLogo/BrandLogo';
import { aboutLogoContext } from '../../contexts';
import './KogitoPageLayout.css';

import userImage from '../../../static/avatar.svg';
import { ouiaAttribute } from '../../../utils/OuiaUtils';

interface IOwnProps {
  children: React.ReactNode;
  BrandSrc: string;
  PageNav: React.ReactNode;
  BrandAltText: string;
  BrandClick: () => void;
}

const KogitoPageLayout: React.FC<IOwnProps & InjectedOuiaProps> = ({
  ouiaContext,
  ...props
}) => {
  const pageId = 'main-content-page-layout-default-nav';
  const [isNavOpen, setIsNavOpen] = useState(true);
  const onNavToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

  useEffect(() => {
    // set OUIA:Page attribute
    ouiaContext.isOuia &&
      document.getElementById(pageId).setAttribute('data-ouia-main', 'true');
  });

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
      {...ouiaAttribute(ouiaContext, 'data-ouia-header', 'true')}
    />
  );

  const Sidebar = (
    <PageSidebar
      nav={props.PageNav}
      isNavOpen={isNavOpen}
      theme="dark"
      {...ouiaAttribute(ouiaContext, 'data-ouia-navigation', 'true')}
    />
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

export default withOuiaContext(KogitoPageLayout);
