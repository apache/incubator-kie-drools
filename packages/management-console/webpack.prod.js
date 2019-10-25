const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  optimization: {
    minimizer: [new OptimizeCSSAssetsPlugin({})]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].css',
      chunkFilename: '[name].bundle.css'
    })
  ],
  module: {
    rules: [
      {
        test: /\.css$/,
        include: [
          path.resolve(__dirname, 'src'),
          path.resolve('../../node_modules/patternfly'),
          path.resolve('../../node_modules/@patternfly/patternfly'),
          path.resolve('../../node_modules/@patternfly/react-styles/css'),
          path.resolve('../../node_modules/@patternfly/react-core/dist/styles/base.css'),
          path.resolve('../../node_modules/@patternfly/react-core/dist/esm/@patternfly/patternfly'),
          path.resolve('../../node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css'),
          path.resolve('../../node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css')
        ],
        loaders: ['style-loader', 'css-loader']
      }
    ]
  }
});
