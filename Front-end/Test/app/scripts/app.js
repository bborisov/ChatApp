'use strict';

/**
 * @ngdoc overview
 * @name testApp
 * @description
 * # testApp
 *
 * Main module of the application.
 */
angular
    .module('testApp', [
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'ngStomp',
        'ui.router',
        'luegg.directives'
    ])
    .config(function($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('root', {
                abstract: true,
                template: '<div ui-view=""></div>',
                resolve: {
                    connectionCheck: function(BackendConnectionService) {
                        return BackendConnectionService.connect();
                    }
                }
            });

        $stateProvider
            .state('login', {
                parent: 'root',
                url: '/login',
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl',
                controllerAs: 'login'
            })
            .state('chats', {
                parent: 'root',
                url: '/chats',
                templateUrl: 'views/chats.html',
                controller: 'ChatsCtrl',
                controllerAs: 'chats',
                resolve: { accessCheck: accessRestrictionHandler }
            })
            .state('chats.currentChat', {
                url: '/:chatId',
                templateUrl: 'views/currentChat.html',
                controller: 'CurrentChatCtrl',
                controllerAs: 'currentChat'
            });

        $urlRouterProvider.otherwise('login');

        function accessRestrictionHandler($q, $state, $timeout, UserIdentityFactory) {
            var deferred = $q.defer();

            if (UserIdentityFactory.user.id) {
                deferred.resolve();
            } else {
                deferred.reject();
                $timeout(function() {
                    $state.go('login');
                }, 0);
            }

            return deferred.promise;
        }

    });
