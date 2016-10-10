'use strict';

angular.module('testApp')
    .factory('InvitationStatusFactory', function InvitationStatusFactory() {

        return {
            PENDING: 1,
            ACCEPTED: 2,
            DECLINED: 3
        };
    });
