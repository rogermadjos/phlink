'use strict';

import Router         from 'koa-router';
import bodyParser     from 'koa-bodyparser';
import koa            from 'koa';
import randToken      from 'rand-token';
import { UserModel }  from './model/user';
import { APIError }   from './util/apiError';
import { UserConfig } from './routes/user';

let app = koa(),
  router = Router(),
  Tokens = global.Tokens = { },
  sensorKey = 'ign0q-RD2N9-g7TUc-LR6pI'

app.use( function* errorHandler( next ) {
  try {
    yield next;
  } catch ( e ) {
    if ( e instanceof APIError ) {
      let err = e.generateErrorObject();
      this.status = e.httpStatusCode;
      this.body = err;
    } else {
      console.log( e.stack );
      this.status = 500;
    }
  }
} );

app.use( bodyParser() );

app.use( function* getUserToken ( next ) {
  let token = this.request.query.token || this.cookies.get( 'token' );
  if ( token && Tokens[ token ] ) {
    this.state.user = Tokens[ token ];
  }

  yield next;
} );

router.get(
  '/user/auth/test',
  UserConfig.policies.isLoggedIn,
  function*() {
    this.status = 200;
  }
);

router.post(
  '/user/auth',
  function*() {
    let { email, password } = this.request.body;
    if ( !email || !password ) {
      return new APIError( 'INVALID_FIELDS', 'Invalid fields', 400 );
    }
    let user = yield UserModel.getUserByCredentials( email, password );
    if ( !user ) {
      throw new APIError( 'USER_NOT_FOUND', 'User not found.', 404 );
    }

    let token = randToken.generate( 16 );
    this.cookies.set( 'token', token );
    Tokens[ token ] = user;

    this.body = { token };
  }
);

router.get(
  '/user',
  UserConfig.policies.isLoggedIn,
  function*() {

    this.body = yield UserModel.getUserById( this.state.user.id );
  }
)

router.delete(
  '/user/auth',
  UserConfig.policies.isLoggedIn,
  function*() {
    let token = this.request.query.token || this.cookes.get( 'token' );
    if ( Tokens[ token ] ) {
      delete Tokens[ token ];
    }
  }
);

router.post(
  '/user',
  function*() {
    this.body = yield UserModel.create( this.request.body );
  }
);

app.use( router.routes() );
app.use( router.allowedMethods() );
global.app = app.listen( 7777 );