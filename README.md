# rif-notifier

## Quick Start

-First of all you need to set the blockchain endpoint in the application.properties of this project

-To subscribe to Events like Open Channel or Close Channel, there's a property that needs the Token Network Registry Address to be setted in the application.properties

-We use mysql for DB, please put your DB settings in the application.properties too

-You have the DB schema in src/main/resources/db_dumps/, look for the latest Dump.sql, create a DB with this schema, and in application.properties set the connection to your DB

-Get started with the following steps

###### You need to register a user

Make a POST call to http://localhost:8080/users?address=YOUR_ADDRESS

You will need to send YOUR_ADDRESS signed in the body as text/plain, so the notifier can validate that you own this address

This endpoint will give you an ApiKey, please keep track of this Api Key cause you will need it for future calls

Newest implementations, if you re-send this, it will return the ApiKey, same as you register for the first time.

###### Now you need to generate a subscription to the service

Make a POST call to http://localhost:8080/subscribe?type=SUBSCRIPTION_TYPE

In this case you will need to set a header-param call "apiKey" with the apiKey previously stored

//Not implemented yet

//SUBSCRIPTION_TYPE will indicate how many notifications you can recieve. So when selecting one, will be giving you a notification balance.

//When consuming this endpoint, the notifier will be creating a subscription and giving a lumino-invoice, that the user will need to pay. For development purposes, right now it's creating a subscription with MAX_INT

###### Now just rest to send the topics with params to be listened

Make a POST call to http://localhost:8080/subscribeToTopic

*Remember using the apiKey header param.

Using the following structure of a contract event (In this example we're using example values from a contract, so feel free to change them): 

`{ 
	"type": "CONTRACT_EVENT", 
	"topicParams":[ 
		{	
			"type": "CONTRACT_ADDRESS", 
			"value": "0xf4af6e52b1bcbbe31d1332eb32d463fb10bded27"
		}, 
		{ 
			"type": "EVENT_NAME", 
			"value": "LogSellArticle"
		}, 
		{ 
			"type": "EVENT_PARAM", 
			"value": "seller", 
			"order": 0, 
			"valueType": "Address", 
			"indexed": 1
		}, 
		{ 
			"type": "EVENT_PARAM",
			"value": "article", 
			"order": 1, 
			"valueType": "Utf8String", 
			"indexed": 0,
			"filter": "iphone x"
		}, 
		{ 
			"type": "EVENT_PARAM", 
			"value": "price", 
			"order": 2, 
			"valueType": "Uint256",
			"indexed": 0,
			"filter": "1000"
		} 
	]
}`

**Notes of Json structure**
You will need to indicate the event type, that's the first param. This can be type of: CONTRACT_EVENT, NEW_TRANSACTIONS, NEW_BLOCK

For CONTRACT_EVENT type, you'll need to send some params that are required:

-CONTRACT_ADDRESS param like the described before, this will indicate to the notifier that this param is the address to be listened. *It is required.

-EVENT_NAME param, this will be used to check that the name of the contract event is the same as the blockchain when it's called. *It is required.

-EVENT_PARAM, here you will need to indicate an order as described by the contract signature, please indicate when a param is indexed also. The valueType need to be a web3 accepted type. Not required, in case the event doest have one, dont send this.

--EVENT_PARAM is composed of some attributes, "value" indicates the name of the event parameter, "order" is for the order of the param that appears in the event, this will be used to filter the data. "valueType" is used to create the event listener in rif-notification, so it needs to be a valid web3 type, "indexed" is used to indicate if the param is indexed, default value is 0, and we add a "filter" param, so you can use it to filtering the data you want to retrieve from the event.

For others types, you only need to send the Type without Params.


###### Getting notifications

When you're subscribed to topics, and a event is triggered the notifier will be processing the data, and saving it so you can access to that.

Try making a GET call to http://localhost:8080/getNotifications with your apiKey in the header param, you will see the data you subscribed to.

Each notification that's prepared to you, will consume 1 of the subscription balance, so remember to always have balance in your subscription.

Params: 

	-idTopic: The notifications will be filtered with this param, so it brings only the idTopics associated with each, you can send lots of ids, separating each with commas: 12,15,21
	-fromId: Each notification has an id, you can make a greater than by providing this param
	-lastRows: With this param you can set how much notifications will the notifier return. MAX is setted in applications.properties at 1000, so this number need to less than that


###### Unsubscribing from a Topic

To make this operation, just POST call: http://localhost:8080/unsubscribeFromTopic?idTopic=ID_TOPIC

Remember of putting your apiKey in the header.

###### Other available endpoints

GET_CALL: /getSubscriptionInfo - Brings the data associated with your subscription (Notification_Balance, Topics subscribed with params, etc)

GET_CALL: /getLuminoTokens - Brings an array of Token Network Address for the tokens registered in the blockchain, it can be used in other endpoints to subscribe to OpenChannels for the token or Close Channel events.

POST_CALL: /subscribeToOpenChannel - Accepts 3 params, Token (required) you need to provide a valid token network address, so the notifier can listen to the events for that specific token. participantone, is the param in the event of Open-Channel ("Who's oppening a channel"), if sended the notifier will filter the event. Also you can provide a participanttwo param to indicate when someone is oppening a channel against that address.

POST_CALL: /subscribeToCloseChannel - Similar to subscribeToOpenChannel, accepts 3 params, Token (required), closingParticipant (Who's closing the channel), and a channelidentifier (Id of the channel). This last 2 params are not required.

POST_CALL: /subscribeToLuminoOpenChannels - This endpoint subscribes you to all tokens, and returns an array of topic id, each topic will represent a event for each token. Also this endpoint accepts params for participantone and participanttwo, if sended, will filter all the topics.