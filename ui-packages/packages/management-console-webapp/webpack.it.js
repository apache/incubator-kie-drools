const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.dev.js');
const webpack = require('webpack');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = merge(common(), {
  output: {
    path: path.resolve(__dirname, 'dist-it')
  },
  module: {
    rules: [
      {
        test: /\.(css|sass|scss)$/,
        include: [
          path.resolve(
            '../../node_modules/@kie-tools-core/editor/dist/envelope'
          )
        ],
        use: ['style-loader', 'css-loader', 'sass-loader']
      }
    ]
  }
});
