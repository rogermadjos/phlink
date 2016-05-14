'use strict';

import { Model }            from './model';
import { TransactionModel } from './transaction';

class Ticket extends Model {
  constructor() {
    super();
  }

  /**
   * Creates a ticket records and a transaction record.
   * @param {String} opts.userId
   * @param {String} opts.ticketId
   * @param {String} opts.transactionId
   * @param {Object} opts.location
   * @param {Number} [opts.ticketAmount]
   * @param {Number} [opts.trnansactionAmount]
   **/
  *create( opts ) {
    let { long, lat } = opts.location;

    // Transaction amount is optional, and if ever
    // it's optional it means it's 0.
    opts = Object.assign( { transactionAmount: 0 }, opts );

    yield TransactionModel.create(
      Object.assign( { amount: transactionAmount }, opts )
    );

    yield this.query( `
      INSERT IGNORE INTO tickets (id, transactionId) VALUES(? ,?);

      INSERT INTO ticketActivites (ticketId, lat, long, state, amount)
      VALUES (?, ?, ?, ?, ?);
    `, [ opts.ticketId, opts.transactionId,
        opts.ticketId, lat, long, opts.state, opts.ticketAmount
      ] );
  }
  /**
   * Creates a ticket record with a transcation,
   * because it does not affect the user's balance.
   * @param opts.userId
   * @param opts.ticketId
   * @param opts.location
   **/
  *embark( opts ) {
    return yield this.create( Object.assign( { state: 'embark' }, opts ) );
  }

  /**
   * Creates a ticket with disembark state, and deducts the
   * @param {String} opts.userId
   * @param {Number} opts.amount
   * @param {Object} opts.location
   **/
  *disembark( opts ) {
    let { amount } = opts;
    return yield this.create(
      Object.assign( { state: 'disembark', transactionAmount: -amount }, opts )
    );
  }
}
