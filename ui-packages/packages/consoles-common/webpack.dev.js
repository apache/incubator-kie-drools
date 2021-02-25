const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = merge(common, {
  mode: 'development',
  devtool: 'source-map',
  devServer: {
    contentBase: './dist',
    host: HOST,
    port: PORT,
    compress: true,
    inline: true,
    historyApiFallback: true,
    hot: true,
    overlay: true,
    open: true
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        include: [
          path.resolve(__dirname, 'src'),
          path.resolve('../../node_modules/patternfly'),
          path.resolve('../../node_modules/@patternfly/patternfly'),
          path.resolve('../../node_modules/@patternfly/react-styles/css'),
          path.resolve(
            '../../node_modules/@patternfly/react-core/dist/styles/base.css'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-core/dist/esm/@patternfly/patternfly'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css'
          ),
        ],
        use: ['style-loader', 'css-loader']
      }
    ]
  },
  output: {
    filename: 'src/components/styles.css'
  }
});