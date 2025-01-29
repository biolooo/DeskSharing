const webpack = require("webpack");

module.exports = {
  devServer: {
    proxy: {
      "^/api": {
        target: "http://localhost:8080", // Backend-Server
        changeOrigin: true,
        secure: false, // Nur für Entwicklung; erlaubt unsichere Verbindungen
        logLevel: "debug", // Debug-Logging aktivieren, um Proxy-Probleme zu erkennen
      },
    },
  },
  configureWebpack: {
    resolve: {
      alias: {
        vue$: "vue/dist/vue.runtime.esm-bundler.js", // Nutze die richtige Vue-Bundle-Version
      },
    },
    plugins: [
      new webpack.DefinePlugin({
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: true, // Aktiviert Hydration-Debugging für SSR
      }),
    ],
  },
  transpileDependencies: [],
};
