'use strict';

import mysql      from 'mysql';
import Pool       from 'mysql/lib/Pool';
import Connection from 'mysql/lib/Connection';
import Promise    from 'bluebird';

/**
  * Patch default Pool and Connection prototype of the mysql
  * to promise based to utilize the yield functionality
  */
Promise.promisifyAll( [ Pool, Connection ] );

/**
  * Setup the connection pool.
  **/
let pool = mysql.createPool( {
  host: 'localhost',
  user: 'root',
  password: '',
  port: 3306,
  database: 'phlink',
  connectionLimit: 25
} );


export class Model {

  /**
   * Executes the query.
   * @param {String}  qs  ... the string to be queried
   * @param {Array}   qa  ... the query arguments
   * @return {*}
   **/
  *query( qs, qa ) {
    let res = null;
    try {
      res = yield pool.queryAsync( qs, qa );
    } catch ( e ) {
      console.log( e );
    }
    return res;
  }

  /**
   * Gets a single row of the result set of the query.
   * @param {String}    qs
   * @param {Array}     qa
   * @return {Object}
   **/
  *getRow( qs, qa ) {
    let res = yield this.query( qs, qa );
    if ( res ) {
      return res[ 0 ];
    }
  }

  /**
   * Gets the rows of the result set of the query.
   * Alias of `query` but preferrably to used in
   * in `SELECT` queries.
   * @param {String}    qs
   * @param {Array}     qa
   * @return {Object}
   **/
  *getRows( qs, qa ) {
    return yield this.query( qs, qa );
  }
}
