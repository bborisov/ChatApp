'use strict';

angular.module('testApp')
    .controller('ChatsCtrl', function ChatsCtrl($scope, $state, ChatTypeFactory, InvitationStatusFactory, ChatsService) {

        init();

        function init() {
            ChatsService.getAllPermittedChats(function getAllPermittedChatsCallback(chats) {
                $scope.chats = chats;
            });

            ChatsService.getAllInvitationsForUser(function getAllInvitationsForUserCallback(invitations) {
                $scope.invitations = invitations;
            });

            $scope.chatTypes = ChatTypeFactory;

            ChatsService.listenForNewInvitations();
            ChatsService.listenForNewChats();
        }

        $scope.getInvitationChatName = function getInvChatName(chatId) {
            return $scope.chats[chatId].name;
        }

        $scope.getInvitationStatus = function getInvStatus(statusId) {
            switch (statusId) {
                case InvitationStatusFactory.PENDING:
                    return "pending";
                    break;
                case InvitationStatusFactory.ACCEPTED:
                    return "accepted";
                    break;
                case InvitationStatusFactory.DECLINED:
                    return "declined";
                    break;
            }
        }

        $scope.isObjectEmpty = function isObjectEmpty(obj) {
            for (var prop in obj) {
                if (obj.hasOwnProperty(prop))
                    return false;
            }

            return true;
        }

        $scope.selectChat = function selectChat(id) {
            $state.go('chats.currentChat', { chatId: id });
        }

        $scope.acceptInvitation = function acceptInvitation(userId, chatId, id) {
            ChatsService.acceptInvitation(userId, chatId, id, function acceptInvitationCallback(chatId) {
                $scope.$broadcast('ChatsCtrl.accept-invitation', { chatId: chatId });
            });
        }

        $scope.declineInvitation = function declineInvitationCallback(userId, chatId, id) {
            ChatsService.declineInvitation(userId, chatId, id);
        }

        $scope.createChat = function createChat() {
            ChatsService.createChat($scope.name, $scope.summary, $scope.typeId);
        }

    });
