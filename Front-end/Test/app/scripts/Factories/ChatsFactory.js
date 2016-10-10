'use strict';

angular.module('testApp')
    .factory('ChatsFactory', function ChatsFactory($q) {

        var initializedDeffered = $q.defer();

        return {
            chats: {},

            setInitialized: function setInitialized(isSucceeded) {
                if (isSucceeded) {
                    initializedDeffered.resolve();
                } else {
                    initializedDeffered.reject();
                }
            },
            getInitialized: function getInitialized() {
                return initializedDeffered.promise;
            }
        };
    });
