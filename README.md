# rif-notifier

## Quick Start

-First of all you need to set the blockchain endpoint in the application.properties of this project

-We use mysql for DB, please put your DB settings in the application.properties

-Follow next steps

###### You need to register a user

Make a call to http://localhost:8080/users?address=YOUR_ADDRESS

This endpoint will give you an ApiKey, please keep track of this Api Key cause you will need it for future calls

###### Now you need to generate a subscription to the service

Make a call to http://localhost:8080/subscribe?type=SUBSCRIPTION_TYPE

In this case you will need to set a header-param call "apiKey" with the apiKey previously stored

Currently we're only supporting subscription_type 0

When consuming this endpoint, the notifier will be creating a subscription and giving a lumino-invoice, that the user will need to pay. For development purposes, just go to the DB/subscription table, and change active to 1, and set the state column to "PAYED"

###### Now just rest to send the topics with params to be listened

Using the following structure of a contract event: 

`{
	"type": "CONTRACT_EVENT",
	"topicParams":[
		{
			"type": "CONTRACT_ADDRESS",
			"value": "0x5ea3dc5fb6f5167d5673e8a370e411cff9a4125f",
			"valueType": "string",
			"indexed": 0
		},
		{
			"type": "EVENT_NAME",
			"value": "LogSellArticle",
			"valueType": "string",
			"indexed": 0
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
			"indexed": 0
		},
		{
			"type": "EVENT_PARAM",
			"value": "price",
			"order": 2,
			"valueType": "Uint256",
			"indexed": 0
		}
	]
}`

**Notes of Json structure**
You will need to indicate the event type, that's the first param. This can be type of: CONTRACT_EVENT, NEW_TRANSACTIONS, NEW_BLOCK or PENDING_TRANSACTIONS

For CONTRACT_EVENT type:

-CONTRACT_ADDRESS param like the described before, this will indicate to the notifier that this param is the address to be listened

-EVENT_NAME param, this will be used to check that the name of the contract event is the same as the blockchain when it's called

-EVENT_PARAM, here you will need to indicate an order as described by the contract signature, please indicate when a param is indexed also. The valueType need to be a web3 accepted type.

For others types, you only need to send the Type without Params.