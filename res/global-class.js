/**
 * Global class.
 * @constructor
 * Global class constructor.
 */
Connection = function(url) {
    /**
     * Global class constant.
     */
    this._DEFAULT_ROUTE = 'IPC';

    /**
     * Global class field.
     */
    this._url = url;

    /**
     * Global class method.
     */
    this.send = function(url, data) {
        return null;
    }
};
