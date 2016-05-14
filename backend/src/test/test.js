/* globals describe, it */
'use strict';

import chai       from 'chai';
import supertest  from 'co-supertest';
import Pool       from 'mysql/lib/Pool';
import Connection from 'mysql/lib/Connection';
import Promise    from 'bluebird';
import bcrypt     from 'bcryptjs';
import randToken  from 'rand-token';
import mysql      from 'mysql';
import chaiThings from 'chai-things';

/**
  * Patch default Pool and Connection prototype of the mysql
  * to promise based to utilize the yield functionality
  */
Promise.promisifyAll( [ Pool, Connection ] );

let expect = chai.expect,
  request = supertest( global.app ),
  pool = mysql.createPool( {
    host: 'localhost',
    user: 'root',
    password: '',
    port: 3306,
    database: 'phlink',
    connectionLimit: 25
  } ),
  crypt = {
    hash: Promise.promisify( bcrypt.hash ),
    compare: Promise.promisify( bcrypt.compare )
  },
  userCode = randToken.generate.bind( null, 8, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890' ),
  sensorKey = 'ign0q-RD2N9-g7TUc-LR6pI';
chai.should();
chai.use( chaiThings );

function *assertLogin( opts ) {
  opts = Object.assign( {
    code: 200,
    error: null,
    assert: true
  }, opts );
  let token = null;
  yield request
    .post( '/user/auth' )
    .send( { email: opts.email, password: opts.password } )
    .expect( opts.code )
    .expect( res => {
      if ( opts.error !== null ) {
        expect( res.body ).to.have.property( 'code', opts.error );
      }

      if ( opts.assert ) {
        expect( res.body ).to.have.property( 'token' ).that.is.a( 'string' );
        token = res.body.token;
      }
    } );
  return token;
}

  function *createUser( opts ) {
    let hash = yield crypt.hash( opts.password, 10 );
    yield pool.queryAsync(
      'INSERT INTO users(id, email, hash) VALUES(?,?,?)',
      [ opts.id, opts.email, hash ]
    );
  }
describe( 'API Tests', function() {
  describe( 'User', function() {
    describe( 'Authentication', function () {
      afterEach( function*() {
        yield pool.queryAsync( 'DELETE FROM users WHERE 1' );
      } );

      describe( 'Given user does not exists', function() {
        it( 'should give an error user not found', function*() {
          yield assertLogin( {
            email: 'djansyle',
            password: 'password',
            code: 404,
            error: 'USER_NOT_FOUND',
            assert: false
          } );
        } );
      } );

      describe( 'Given account exists but wrong password', function() {
        let id = randToken.generate(8),
          email = 'test@gmail.com';

        before( function*() {
          yield createUser( { id, email, password: 'password' } );
        } );

        it( 'should give an error user not found', function*() {
          yield assertLogin( {
            email: 'test@gmail.com',
            password: 'wrongpassword',
            code: 404,
            error: 'USER_NOT_FOUND',
            assert: false
          } );
        } );
      } );

      describe( 'Given correct credentials', function() {
        let id = randToken.generate(8),
          email = 'testing@gmail.com',
          password = 'password';

        before( function*() {
          yield createUser( { id, email, password } );
        } );

        it( 'should be able to login', function*() {
          let token = yield assertLogin( {
            email,
            password,
            code: 200,
            assert: true
          } );

          yield request
            .get( `/user/auth/test?token=${ token }` )
            .expect( 200 )
            .end();
        } );
      } );
    } );

    describe( 'Creation', function() {
      describe( 'Given all fields are valid', function() {
        it( 'should be able to create new user', function*() {
          yield request
            .post( '/user' )
            .send( { email: 'djansyle', password: 'passy' } )
            .expect( 200 )
            .end();

          let res = yield pool.queryAsync(
            'SELECT COUNT(*) as count FROM users WHERE email = ?',
            [ 'djansyle' ]
          );

          expect( res ).to.have.length( 1 );
        } );
      } );
    } );

    describe( 'Information', function() {
      after( function*() {
        yield pool.queryAsync( 'DELETE FROM transactions WHERE 1' );
      } );

      let token = null,
        user = {
          id: userCode(),
          email: 'djanyledjans@gmail.com',
          password: 'password',
        };

      before( function*() {
        yield createUser( user );
        token = yield assertLogin( user );
        yield [ 100, 50, 20 ].map( amount =>
          pool.queryAsync(
            'INSERT INTO transactions(id, userId, amount, type) VALUES(?,?,?,?)',
            [ userCode(), user.id, amount, 'topup' ]
          )
        );
      } );

      describe( 'Given credentials are correct', function() {
        it( 'should be able to get all the user information', function*() {
          yield request
            .get( `/user?token=${ token }`)
            .expect( 200 )
            .expect( res => {
              expect( res.body ).to.have.property( 'id', user.id );
              expect( res.body ).to.have.property( 'email', user.email );
              expect( res.body ).to.have.property( 'balance', 170 );
            } );
        } );
      } );

      describe( 'Given credentials is not correct', function() {
        it( 'should give an error', function*() {
          yield request
            .get( '/user?token=aoeu')
            .expect( 400 )
            .expect( res =>
              expect( res.body ).to.have.property( 'code', 'AUTHENTICATION_REQUIRED' )
            );
        } );
      } );
    } );
  } );

  describe( 'Transactions', function() {
    let token = null,
      user = {
        id: userCode(),
        email: 'djanyledjans@gmail.com',
        password: 'password',
      };

    before( function*() {
      yield createUser( user );
      token = yield assertLogin( user );
      yield [ 100, 50, 20 ].map( amount =>
        pool.queryAsync(
          'INSERT INTO transactions(id, userId, amount, type) VALUES(?,?,?,?)',
          [ userCode(), user.id, amount, 'topup' ]
        )
      );
    } );

    after( function*() {
      yield pool.queryAsync( 'DELETE FROM transactions WHERE 1' );
    } );

    describe( 'Given has valid credentials', function() {
      it( 'should be able to get list of transactions', function*() {
        yield request
          .get( `/user/${ user.id }/transactions?token=${ token }` )
          .expect( 200 )
          .expect( res =>
            [ 100, 50, 20 ].forEach( amount =>
              res.body.should.include.an.item.with.property( 'amount', amount )
          ) );
      } );
    } );
    describe( 'Sensor', function() {
      describe( 'Given sensor key is invalid', function() {
        it( 'should give an error', function*() {
          yield request
            .put( `/user/${ user.id }/transaction/123-abcd-456-efgh?type=embark&key=123` )
            .send( {
              ticketId: '1s23-93to-23hn3'
            } )
            .expect( 400 )
            .expect( res =>
              expect( res.body ).to.have.property( 'code', 'INVALID_FIELDS' )
            );
        } );
      } );
      describe( 'Given sensory key is valid', function() {
        describe( 'Given transactionId does not exists and ticketId', function() {
          let transactionId = '123-abcd-456-efgh',
            ticketId = 'aoeu-123-stoet-987'
          before( function*() {
            yield pool.queryAsync( 'DELETE FROM transactions WHERE 1' );
          } );
          it( 'should be able to add a new transaction record', function*() {
            yield request
              .put( `/user/${ user.id }/transaction/${ transactionId }?key=${ sensorKey }&type=embark` )
              .send( {
                ticketId,
                fareId: 1
              } )
              .expect( 200 )
              .end();
          } );
        } );
      } );
    } );
  } );
} );
