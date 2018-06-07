'use strict';

angular.module('testApp')
    .service('LoginService', function LoginService(UserIdentityFactory, BackendConnectionService) {

        angular.extend(this, {
            signIn: signIn
        });

        function signIn(email, password, callback) {

            var signInSubscription = BackendConnectionService.subscribe("/user/queue/users/login", subscribeCallbackSignIn);
            BackendConnectionService.send("/login", { "email": email, "password": password });

            function subscribeCallbackSignIn(data) {
                if (data) {
                    UserIdentityFactory.user.id = data.id;
                    UserIdentityFactory.user.name = data.name;
                    UserIdentityFactory.user.email = data.email;
                    UserIdentityFactory.user.statusId = data.statusId;
                    callback();
                } else {
                    alert("Invalid credentials!");
                }

                signInSubscription.unsubscribe();
            }
        }

    });
