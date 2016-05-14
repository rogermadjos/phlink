'use strict';

import Model from './model';

class Transaction extends Model {
  constructor() {
    super();
  }

  /**
   * Creates a record of user's transactions.
   * @param {Object} opts
   * @param {String} opts.userId
   * @param {Number} opts.amount
   **/
  *create( opts ) {
    let res = yield this.query(
      'INSERT INTO transactions (userId, amount) VALUES (?,?)',
      [ opts.userId, opts.amount ]
    );
    return res;
  }

  /**
   * Gets the transactions of the user with the given `limit` and `offset`.
   * @param {String}  userId
   * @param {String}  limit
   * @param {Number}  offset
   * @returns {Array}
   **/
  *getTransactionsById( userId, limit=100, offset=0 ) {
    return yield this.getRows( `
      SELECT *
       FROM transactions
      WHERE userId = ?
      ORDER BY createdAt DESC
      LIMIT ? OFFSET ?
    `, [ userId, limit, offset ] );
  }
}

export let TransactionModel = new Transaction();
