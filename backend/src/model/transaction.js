'use strict';

import { UserModel }  from './user';
import { APIError }   from '../util/apiError';
import { Model }      from './model';

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
    let fareAmount = null;
    console.log(opts);
    let user = yield UserModel.getUserById( opts.userId );
    console.log(user);
    if ( opts.fareId ) {
      let fare = ( yield this.query(
        'SELECT fare AS amount FROM fares WHERE id = ? ',
        [ opts.fareId ]
      ) )[ 0 ];
      if ( fare ) {
        fareAmount = fare.amount;
      }
    }
    
    if ( user.balance < fareAmount ) {
      throw new APIError( 'INSUFFICIENT_BALANCE', 'Insufficient balance.', 400 );
    }
    
    let delta = fareAmount !== null ? -fareAmount : opts.amount;

    yield this.query( `
        INSERT INTO transactions
          (id, ticketId, userId, fareId, amount, type)
        VALUES (?,?,?,?,?,?)`,
      [ opts.transactionId, opts.ticketId, opts.userId, opts.fareId,
        delta, opts.type  ]
    );

    return {
      balance: user.balance + delta,
      email: user.email
    };
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
      SELECT
        t.userId, t.id AS transactionId,
        t.ticketId AS ticketId, IF( f.id IS NOT NULL, -f.fare, t.amount ) AS amount, t.type,
        IF( f.id IS NOT NULL, CONCAT( 'From ', l1.locationName, ' to ', l2.locationName ), 'Topup' ) AS description
      FROM transactions AS t
        LEFT JOIN fares AS f
          ON t.fareId = f.id
        LEFT JOIN locations AS l1
          ON f.locationOne = l1.id
        LEFT JOIN locations AS l2
          ON f.locationTwo = l2.id
      WHERE t.userId = ?
      ORDER BY t.createdAt DESC
      LIMIT ? OFFSET ?
    `, [ userId, limit, offset ] );
  }
}

export let TransactionModel = new Transaction();
