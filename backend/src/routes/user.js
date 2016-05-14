'use strict';

import { APIError }   from './../util/apiError';
import { UserModel }  from './../model/user';

export let UserConfig = {
  policies: {
    userExists: function*( next ) {
      let { userId } = this.params,
        user = yield UserModel.getUserById( userId );

      if ( !user ) {
        throw new APIError( 'USER_NOT_FOUND', 'User not found.', 404 );
      }

      this.params.state = { user };
      yield next;
    },
    isLoggedIn: function*( next ) {
      if ( !this.state || ( this.state && !this.state.user ) ) {
        throw new APIError( 'AUTHENTICATION_REQUIRED', 'Authentication required.', 400 );
      }
      yield next;
    }
  }
};
