const express = require('express')
const router = express.Router()
const user_controller = require('../controllers/user-controller')

router.post('/signup', user_controller.signup)
router.post('/login', user_controller.login)
router.post('/show', user_controller.show)
router.get('/info', user_controller.info)

module.exports = router
