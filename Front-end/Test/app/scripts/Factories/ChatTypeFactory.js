'use strict';

angular.module('testApp')
    .factory('ChatTypeFactory', function ChatTypeFactory() {

        return {
            PUBLIC: 1,
            PRIVATE: 2
        };
    });
