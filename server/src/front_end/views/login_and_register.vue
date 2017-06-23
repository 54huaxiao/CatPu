<template>
	<div id='login-register-page'>
		<mu-tabs id='login-register-toggle' :value="activeTab" @change="handleTabChange">
    	<mu-tab value="login" title="登录"/>
    	<mu-tab value="register" title="注册"/>
    </mu-tabs>
		<div id='login-container' v-if='activeTab=="login"'>
			<mu-paper id='login-paper'>
				<div id='login-name'>
					<mu-text-field class='login-text' @focus='clearHint("l_n_error")' :inputClass='{"l-text-f":true}' :hintTextClass='{"l-r-hint":true}' v-model='l_name' hintText='昵称' icon='perm_identity' :iconClass='{"l-r-icon":true}' :underlineShow='false' />
					<div class='l-r-error-text'>{{ l_n_error }}</div>
				</div>
				<div id='login-password'>
					<mu-text-field class='login-text' @focus='clearHint("l_pa_error")' :inputClass='{"l-text-f":true}' :hintTextClass='{"l-r-hint":true}' v-model='l_password' hintText='密码' type='password' icon='lock' :iconClass='{"l-r-icon":true}' :underlineShow='false' />
					<div class='l-r-error-text'>{{ l_pa_error }}</div>
				</div>
				<div>
					<mu-raised-button id='login-button' label='登 录' @click='login' />
				</div>
			</mu-paper>
		</div>
			
		<div id='register-container' v-if='activeTab=="register"'>
			<mu-paper id='register-paper'>
				<div id='register-name'>
					<mu-text-field class='register-text' @focus='clearHint("r_n_error")' :inputClass='{"r-text-f":true}' :hintTextClass='{"l-r-hint":true}' v-model='r_name' hintText='昵称' icon='perm_identity' :iconClass='{"l-r-icon":true}' :underlineShow='false' @blur='isValid("r_n_error")' />
					<div class='l-r-error-text'>{{ r_n_error }}</div>
				</div>
				<div id='register-phone'>
					<mu-text-field class='register-text' @focus='clearHint("r_ph_error")' :inputClass='{"r-text-f":true}'  :hintTextClass='{"l-r-hint":true}' v-model='r_phone' hintText='电话' icon='phone' :iconClass='{"l-r-icon":true}' :underlineShow='false' @blur='isValid("r_ph_error")' />
					<div class='l-r-error-text'> {{ r_ph_error }} </div>
				</div>
				<div id='register-email'>
					<mu-text-field class='register-text' @focus='clearHint("r_em_error")' :inputClass='{"r-text-f":true}'  :hintTextClass='{"l-r-hint":true}' v-model='r_email' hintText='邮箱' icon='email' :iconClass='{"l-r-icon":true}' :underlineShow='false' @blur='isValid("r_em_error")' />
					<div class='l-r-error-text'> {{ r_em_error }} </div>
				</div>
				<div id='register-password'>
					<mu-text-field class='register-text' @focus='clearHint("r_pa_error")' :inputClass='{"r-text-f":true}' :hintTextClass='{"l-r-hint":true}' v-model='r_password' hintText='密码' type='password' icon='lock' :iconClass='{"l-r-icon":true}' :underlineShow='false' @blur='isValid("r_pa_error")' />
					<div class='l-r-error-text'> {{ r_pa_error }} </div>
				</div>
				<div>
					<mu-raised-button id='register-button' label='注 册' @click='register' />
				</div>
			</mu-paper>
		</div>
		
		
	</div>
</template>



<script>
import md5 from 'md5'

