const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = merge(common, {
  mode: 'development',
  devtool: 'source-map',
  output: {
    publicPath: '/'
  },
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
      overlay: false,
      progress: true
    },
    proxy: [
      {
        context: ['/svg', '/forms', '/customDashboard'],
        target: 'http://localhost:4000',
        secure: false,
        changeOrigin: true
      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, 'resources', 'index.html'),
      favicon: 'src/favicon.ico',
      chunks: ['app']
    })
  ],
  module: {
    rules: [
      {
        test: /\.(css|sass|scss)$/,
        use: [require.resolve('style-loader'), require.resolve('css-loader'), require.resolve('sass-loader')]
      },
      {
        test: /\.css$/,
        include: [path.resolve('../../node_modules/monaco-editor')],
        use: [require.resolve('style-loader'), require.resolve('css-loader')]
      }
    ]
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js', '.jsx'],
    modules: [path.resolve('src'), 'node_modules']
  }
});
