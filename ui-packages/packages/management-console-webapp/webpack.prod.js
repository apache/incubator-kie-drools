const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  optimization: {
    minimizer: [new CssMinimizerPlugin()]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].css',
      chunkFilename: '[name].bundle.css'
    }),
    new webpack.EnvironmentPlugin({
      KOGITO_ENV_MODE: 'PROD'
    })
  ],
  module: {
    rules: [
      {
        test: /\.(css|sass|scss)$/,
        use: [ require.resolve('style-loader'),
        require.resolve('css-loader'),
        require.resolve('sass-loader')]
      }
    ]
  }
});
