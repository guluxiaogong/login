$(function () {

    var internal = {
        init: function () {
            internal.doLayout();
            /**
             * 自动缩放
             */
            window.onresize = function () {
                internal.doLayout();

            };

            internal.submitForm();

            internal.loadCaptcha();
            $('#captchaImg').on('click', internal.loadCaptcha)

        },
        doLayout: function () {

        },
        submitForm: function () {
            var UNAME_COOKIE_NAME = 'lastLoginUserName';
            // 如果name没有value，将cookie中存储过的name值写入
            var loginName = $('input[name=loginName]');
            loginName.val(Cookie.get(UNAME_COOKIE_NAME));

            $('button[type=submit]').on('click', function (e) {
                e.preventDefault();
                // 登录按钮被点击时记住当前name
                Cookie.set(UNAME_COOKIE_NAME, $.trim(loginName.val()), null, 7 * 24 * 60);
                // 将密码字段使用 MD5(MD5(密码) + 验证码）编码后发给服务端
                var captcha = $('input[name=captcha]').val();
                $.ajax({
                    type: 'post',
                    url: 'login',
                    data: {
                        loginName: $.trim(loginName.val()),
                        password: $.md5($.md5($('input[type=password]').val()) + captcha),
                        captcha: captcha
                    },
                    beforeSend: function (xhr) {
                        console.log(xhr);
                        $('.message').html("登录中...");
                    },
                    success: function (result) { //...
                        var content = "网络连接失败！";
                        if (result) {
                            if (result.code == 0)
                                content = result.errorMsg;
                            else if (result.code == 1) {
                                if (result.backUrl)
                                    content = backUrl;
                                else
                                    content = "index";
                                window.location.href = content;
                                return;
                            }
                        }
                        $('.message').html(content);
                        console.log(result);
                    },
                    error: function (XMLHttpRequest, errorMsg, e) {
                        console.log(errorMsg);
                        $('.message').html("登录失败！");
                    }
                    ,
                    complete: function (xhr, ts) {
                        console.log(xhr);
                        internal.loadCaptcha();
                    }
                });
            });
        },
        loadCaptcha: function () {//加载验证码
            //alert('bb');
            $.ajax('preLogin').done(function (data) {
                console.log(data);
                $('#captchaImg').attr('src', data.imgData);
            }).fail(function () {
                alert('验证码加载失败');
            });
        }

    };

    internal.init();
});