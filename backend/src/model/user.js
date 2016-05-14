'use strict';

import bcrypt     from 'bcryptjs';
import { Model }  from './model';
import Promise    from 'bluebird';
import randToken  from 'rand-token';

let crypt = {
  hash: Promise.promisify( bcrypt.hash ),
  compare: Promise.promisify( bcrypt.compare )
}, code = randToken.generate.bind( null, 8, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890' );

class User extends Model {
  constructor() {
    super();
  }

  /**
   * Gets the user information and balance
   * based on the summation of the transaction amount.
   *
   * @param  {String} id
   * @return {Object}
   **/
  *getUserById( id ) {
    return yield this.getRow( `
      SELECT u.*, ut.balance AS balance
        FROM users AS u
        JOIN (
          SELECT t.userId, SUM(t.amount) AS balance
            FROM transactions AS t
        ) AS ut
          ON ut.userId = u.id
      WHERE u.id = ?
      LIMIT 1;
    `, [ id ] );
  }

  /**
   * Creates a new user with the given id, email, and password.
   * @param {Object} opts
   * @param {String} opts.id
   * @param {String} opts.email
   * @param {String} opts.password
   * @return {String}
   **/
  *create( opts ) {
    let hash = yield crypt.hash( opts.password, 10 ),
      id = code();
    yield this.query(
      'INSERT INTO users(id, email, hash) VALUES(?, ?, ?)',
      [ id, opts.email, hash ]
    );
    return id;
  }

  /**
   * Gets user by credentials, returns false when user not found
   * or password does not match.
   * @param {String} email
   * @param {String} password
   * @return {boolean|Object}
   **/
  *getUserByCredentials( email, password ) {
    let res = yield this.getRow(
      'SELECT id, hash, email FROM users WHERE email = ?',
      [ email ]
    );

    if ( !res ) {
      return false;
    }

    let match = yield crypt.compare( password, res.hash );
    if ( !match ) {
      return false;
    }
    delete res.hash;
    return res;
  }
}

export let UserModel = new User();
