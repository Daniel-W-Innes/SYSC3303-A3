This is a markdown file but I can't submit it as such because I'll lose marks.

# SYSC 3303 Assignment 3

Example code for rpc using udp.

## Important Notes
* There is no regex for strings in the request.
* UTF-8 is used for encoding strings.
* If the strings in the request too long it will raise an exception.
* See the doc folder for more documentation.

## Files
* resources non-code resources used by the program.
    - config.cfg contains the program configurations.
* src all the code for the program. 
    * actor is the actors which are exchanging messages uses rpc.
        - Client.java sends requests and prints the response.
        - intermediate is a buffer between the client and a server.
            - ClientSide.java listens for rpc calls from the client and conducts the appropriate action on the Intermediate.
            - ClientSideApi.java an interface with the methods, callable through rpc.
            - Intermediate.java handles the synchronicity between all the intermediate threads and therefore ensures the state of the request and response queues.
            - Main.java runs the entire intermediate through a single method.
            - ServerSide.java listens for rpc calls from the server and conducts the appropriate action on the Intermediate.
            - ServerSideApi.java an interface with the methods, callable through rpc.
        - Server.java receives requests and responses to them.
    * model is the date model for sending between actors. All data models have the ability to decode in encode themselves.
        - AckMessage.java is an acknowledgment message to be use in stub as the return for void functions.
        - Request.java is a model containing information to send to the server.
        - BadRequest.java is an extension of the request object with a broken encoder.
        - Message.java is a serializable alternative to optional. This is used as an alternative to status codes.
        - Response.java is a model containing information to send to the client.
        - StubRequestMessage.java is a representation of a function call containing the functions number and arguments to be use in a stub.
    * util is a collection of basic utilities for use by other classes.
        - Config.java the configuration file loader. Loads the properties from a given CONFIG_FILE_NAME.

## Running
Run main from Main.java or each actor independently.

## Questions
1. The intermediate should have at least four threads, preferably more. 
   The first two threads are responsible for receiving message on the server and client socket.
   These messages are then handled by two thread pools, ensuring the receiving threads are freed aas fast as possible.
   Thread pools were used to prevent the program from over-scaling, but allow it to handle concurrent requests.
1. It is necessary to have some form of synchronization in the intermediate because it is handling concurrent requests.
   In this implementation, the synchronization is handled two ConcurrentLinkedQueues.
   These are a thread safe, non-blocking queue and therefore allow all the concurrent messages to be handled without method level synchronization.
   
## Design Considerations
This assignment posed several design constraints that are inconsistent with modern practices. 
This resulted in a program that inefficient and inelegant. 
The first and most important of these constraints is the existence of the intermediate.
In previous iterations the intermediate acted as a form of proxy, which while inefficient made some sense.
In this iteration the intermediate only serves to break synchronicity between the client and the server.
Even through this limited use case it does not preform a satisfactory job, as without callbacks it forces the client, and the server, to actively ping it for new messages.
One possible solution to this would be to use blocking data structures in the intermediate. 
This is unsalable and largely unfeasible as it requires the intermediate to have at least one thread per client and server.
In addition, the connectionless nature of udp would result in the client timing out and resending the blocked message.
This is only the first of the unadvisable constraints placed on this code, which could be enumerated upon request.

## Author Info
Name: Daniel Innes

Student number: 101067175