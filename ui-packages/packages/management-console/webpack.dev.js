const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const webpack = require('webpack');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = function (env) {
  const dataIndexURL = env?.KOGITO_DATAINDEX_HTTP_URL ?? 'http://localhost:4000/graphql';
  return merge(common, {
    mode: 'development',
    devtool: 'source-map',
    devServer: {
      static: {
        directory: './dist'
      },
      host: HOST,
      port: PORT,
      compress: true,
      historyApiFallback: true,
      hot: true,
      open: true,
      client: {
        overlay: {
          warnings: false,
          errors: true
        },
        progress: true
      },
      proxy: {
        '/svg': {
          target: 'http://localhost:4000',
          secure: false,
          changeOrigin: true
        },
      }
    },
    plugins: [new webpack.EnvironmentPlugin({
      KOGITO_ENV_MODE: 'DEV',
      KOGITO_DATAINDEX_HTTP_URL: dataIndexURL
    })],
    module: {
      rules: [
        {
          test: /\.css$/,
          use: ['style-loader', 'css-loader']
        }
      ]
    },
    resolve: {
      extensions: ['.tsx', '.ts', '.js', '.jsx'],
      modules: [path.resolve(__dirname, 'src'), 'node_modules'],
    }
  })
}