export default {
	props: ['url'],
	data() {
		return {
			activeTab: 'login',
			l_name: '',
			l_password: '',
			r_name: '',
			r_phone: '',
			r_email: '',
			r_password: '',
			l_n_error: '',
			l_pa_error: '',
			r_n_error: '',
			r_ph_error: '',
			r_em_error: '',
			r_pa_error: '',
		}
	},
	methods: {
		handleTabChange(val) {
      this.activeTab = val
    },
    login() {
    	let info = {
    		username: this.l_name,
    		password: md5(this.l_password)
    	}
    	this.$http.post(`${this.url}/user/login`, info)
    		.then(res => {
    			if (res.data.status == 'OK') {
    				alert(res.data.msg)
    			} else if (res.data.status == 'USER_NOT_EXIST') {
    				this.l_n_error = res.data.msg
    			} else if (res.data.status == 'PASSWORD_WRONG') {
    				this.l_pa_error = res.data.msg
    			}
    			console.log(res.data)
    		})
    },
    register() {
    	let info = {
    		username: this.r_name,
    		phone: this.r_phone,
    		email: this.r_email,
    		password: md5(this.r_password)
    	}
    	this.$http.post(`${this.url}/user/register`, info)
    		.then(res => {
    			if (res.data.status == 'USER_EXIST') {
    				this.r_n_error = res.data.msg
    			} else if (res.data.status == 'INVALID_VALUE') {
    				alert(res.data.msg)
    			} else if (res.data.status == 'OK') {
    				alert(res.data.msg)
    				this.activeTab = 'login'
    			}
    		})
    },
    isValid(val) {
    	if (val == 'r_n_error' && this.r_name == '') {
    		this.r_n_error = '昵称不能为空'
    	} else if (val == 'r_ph_error' && !(/^[1-9]\d{10}$/.test(this.r_phone))) {
    		this.r_ph_error = '11位数字，不能以零开头'
    	} else if (val == 'r_em_error' && !(/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test(this.r_email))) {
    		this.r_em_error = '邮箱不合法'
    	} else if (val == 'r_pa_error' && this.r_password == '') {
    		this.r_pa_error = '密码不能为空'
    	}
    },
    clearHint(val) {
    	(this.$data)[val] = ''
    }
	}
}
</script>


<style>

#login-register-page {
	width: 100%;
	height: 100%;
}

#login-register-toggle {
	margin: auto;
	margin-top: 7%;
	width: 500px;
	height: 48px;
	background-color: #5A4F60;
}

#login-container {
	margin: auto;
	width: 500px;
	height: 400px;
}

#login-paper {
	width: 100%;
	height: 100%;
	background-color: #5A4F60;
	text-align: center;
}

#register-container {
	margin: auto;
	width: 500px;
	height: 500px;
}

#register-paper {
	width: 100%;
	height: 100%;
	background-color: #5A4F60;
	text-align: center;
}

#login-name, #login-password {
	position: relative;
	margin: auto;
	margin-bottom: 10%;
	top: 12%;
	width: 80%;
	height: 60px;
	border-radius: 10px;
	background-color: #3D3743;
}

#register-name, #register-phone, #register-email, #register-password {
	position: relative;
	margin: auto;
	margin-bottom: 6%;
	top: 10%;
	width: 80%;
	height: 55px;
	border-radius: 10px;
	background-color: #3D3743;
}

#login-button {
	margin-top: 12%;
	width: 300px;
	height: 50px;
	font-size: 25px;
	color: #CCCCCC;
	background-color: #008B8B;
	border-radius: 15px;
}

#register-button {
	margin-top: 12%;
	width: 300px;
	height: 50px;
	font-size: 25px;
	color: #CCCCCC;
	background-color: #008B8B;
	border-radius: 15px;
}

.login-text {
	position: relative;
	float: left;
	height: 100%;
	top: 16%;
	font-size: 25px;
}

.register-text {
	position: relative;
	float: left;
	height: 100%;
	top: 14%;
	font-size: 20px;
}

.l-text-f {
	color: #DDDDDD;
	position: relative;
	top: -56px;
}

.r-text-f {
	color: #DDDDDD;
	position: relative;
	top: -51px;
}

.l-r-hint {
	font-size: 18px;
	color: #666666;
}

.l-r-error-text {
	position: absolute;
	float: right;
	max-width: 300px;
	white-space:nowrap;
	overflow: hidden;
	font-weight: bold;
	color: #993333;
	background-color: #3D3743;
	height: 70%;
	padding-top: 2%;
	font-size: 16px;
	right: 6%;
	top: 16%;
}

.l-r-icon {
	position: relative;
	float: left;
	height: 100%;
	left: -18%;
	top: 9px;
	padding: 0px;
}

</style>