'use strict';

angular.module('testApp')
    .service('ChatsService', function ChatsService(ChatsFactory, InvitationsFactory, InvitationStatusFactory, UserIdentityFactory, BackendConnectionService) {

        angular.extend(this, {
            getAllPermittedChats: getAllPermittedChats,
            getAllInvitationsForUser: getAllInvitationsForUser,
            listenForNewInvitations: listenForNewInvitations,
            acceptInvitation: acceptInvitation,
            declineInvitation: declineInvitation,
            listenForNewChats: listenForNewChats,
            createChat: createChat
        });

        function getAllPermittedChats(callback) {

            var chatsSubscription = BackendConnectionService.subscribe("/user/queue/getAllPermittedChats", subscribeCallbackChats);
            BackendConnectionService.send("/getAllPermittedChats", UserIdentityFactory.user.id);

            function subscribeCallbackChats(data) {
                if (data) {
                    ChatsFactory.chats = data;
                    callback(data);
                } else {
                    alert("Problem with chats delivery!");
                }

                chatsSubscription.unsubscribe();
                ChatsFactory.setInitialized(true);
            }
        }

        function getAllInvitationsForUser(callback) {

            var invitationsSubscription = BackendConnectionService.subscribe("/user/queue/getAllInvitationsForUser", subscribeCallbackInvitations);
            BackendConnectionService.send("/getAllInvitationsForUser", UserIdentityFactory.user.id);

            function subscribeCallbackInvitations(data) {
                if (data) {
                    InvitationsFactory.invitations = data;
                    callback(data);
                } else {
                    alert("Problem with invitations delivery!");
                }

                invitationsSubscription.unsubscribe();

            }
        }

        function listenForNewInvitations() {

            var newInvSubscription = BackendConnectionService.subscribe("/topic/users/" + UserIdentityFactory.user.id + "/newInvitation", subscribeCallbackNewInv);

            function subscribeCallbackNewInv(data) {
                if (data) {
                    InvitationsFactory.invitations[data.id] = data;
                    alert("You hava a new invitation, take a look at the 'Invits' section!");
                } else {
                    alert("Problem with new invitation delivery!");
                }
            }
        }

        function acceptInvitation(userId, chatId, id, callback) {

            var acceptInvSubscription = BackendConnectionService.subscribe("/user/queue/users/" + userId + "/acceptInvitation", subscribeCallbackAcceptInv);
            BackendConnectionService.send("/acceptInvitation", { "userId": userId, "chatId": chatId, "invitationId": id });

            function subscribeCallbackAcceptInv(data) {
                if (data) {
                    InvitationsFactory.invitations[id].statusId = InvitationStatusFactory.ACCEPTED;
                    alert("Invitation accepted successfully!");
                    callback(chatId);
                } else {
                    alert("Problem with accepting invitation. You might be a member alredy!");
                }

                acceptInvSubscription.unsubscribe();

            }
        }

        function declineInvitation(userId, chatId, id) {
            var declineInvSubscription = BackendConnectionService.subscribe("/user/queue/users/" + userId + "/declineInvitation", subscribeCallbackDeclineInv);
            BackendConnectionService.send("/declineInvitation", { "userId": userId, "chatId": chatId, "invitationId": id });

            function subscribeCallbackDeclineInv(data) {
                if (data) {
                    InvitationsFactory.invitations[id].statusId = InvitationStatusFactory.DECLINED;
                    alert("Invitation declined successfully!");
                } else {
                    alert("Problem with declining invitation. You might be a member alredy!");
                }

                declineInvSubscription.unsubscribe();
            }
        }

        function listenForNewChats() {

            var newChatsSubscription = BackendConnectionService.subscribe("/topic/chats/newChat", subscribeCallbackNewChats);

            function subscribeCallbackNewChats(data) {
                if (data) {
                    ChatsFactory.chats[data.id] = data;
                } else {
                    alert("Problem with new chat delivery!");
                }
            }

            var newChatsForInvitationSubscription = BackendConnectionService.subscribe("/topic/users/" + UserIdentityFactory.user.id + "/newChatForInvitation", subscribeCallbackNewChatsForInvitation);

            function subscribeCallbackNewChatsForInvitation(data) {
                if (data) {
                    ChatsFactory.chats[data.id] = data;
                } else {
                    alert("Problem with new chat delivery!");
                }
            }

        }

        function createChat(name, summary, typeId) {

            var createChatSubscription = BackendConnectionService.subscribe("/user/queue/chats/createChat", subscribeCallbackCreateChat);
            BackendConnectionService.send("/createChat", { "name": name, "summary": summary, "typeId": typeId, "creatorId": UserIdentityFactory.user.id });

            function subscribeCallbackCreateChat(data) {
                if (data) {
                    alert("Chat successfully created!");
                } else {
                    alert("Problem with new chat delivery!");
                }

                createChatSubscription.unsubscribe();
            }
        }

    });
