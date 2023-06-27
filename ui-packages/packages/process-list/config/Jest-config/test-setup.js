const enzyme = require('enzyme');
const Adapter = require('@wojtekmaj/enzyme-adapter-react-17');
const { TextDecoder } = require('util');

global.TextDecoder = TextDecoder;
enzyme.configure({ adapter: new Adapter() });
