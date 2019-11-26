import React from 'react';
import { shallow } from 'enzyme';
import TitleComponent from '../TitleComponent';

describe('Title Component Test cases', () => {
    it('testing snapshot', () => {
        const wrapper = shallow(<TitleComponent />)
        expect(wrapper).toMatchSnapshot();
    });
});
