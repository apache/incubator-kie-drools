import React from 'react';
import { shallow } from 'enzyme';
import KogitoSpinner from '../KogitoSpinner';

describe('KogitoSpinner component tests', () => {
  it('snapshot testing with loading test', () => {
    const wrapper = shallow(
      <KogitoSpinner
        spinnerText={'loading...'}
        ouiaId="kogito-spinner-ouia-id"
      />
    );
    expect(wrapper).toMatchSnapshot();
  });
});
