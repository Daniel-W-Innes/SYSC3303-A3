This is a markdown file but I can't submit it as such because I'll lose marks.

# SYSC 3303 Assignment 3

Example code for rpc using udp.

## Important Notes

* There is no regex for strings in the request.
* UTF-8 is used for encoding strings.
* If the strings in the request too long it will raise an exception.
* See the doc folder for more documentation.

## Files

* model is the date model for sending between actors. All data models have the ability to decode in encode themselves.
    - Request.java is a model containing information to send to the server.
    - BadRequest.java is an extension of the request object with a broken encoder.
    - Response.java is a model containing information to send to the client.
* actor is the actors that exchange udp messages.
    - Client.java sends requests and prints the response.
    - Intermediate a proxy between the server and client.
        - Frontend.java is the frontend of proxy it received requests from the client.
        - LoadBalancer.java is a Round Robin load balancer for allocating requests to a backend.
        - Backend.java is the backend of the proxy it sends requests to the sever and handles the response.
        - Main.java is the main class for the proxy alone it also contains the method to run the proxy as one unit.
    - Server.java receives requests and responses to them.
* Main.java is the main class for the system as a whole it runs all the actors.

## Ports

* 25 for the intermediate
* 69 for the server

## Running

Run main from Main.java or each actor independently.

## Author Info

Name: Daniel Innes

Student number: 101067175