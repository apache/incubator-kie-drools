const enzyme = require("enzyme");
const Adapter = require("enzyme-adapter-react-16");
const { TextDecoder } = require('util');

global.TextDecoder = TextDecoder;
enzyme.configure({ adapter: new Adapter() });