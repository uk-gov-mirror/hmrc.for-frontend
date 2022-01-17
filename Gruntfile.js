var nodeSass = require('node-sass');
module.exports = function(grunt) {
	'use strict';
    grunt.initConfig({
        // Clean
        clean: {
            public: {
                src:['public/**/*']
            }
        },
        connect: {
            server: {
                options: {
                    port: 23883
                }
            }
        },
        // Builds Sass
        sass: {
            dev: {
                options: {
                    lineNumbers: true,
                    style: 'expanded',
                    implementation: nodeSass
                },
                files: [{
                    expand: true,
                    cwd: 'frontend/sass',
                    src: ['*.scss'],
                    dest: 'public/stylesheets/',
                    ext: '.css'
                }]
            },
            prod: {
                options: {
                    lineNumbers: false,
                    style: 'compressed',
                    sourcemap: false,
                    implementation: nodeSass
                },
                files: [{
                    expand: true,
                    cwd: 'frontend/sass',
                    src: ['*.scss'],
                    dest: 'public/stylesheets/',
                    ext: '.min.css'
                }]
            }
        },

        // Copies templates and assets from external modules and dirs
        copy: {
            assets: {
                files: [{
                    expand: true,
                    cwd: 'frontend/',
                    src: ['**/*', '!sass/**', '!jasmine/**', '!javascripts/polyfills/**'],
                    dest: 'public/'
                }]
            },
            cutstomImages: {
                files: [{
                    expand: true,
                    cwd: 'frontend/images',
                    src: '**',
                    dest: 'public/images/'
                }]
            }
        },
        cssmin: {
            target: {
                files: [{
                    expand: true,
                    cwd: 'public/stylesheets',
                    src: ['*.css', '!*.min.css'],
                    dest: 'public/stylesheets',
                    ext: '.min.css'
                }]
            }
        },

        // workaround for libsass
        replace: {
            fixSass: {
                src: ['govuk_modules/govuk_template/**/*.scss', 'govuk_modules/govuk_frontend_toolkit/**/*.scss'],
                overwrite: true,
                replacements: [{
                    from: /filter:chroma(.*);/g,
                    to: 'filter:unquote("chroma$1");'
                }]
            }
        },
        jshint: {
            options: grunt.file.readJSON('.jshintrc'),
            javascripts: {
                src: [
                    'Gruntfile.js',
                    'frontend/javascripts/*.js'
                ]
            }
        },
        uglify: {
            app: {
                options: {
                    mangle: true,
                    beautify: false
                },
                files: {
                    'public/javascripts/app.min.js': [
                        'frontend/javascripts/**.js'
                    ]
                }
            },
            detailsPolyfill: {
                files: {
                    'public/javascripts/polyfills/details.polyfill.min.js': [
                        'frontend/javascripts/polyfills/details.polyfill.js'
                    ]
                }
            },
            toISOStringPolyfill: {
                files: {
                    'public/javascripts/polyfills/toISOString.polyfill.min.js': [
                        'frontend/javascripts/polyfills/toISOString.polyfill.js'
                    ]
                }
            },
            dev: {
                options: {
                    mangle: false,
                    beautify: true
                },
                files: {
                    'public/javascripts/app.js': [
                        'frontend/javascripts/**.js'
                    ]
                }
            }
        },
        // Watches assets and sass for changes
        watch: {
            css: {
                files: ['app/assets/sass/**/*.scss'],
                tasks: ['sass'],
                options: {
                    spawn: false
                }
            },
            assets: {
                files: ['app/assets/**/*', '!app/assets/sass/**'],
                tasks: ['copy:assets'],
                options: {
                    spawn: false
                }
            }
        },
        jasmine: {
            pivotal: {
                src: ['frontend/jasmine/specs/**/*.js'],
                options: {
                    keepRunner: false,
                    specs: 'frontend/jasmine/spec/*Spec.js',
                    //helpers: 'jasmine/spec/*Helper.js',
                    vendor: [
                        'frontend/javascripts/vendor/jquery-1.11.3.min.js',
                        'frontend/jasmine/vendor/jasmine-jquery.js',
                        'public/javascripts/govuk_template.js',
                        'public/javascripts/vendor/typeahead.bundle.min.js',
                        'public/javascripts/app.js',
                        'public/javascripts/selection-buttons.min.js'

                    ],
                    host : 'http://127.0.0.1:23883/',
                    noSandbox: true
                }
            }
        }
    });

    [
        'grunt-contrib-copy',
        'grunt-contrib-watch',
        'grunt-contrib-clean',
        'grunt-sass',
        'grunt-contrib-jshint',
        'grunt-contrib-uglify',
        'grunt-text-replace',
        'grunt-contrib-jasmine',
        'grunt-contrib-cssmin',
        'grunt-contrib-connect'
    ].forEach(function(task) {
        grunt.loadNpmTasks(task);
    });

    grunt.registerTask('generate-assets', [
        'clean',
        'jshint',
        'copy',
        'uglify',
        'replace',
        'sass',
        'connect',
        // 'jasmine', // Tests failing in Jenkins, uncomment to enable tests
        'cssmin'
    ]);

    grunt.registerTask('default', [
        'generate-assets'
    ]);

    grunt.event.on('watch', function(action, filepath, target) {
        if (target === 'assets') {
            grunt.config('copy.assets.files.0.src', filepath.replace('app/assets/', ''));
        }
    });
};