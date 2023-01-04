import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import ServerErrors from './ServerErrors';

export default {
  title: 'Server errors'
};

export const defaultView = (args) => {
  return (
    <BrowserRouter>
      <ServerErrors {...args} />
    </BrowserRouter>
  );
};

defaultView.args = {
  'Error message':
    'Network error: Response not successful: Received status code 400',
  variant: 'large'
};
