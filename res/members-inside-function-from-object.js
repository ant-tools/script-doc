/**
 * Class.
 */
js.net.Connection =
{
    /**
     * Static method.
     */
    send: function(url, data) {
        /**
         * Static constant.
         */
        this._DEFAULT_ROUTE = 'IPC';

        /**
         * Static field.
         */
        this._url = url;
    }
};
