'use strict';

angular.module('testApp')
    .factory('MembersFactory', function MembersFactory($q) {

        return { members: {} };
    });
