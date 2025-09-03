// karma.conf.js
module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: { jasmine: {} },
    jasmineHtmlReporter: { suppressAll: true },

    // ⬇⬇⬇ GENERAR LCOV PARA SONAR
    coverageReporter: {
      // puedes dejar 'coverage' o 'coverage/frontend'; Sonar lo encontrará con el glob
      dir: require('path').join(__dirname, './coverage'),
      subdir: '.',                                  // deja el archivo directo en coverage/
      reporters: [
        { type: 'lcovonly', file: 'lcov.info' },    // <--- NECESARIO PARA SONAR
        { type: 'html' },
        { type: 'text-summary' }
      ]
    },

    // ⬇⬇⬇ AÑADE EL REPORTER 'coverage'
    reporters: ['progress', 'kjhtml', 'coverage'],

    browsers: ['Chrome'],                           // en CI podrás usar ChromeHeadless
    restartOnFileChange: true
  });
};
