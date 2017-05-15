/**
 * @Author: zx
 * @Date:   2017-05-13
 * @Email:  yangzx8@mail2.sysu.edu.cn
 * @Last modified by:   zx
 * @Last modified time: 2017-05-13
 */

/**
 * 自动化构建
 * 需要安装gulp
 * 在服务器运行：gulp full-stack
 * task的数组参数表示在此task之前执行的task
 * 这些任务将启动服务器端，将src文件复制到build文件夹中并监视文件的改动，自动重启服务
 */

var gulp = require('gulp')
var express = require('gulp-express')
var changed = require('gulp-changed')


gulp.task('full-stack', ['transmit-file'], function() {
  express.run(['./build/back_end/bin/www'])
  gulp.watch('./src/back_end/**', ['reload'])
})

gulp.task('reload', ['transmit-file'], function() {
  express.stop()
  express.run(['./build/back_end/bin/www'])
})

gulp.task('transmit-file', function() {
  return gulp.src('./src/back_end/**')
    .pipe(changed('./build/back_end/'))
    .pipe(gulp.dest('./build/back_end/'))
})