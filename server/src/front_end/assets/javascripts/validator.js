var validator = {
	form: {
		username: {
			status: false,
			errorMessage: "6~18位英文字母，数字或下划线，必须以英文字母开头"
		},
		password: {
			status: false,
			errorMessage: "6~10位英文字母或数字"
		},
		telephone: {
			status: false,
			errorMessage: "11位数字，不能以零开头"
		},
		email: {
			status: false,
			errorMessage: "邮箱不合法"
		}
	},

	isUsernameValid: function(username) {
		return this.form.username.status = /^[a-zA-Z][0-9a-zA-Z_]{5,17}$/.test(username);
	},

	isPasswordValid: function(password) {
		return this.form.password.status = /[0-9a-zA-Z_]{6,10}$/.test(password);
	},

	isTelephoneValid: function(telephone) {
		return this.form.telephone.status = /^[1-9]\d{10}$/.test(telephone);
	},

	isEmailValid: function(email) {
		return this.form.email.status = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test(email);
	},

	isFieldValid: function(fieldname, value) {
		var CapFiledname = fieldname[0].toUpperCase() + fieldname.slice(1, fieldname.length);
		return this["is" + CapFiledname + 'Valid'](value);
	},

	isFormValid: function() {
		return this.form.username.status && this.form.telephone.status && this.form.email.status;
	},

	getErrorMessage: function(fieldname) {
		return this.form[fieldname].errorMessage;
	}
}

if (typeof module == 'object') {
	module.exports = validator;
}