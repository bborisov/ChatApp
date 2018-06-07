'use strict';

angular.module('testApp')
    .controller('LoginCtrl', function LoginCtrl($scope, $state, LoginService) {

        $scope.signIn = function signIn() {
        	if (isEmailValid($scope.email) && isPasswordValid($scope.password)) {
	            LoginService.signIn($scope.email, $scope.password, function signInCallback() {
	                $state.go('chats');
	            });
	        }
        }

        function isEmailValid(email) {
        	var emailRegex = "[A-Z0-9_]+@[A-Z0-9]+[\.][A-Z]{2,4}$";

        	if (email.toUpperCase().match(emailRegex)) {
        		return true;
        	} else {
        		alert("Invalid e-mail address! Pattern: upper and lower case letters, digits or underscore followed by '@' and hostname.");
        		return false;
        	}
        }

        function isPasswordValid(password) {
        	var passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{6,}$";

        	if (password.match(passwordRegex)) {
        		return true;
        	} else {
        		alert("Invalid password! Pattern: at least one letter and one digit, special symbols like '$', '@', '!', '%', '*', '#', '?', '&' are optional. Total length no less than six characters.");
        		return false;
        	}
        }

    });
