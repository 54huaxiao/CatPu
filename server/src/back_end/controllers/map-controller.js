/**
 * @Author: yx
 * @Date:   2017-06-23
 * @Last modified by:   yx
 * @Last modified time: 2017-06-23
 */

const express = require('express')
const map_model = require('../models/map-model')

exports.select = (req, res, next) => {
  map_model.retrieveData()
    .then((maps) => {
      res.json(maps)
    })
    .catch(err => { console.log(err) })
}

exports.insert = (req, res, next) => {
  map_model.insert(req.body)
    .then((map) => {
        res.json({
            status: 'OK',
            msg: 'insert success'
        })
    })
    .catch(err => { console.log(err) })
}