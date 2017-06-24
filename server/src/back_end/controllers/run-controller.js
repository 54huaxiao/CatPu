/**
 * @Author: yx
 * @Date:   2017-06-23
 * @Last modified by:   yx
 * @Last modified time: 2017-06-23
 */

const express = require('express')
const run_model = require('../models/run-model')

exports.select = (req, res, next) => {
  run_model.retrieveData(req.body.date, 'date')
    .then((runs) => {
      res.json(runs)
    })
    .catch(err => { console.log(err) })
}

exports.insert = (req, res, next) => {
  run_model.insert(req.body)
    .then((run) => {
        res.json({
            status: 'OK',
            msg: 'insert success'
        })
    })
    .catch(err => { console.log(err) })
}

exports.delete = (req, res, next) => {
  run_model.delete(date)
    .then(() => {
        res.json({
            status: 'OK',
            msg: 'delete success'
        })
    })
    .catch(err => {console.log(err) })
}