import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetailsProcessDiagram from '../ProcessDetailsProcessDiagram';

describe('ProcessDetailsDiagram component tests', () => {
  it('Snapshot testing', () => {
    const wrapper = shallow(<ProcessDetailsProcessDiagram />);
    expect(wrapper).toMatchSnapshot();
  });
});
