import React from 'react';
import { shallow } from 'enzyme';
import ProcessDetailsProcessDiagram from '../ProcessDetailsProcessDiagram';


describe('Process Details Diagram component', () => {
    it('Snapshot tests', () => {
        const wrapper = shallow(<ProcessDetailsProcessDiagram />);
        expect(wrapper).toMatchSnapshot();
      });
})

