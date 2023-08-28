require('jest-canvas-mock');
const { TextDecoder } = require('util');

global.TextDecoder = TextDecoder;
