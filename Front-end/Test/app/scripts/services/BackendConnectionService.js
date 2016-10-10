'use strict';

angular.module('testApp')
    .service('BackendConnectionService', function BackendConnectionService($q, $rootScope) {

        angular.extend(this, {
            connect: connect,
            subscribe: subscribe,
            send: send,
            disconnect: disconnect
        });

        var client;

        function connect() {
            var deferred = $q.defer();
            var url = "http://localhost:8080/FirstSpringProject/ws";
            var ws = new SockJS(url);

            client = Stomp.over(ws);

            client.connect({}, connectCallback);

            function connectCallback() {
                // called back after the client is connected and authenticated to the STOMP server
                deferred.resolve();
            };

            return deferred.promise;
        }

        function subscribe(endpoint, callback) {
            return client.subscribe(endpoint, function(message) {
                $rootScope.$apply(function() {
                    callback(JSON.parse(message.body));
                });
            });
        }

        function send(endpoint, data) {
            if (typeof data === 'object') {
                data = JSON.stringify(data);
            }

            client.send("/app" + endpoint, {}, data);
        }

        function disconnect() {
            client.disconnect(function() {
                alert("See you next time!");
            });
        }

    });
