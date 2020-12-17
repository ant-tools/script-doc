$package("js.tools");

js.tools.Class = function () {
};

js.tools.Class.prototype = {
    method : function () {
        this.method = null;
    }
};
$extends(js.tools.Class, Object);
