$(function() {
  console.log("start");

  $("input:not(.button)").blur(function() {
    if (validator.isFieldValid(this.id, $(this).val())) {
      $(this).parent().find('.error').text('').hide();
    } else {
      $(this).parent().find('.error').text(validator.form[this.id].errorMessage).show();
    }
  });

  $("#submit").click(function() {
    console.log('click');
    $("input:not(.button)").blur();
    //if (!validator.isFormValid()) return false;
    $.ajax({
        type : "POST",
        url : "/userApi/register",
        data: $('#registerForm').serialize(),
        success : function (data) {
          if (data == 'user register success')
            $(location).attr('href', '/login');
          else
            $('#registerForm').before(data);
        },
        error: function(data) {
        	alert("error:" + data);
        }
    });
  });
});