/**
 * Class.
 * @constructor
 * Constructor.
 */
js.net.Connection = function(url) {
    /**
     * Object method.
     */
    this.send = function(url, data) {
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
