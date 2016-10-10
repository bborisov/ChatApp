'use strict';

angular.module('testApp')
    .controller('LoginCtrl', function LoginCtrl($scope, $state, LoginService) {

        $scope.signIn = function signIn() {
            LoginService.signIn($scope.email, function signInCallback() {
                $state.go('chats');
            });
        }

    });
