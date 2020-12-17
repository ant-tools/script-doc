/**
 * Inner class.
 * @constructor
 * Inner constructor.
 */
js.net.Connection.Transport.Channel = function(url) {
    /**
     * Inner object field.
     */
    this._url = url;
};

js.net.Connection.Transport.Channel.prototype =
{
    /**
     * Inner static constant.
     */
    DELAY: 1000,

    /**
     * Inner object method.
     */
    send: function(data) {
        return null;
    }
};
