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


        },
        doLayout: function () {

        },
        logout:function(){

        }


    };

    internal.init();
});