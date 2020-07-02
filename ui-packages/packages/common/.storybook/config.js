import { configure, addDecorator } from '@storybook/react';
const path = require('path');
import '@patternfly/patternfly/patternfly.css';
import {  addParameters } from '@storybook/react';
import StoryRouter from 'storybook-react-router';
import './storybook.css'

addDecorator(StoryRouter());
addParameters({
  backgrounds: [
    { name: 'White', value: '#fff' },
    { name: 'Blue', value: '#00aced' },
    { name: 'Black', value: '#000000' },
    { name: 'Grey', value: 'rgb(237, 237, 237, 237)', default: true}
  ],
});

configure(require.context('../src/components', true, /\.stories\.tsx$/), module);