/**
 * Outer class.
 */
js.net.Connection =
{
    /**
     * Nested class.
     * @constructor
     * Nested class constructor.
     */
    Transport: function(url) {
        /**
         * Nested class constant.
         */
        this._DEFAULT_ROUTE = 'IPC';

        /**
         * Nested class field.
         */
        this._url = url;

        /**
         * Nested class method.
         */
        this.send = function(url, data) {
            return null;
        }
    }
};
