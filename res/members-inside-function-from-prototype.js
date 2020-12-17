/**
 * Class.
 * @constructor
 * Constructor.
 */
js.net.Connection = function() {

};

js.net.Connection.prototype =
{
    /**
     * Object method.
     */
    send: function(url, data) {
        /**
         * Object constant.
         */
        this._DEFAULT_ROUTE = 'IPC';

        /**
         * Object field.
         */
        this._url = url;
    }
};
