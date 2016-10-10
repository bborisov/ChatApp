'use strict';

angular.module('testApp')
    .service('CurrentChatService', function CurrentChatService(MembersFactory, MemberRoleFactory, MessagesFactory, UserIdentityFactory, BackendConnectionService) {

        angular.extend(this, {
            getAllMembersFromChat: getAllMembersFromChat,
            getAllMessagesFromChat: getAllMessagesFromChat,
            listenForNewMembers: listenForNewMembers,
            joinChat: joinChat,
            inviteToChat: inviteToChat,
            listenForKickingUsers: listenForKickingUsers,
            kick: kick,
            listenForPromotingUsers: listenForPromotingUsers,
            promote: promote,
            listenForNewMessages: listenForNewMessages,
            sendMessage: sendMessage,
            listenForEditingMessages: listenForEditingMessages,
            editMessage: editMessage
        });

        function getAllMembersFromChat(chatId, callback) {

            var membersSubscription = BackendConnectionService.subscribe("/user/queue/getAllMembersFromChat", subscribeCallbackMembers);
            BackendConnectionService.send("/getAllMembersFromChat", chatId);

            function subscribeCallbackMembers(data) {
                if (data) {
                    MembersFactory.members = data;
                    callback(data);
                } else {
                    alert("Problem with members delivery!");
                }

                membersSubscription.unsubscribe();
            }
        }

        function getAllMessagesFromChat(chatId, callback) {

            var messagesSubscription = BackendConnectionService.subscribe("/user/queue/getAllMessagesFromChat", subscribeCallbackMessages);
            BackendConnectionService.send("/getAllMessagesFromChat", chatId);

            function subscribeCallbackMessages(data) {
                if (data) {
                    MessagesFactory.messages = data;
                    callback(data);
                } else {
                    alert("Problem with messages delivery!");
                }

                messagesSubscription.unsubscribe();
            }
        }

        function listenForNewMembers(chatId) {

            var newMemberSubscription = BackendConnectionService.subscribe("/topic/chats/" + chatId + "/newUserInChat", subscribeCallbackNewMember);

            function subscribeCallbackNewMember(data) {
                if (data) {
                    MembersFactory.members[data.id] = data;
                } else {
                    alert("Problem with new member delivery!");
                }
            }
        }

        function joinChat(chatId, callback) {

            var joinSubscription = BackendConnectionService.subscribe("/user/queue/chats/" + chatId + "/joinChat", subscribeCallbackJoin);
            BackendConnectionService.send("/joinChat", { "chatId": chatId, "userId": UserIdentityFactory.user.id });

            function subscribeCallbackJoin(data) {
                if (data) {
                    alert("Successfully joined!");
                    callback();
                } else {
                    alert("Problem with joining!");
                }

                joinSubscription.unsubscribe();
            }
        }

        function inviteToChat(chatId, email) {

            var invitationSubscription = BackendConnectionService.subscribe("/user/queue/chats/" + chatId + "/inviteToChat", subscribeCallbackInvitation);
            BackendConnectionService.send("/inviteToChat", { "email": email, "chatId": chatId, "invitorId": UserIdentityFactory.user.id });

            function subscribeCallbackInvitation(data) {
                if (data) {
                    alert("User invited successfully!");
                } else {
                    alert("Problem with invitation!");
                }

                invitationSubscription.unsubscribe();
            }
        }

        function listenForKickingUsers(chatId) {

            var kickingSubscription = BackendConnectionService.subscribe("/topic/chats/" + chatId + "/userKicked", subscribeCallbackKicking);

            function subscribeCallbackKicking(data) {
                if (data) {
                    delete MembersFactory.members[data];
                } else {
                    alert("Problem with kicked user!");
                }
            }
        }

        function kick(userId, chatId, doerRoleId) {

            var kickSubscription = BackendConnectionService.subscribe("/user/queue/chats/" + chatId + "/kickFromChat", subscribeCallbackKick);
            BackendConnectionService.send("/kickFromChat", { "userId": userId, "chatId": chatId, "doerRoleId": doerRoleId });

            function subscribeCallbackKick(data) {
                if (data) {
                    alert("User kicked successfully!");
                } else {
                    alert("Problem with kicking user!");
                }

                kickSubscription.unsubscribe();
            }
        }

        function listenForPromotingUsers(chatId) {

            var promotingSubscription = BackendConnectionService.subscribe("/topic/chats/" + chatId + "/newAdmin", subscribeCallbackPromoting);

            function subscribeCallbackPromoting(data) {
                if (data) {
                    MembersFactory.members[data].roleId = MemberRoleFactory.ADMIN;
                } else {
                    alert("Problem with promoted user!");
                }
            }
        }

        function promote(userId, chatId, doerRoleId) {

            var promoteSubscription = BackendConnectionService.subscribe("/user/queue/chats/" + chatId + "/makeAdmin", subscribeCallbackPromote);
            BackendConnectionService.send("/makeAdmin", { "userId": userId, "chatId": chatId, "doerRoleId": doerRoleId });

            function subscribeCallbackPromote(data) {
                if (data) {
                    alert("User promoted successfully!");
                } else {
                    alert("Problem with promoting user!");
                }

                promoteSubscription.unsubscribe();
            }
        }

        function listenForNewMessages(chatId) {

            var newMessagesSubscription = BackendConnectionService.subscribe("/topic/chats/" + chatId + "/messages/newMessageInChat", subscribeCallbackNewMessages);

            function subscribeCallbackNewMessages(data) {
                if (data) {
                    MessagesFactory.messages[data.id] = data;
                } else {
                    alert("Problem with receiving message!");
                }
            }
        }

        function sendMessage(chatId, yourMessage) {

            var sendMessageSubscription = BackendConnectionService.subscribe("/user/queue/chats/" + chatId + "/messages/sendMessage", subscribeCallbackSendMessage);
            BackendConnectionService.send("/sendMessage", { "senderId": UserIdentityFactory.user.id, "chatId": chatId, "content": yourMessage });

            function subscribeCallbackSendMessage(data) {
                if (data) {
                    //success
                } else {
                    alert("Problem with sending message!");
                }

                sendMessageSubscription.unsubscribe();
            }
        }

        function listenForEditingMessages(chatId) {

            var editingSubscription = BackendConnectionService.subscribe("/topic/chats/" + chatId + "/messages/editedMessageInChat", subscribeCallbackEditing);

            function subscribeCallbackEditing(data) {
                if (data) {
                    MessagesFactory.messages[data.id] = data;
                } else {
                    alert("Problem with receiving editted message!");
                }
            }
        }

        function editMessage(chatId, messageId, newMessage) {

            var editMessageSubscription = BackendConnectionService.subscribe("/user/queue/chats/" + chatId + "/messages/" + messageId + "/editMessage", subscribeCallbackEditMessage);
            BackendConnectionService.send("/editMessage", { "chatId": chatId, "messageId": messageId, "content": newMessage });

            function subscribeCallbackEditMessage(data) {
                if (data) {
                    //success
                } else {
                    alert("Problem with editing message!");
                }

                editMessageSubscription.unsubscribe();
            }
        }

    });
