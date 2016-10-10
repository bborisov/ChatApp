'use strict';

angular.module('testApp')
    .factory('MemberRoleFactory', function MemberRoleFactory() {

        return {
            ADMIN: 1,
            USER: 2
        };
    });
