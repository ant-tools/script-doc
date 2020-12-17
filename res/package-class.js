/**
 * Package class.
 * @constructor
 * Package class constructor.
 */
js.net.Connection = function(url) {
    /**
     * Package class constant.
     */
    this._DEFAULT_ROUTE = 'IPC';

    /**
     * Package class field.
     */
    this._url = url;

    /**
     * Package class method.
     */
    this.send = function(url, data) {
        return null;
    }
};
