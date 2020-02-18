import React from 'react';
import { PageHeader } from '@patternfly/react-core';
import Avatar from '../../Atoms/AvatarComponent/AvatarComponent';
import PageToolbarComponent from '../PageToolbarComponent/PageToolbarComponent';
import BrandComponent from '../../Atoms/BrandComponent/BrandComponent';

const HeaderComponent: React.FC = () => {
  return (
    <PageHeader
      logo={<BrandComponent />}
      toolbar={<PageToolbarComponent />}
      avatar={<Avatar />}
    />
  );
};

export default HeaderComponent;
