# RabbitMQ RPC REST Client

- This tool is using for testing micro-services communication via RabbitMQ.
- The message format using for this tool is JSON.
- Using this tool together with a REST client such as Postman (*method*: **POST**).

## 1. Setting up:

* 1.0. Install Java (this tool is using **Java 8**)
* 1.1. Copy **rabbitclient.jar** + **rabbitclient.cfg.json**
* 1.2. Put both files in the same directory
* 1.3. Edit environment in **rabbitclient.cfg.json**
* 1.4. Run **rabbitclient.jar**

*(HTTP server will start and listen on port and path which is configured at step **1.3**, method: **POST**. If cannot
find the **rabbitclient.cfg.json** in the same directory as **rabbitclient.jar**, the tool will run with default
config: (path= **"/rabbitClient"**, port=**1234**))*

## 2. Build and send the request:

* 2.1. Define the queue which is about to receive this message
  *(field name: **"queue"**, must be a non-null, non-empty string)*.
* 2.2. Define the message which is about to publish to queue
  *(field name: **"data"**, must be a message in **JSON** format only)*.
* 2.3. Build request body in **JSON** format including both fields above.
* 2.4. Send the request to the HTTP server (Method: **POST**).
* 2.5. Receive the response from the HTTP server.

*(For example:  localhost:**1234**/**rabbitClient** - **POST**)*
<pre>
{
    "queue": "ha_qu_be_service_air_booking_gateway",
    "data": {
        "serviceCode": "booking_airline",
        "timezoneOffset": -420,
        "bookingType": 1,
        "departureCode": "SGN",
        "arrivalCode": "DAD",
        "departureTime": 1608944400000,
        "returnTime": 0,
        "departureCountryCode": "VN",
        "arrivalCountryCode": "VN",
        "adult": 1,
        "child": 0,
        "infant": 0,
        "listAirlinesFilter": [
            "bamboo",
            "vna"
        ],
        "extra": {}
    }
}
</pre>

## 3. How does it work ?

<pre>
______________       ________________      _____________________________      ___________________
|             | ---> |              | ---> | Producer Queue (RabbitMQ) | ---> |                 |
| REST Client |      | rabbitclient |                                         |   Test Server   |
|  (Postman)  |      |  (this tool) |      _____________________________      | (micro-service) |
|             | <--- |              | <--- | Consumer Queue (RabbitMQ) | <--- |                 |    
</pre>