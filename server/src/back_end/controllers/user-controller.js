/**
 * @Author: yuanxin
 * @Date:   2017-05-12
 * @Last modified by:   yx
 * @Last modified time: 2017-05-16
 */

const express = require('express')
const user_model = require('../models/user-model')

exports.register = (req, res, next) => {
  user_model.retrieveData(req.body.username, 'username')
    .then(([user]) => {
      if (user != null) {
        res.json({
          status: 'USER_EXIST',
          msg: '用户：' + req.body.username + '已存在',
          content: [user.username]
        })
      } else if (req.body.username == '' || req.body.phone == ''
        || req.body.email == '' || req.body.password == '') {
        console.log(req.body.username)
        console.log(req.body.phone)
        console.log(req.body.email)
        console.log(req.body.password)
        res.json({
          status: 'INVALID_VALUE',
          msg: '参数不能为空',
          content: ['']
        })
      } else {
        user_model.register(req.body)
          .then(users => {
            res.json({
              status: 'OK',
              msg: '用户：' + req.body.username + '注册成功',
              content: [req.body.username]
            })
          })
          .catch(err => { console.log(err) })
      }
    })
    .catch(err => { console.log(err) })
}

exports.login = (req, res, next) => {
  // 若已经登录
  // if (req.session.username) {
  //   res.send("you don't logout!");
  //   return;
  // }
  user_model.retrieveData(req.body.username, 'username')
    .then(([user]) => {
      if (user == null) {
        res.json({
          status: 'USER_NOT_EXIST',
          msg: '用户：' + req.body.username + '不存在',
          content: [user]
        })
      } else if (user.password != req.body.password) {
        res.json({
          status: 'PASSWORD_WRONG',
          msg: '密码错误',
          content: [user.username]
        })
      } else {
        req.session.username = req.body.username
        res.json({
          status: 'OK',
          msg: '用户：' + req.body.username + '登陆成功',
          content: [user.username]
        })
      }
    })
    .catch(err => { console.log(err) })
}

exports.logout = (req, res, next) => {
  req.session.user = null
  res.send('退出成功')
}

exports.show = (req, res, next) => {
  user_model.retrieveData(req.body.username, 'username')
    .then(data => {
      req.session.user = req.body.username
      res.send(data)
    })
    .catch(err => {
      console.log(err)
    })
}

exports.info = (req, res, next) => {
  if (!req.session.user) res.send('not login')
  else {
    user_model.retrieveData(req.session.user, 'username')
      .then(data => res.send(data))
      .catch(err => {
        console.log(err)
      })
  }
}