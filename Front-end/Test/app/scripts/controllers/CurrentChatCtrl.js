'use strict';

angular.module('testApp')
    .controller('CurrentChatCtrl', function CurrentChatCtrl($scope, $stateParams, ChatsFactory, ChatTypeFactory, MembersFactory, MemberRoleFactory, UserIdentityFactory, CurrentChatService) {

        var chatId = $stateParams.chatId;
        var lastTms;
        var todayTms = new Date().setHours(0, 0, 0, 0);

        ChatsFactory.getInitialized().then(function() {
            init();
        });

        function init() {

            //get chat info
            $scope.currentChat = ChatsFactory.chats[chatId];

            //when accept reinit to load everything
            onAcceptInvitation();

            CurrentChatService.getAllMembersFromChat(chatId, function getAllMembersFromChatCallback(members) {

                //load members and messages
                if (isMember()) {
                    $scope.members = members;

                    CurrentChatService.getAllMessagesFromChat(chatId, function getAllMessagesFromChatCallback(messages) {
                        $scope.messages = messages;
                    });


                    CurrentChatService.listenForNewMembers(chatId);
                    CurrentChatService.listenForKickingUsers(chatId);
                    CurrentChatService.listenForPromotingUsers(chatId);
                    CurrentChatService.listenForNewMessages(chatId);
                    CurrentChatService.listenForEditingMessages(chatId);
                }
            });
        }

        function onAcceptInvitation() {
            $scope.$on('ChatsCtrl.accept-invitation', function(event, args) {

                var eventChatId = args.chatId;

                if (eventChatId == chatId) {
                    init();
                }
            });
        }

        $scope.isMember = isMember;

        function isMember() {
            if (MembersFactory.members[UserIdentityFactory.user.id]) {
                return true;
            }
        }

        $scope.isAdmin = isAdmin;

        function isAdmin() {
            if (isMember()) {
                if (MembersFactory.members[UserIdentityFactory.user.id].roleId == MemberRoleFactory.ADMIN) {
                    return true;
                }
            }
        }

        $scope.getChatType = function getChatType() {
            switch ($scope.currentChat.typeId) {
                case ChatTypeFactory.PUBLIC:
                    return "public";
                    break;
                case ChatTypeFactory.PRIVATE:
                    return "private";
                    break;
            }
        }

        $scope.getMembersCount = function getMembersCount() {
            var counter = 0;

            for (var prop in MembersFactory.members) {
                if (MembersFactory.members.hasOwnProperty(prop))
                    counter++;
            }

            return counter;
        }

        $scope.getUserName = function getUserName(id) {
            return MembersFactory.members[id].name;
        }

        $scope.showMessageDate = function showMessageDate(createTms) {
            var messageDayTms = new Date(createTms).setHours(0, 0, 0, 0);

            if (messageDayTms !== lastTms) {
                lastTms = messageDayTms;
                return true;
            } else {
                lastTms = messageDayTms;
                return false;
            }
        }

        $scope.showEditMessageButton = function showEditMessageButton(senderId, createTms) {
            var messageDayTms = new Date(createTms).setHours(0, 0, 0, 0);

            if (senderId == UserIdentityFactory.user.id && todayTms === messageDayTms) {
                return true;
            } else {
                return false;
            }
        }

        $scope.isMessageMine = function isMessageMine(senderId) {
            if (senderId == UserIdentityFactory.user.id) {
                return true;
            } else {
                return false;
            }
        }

        $scope.joinChat = function joinChat() {
            if (isMember()) {
                alert('You are already a member!');
            } else {
                CurrentChatService.joinChat(chatId, function joinChatCallback() {
                    init();
                });
            }
        }

        $scope.handleMessage = function handleMessage() {
            if ($scope.yourMessage === '') {
                //just pass
            } else {
                if ($scope.editMode) {
                    CurrentChatService.editMessage(chatId, $scope.messageId, $scope.yourMessage);
                    $scope.editMode = false;
                } else {
                    CurrentChatService.sendMessage(chatId, $scope.yourMessage);
                }

                $scope.yourMessage = '';
            }
        }

        $scope.prepareForEditing = function prepareForEditing(messageId, content) {
            $scope.editMode = true;
            $scope.messageId = messageId;
            $scope.yourMessage = content;
        }

        $scope.kick = function kick(userId, memberRoleId) {
            if (!isAdmin()) {
                alert("You are not an admin!");
            } else if (memberRoleId == MemberRoleFactory.ADMIN) {
                alert("This user is also an admin. You cannot kick him/her!");
            } else {
                CurrentChatService.kick(userId, chatId, MemberRoleFactory.ADMIN);
            }
        }

        $scope.promote = function promote(userId, memberRoleId) {
            if (!isAdmin()) {
                alert("You are not an admin!");
            } else if (memberRoleId == MemberRoleFactory.ADMIN) {
                alert("This user is already an admin. You cannot promote him/her!");
            } else {
                CurrentChatService.promote(userId, chatId, MemberRoleFactory.ADMIN);
            }
        }

        $scope.inviteToChat = function inviteToChat() {
            var isMember = false;

            for (var i = 0; i < MembersFactory.members.length; i++) {
                if (MembersFactory.members[i].email === $scope.email) {
                    isMember = true;
                }
            }

            if (isMember) {
                alert("This user is already a member!");
            } else {
                CurrentChatService.inviteToChat(chatId, $scope.email);
            }
        }

    });
