/**
 * Inner class.
 * @constructor
 * Inner class constructor.
 */
js.net.Connection.Transport = function(url) {
    /**
     * Inner class constant.
     */
    this._DEFAULT_ROUTE = 'IPC';

    /**
     * Inner class field.
     */
    this._url = url;

    /**
     * Inner class method.
     */
    this.send = function(url, data) {
        return null;
    }
};
