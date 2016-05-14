'use strict';

/**
 * Error class for all API errors
 */
export class APIError extends Error {
  /**
   * Constructor
   * @param code
   * @param message
   * @param httpStatusCode
   * @param metadata
   */
  constructor( code, message, httpStatusCode = 409, metadata = {} ) {
    super( message );

    this.code           = code;
    this.metadata       = metadata;
    this.httpStatusCode = httpStatusCode;
  }

  /**
   * Generates an object to be sent to the client.
   * @access public
   * @returns {Object}
   */
  generateErrorObject() {
    return Object.assign( {}, this.metadata, {
      code: this.code,
      message: this.message
    } );
  }
}
