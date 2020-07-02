import * as React from 'react';
import { actions } from '@storybook/addon-actions';
import AboutModelBox from './AboutModalBox';
import { aboutLogoContext } from '../../contexts';
import managementConsoleLogo from '../../../examples/managementConsoleLogo.svg';
import { withA11y } from '@storybook/addon-a11y';

const eventsFromObject = actions({
  onClick: 'clicked',
  onMouseOver: 'hovered'
});

export default {
  title: 'About modal box',
  component: AboutModelBox,
  decorators: [withA11y]
};

export const defaultView = () => (
  <aboutLogoContext.Provider value={managementConsoleLogo}>
    <AboutModelBox
      isOpenProp={true}
      handleModalToggleProp={() => null}
      {...eventsFromObject}
    />
  </aboutLogoContext.Provider>
);
