'use strict';

angular.module('testApp')
    .factory('UserIdentityFactory', function UserIdentityFactory() {

        function User(id, name, email, statusId) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.statusId = statusId;
        }

        return { user: new User() };
    });
