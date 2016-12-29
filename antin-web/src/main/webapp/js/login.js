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

            $('button[type=submit]').on('click',internal.submitForm);

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

            // 登录按钮被点击时记住当前name
            Cookie.set(UNAME_COOKIE_NAME, $.trim(loginName.val()), null, 7 * 24 * 60);
            // 将密码字段使用 MD5(MD5(密码) + 验证码）编码后发给服务端
            var captcha = $('input[name=captcha]').val();
            $.ajax({
                type: 'post',
                url: 'login',
                data: {
                    userName: $.trim(loginName.val()),
                    password: $.md5($.md5($('input[type=password]').val()) + captcha),
                    captcha: captcha
                },
                beforeSend: function (xhr) {
                    console.log(xhr);
                }

                ,
                success: function (result) { //...
                    console.log(result);
                }
                ,
                error: function (XMLHttpRequest, errorMsg, e) {
                    console.log(errorMsg);

                }
                ,
                complete: function (xhr, ts) {
                    console.log(xhr);
                    internal.loadCaptcha();
                }
            });


        },
        loadCaptcha: function () {//加载验证码
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