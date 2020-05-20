const commonConfig = {
  env: process.env.NODE_ENV || 'development',
  port: parseInt(process.env.PORT, 10) || 4000,
  corsDomain: process.env.CORS_DOMAIN || '*'
};

module.exports = commonConfig;
