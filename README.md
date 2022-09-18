# software-Product-Lines-Homework
software Product Lines Homework project(s)

# running the code

inport the projects as existing maven projects. 

run the server normally as a java application.

run the client gui using maven with the goal `clean javafx:run`
you can open many clients.

the current username/password is
admin
admin

# Assignment 2: exercise 5 explanation

## Strategy pattern for encryption

Our code includes two encryption method: AES and string reversing. 
The encryption method is chosen by the user before message is sent to the server. The default option is AES, but used can select reversing the string as encryption method.

We created two separate classes for each of the encryption method. Each of them implements the _Encryption_ interface with two functions: _encrypt_ and _decrypt_. 
These functions do a corresponding work: encrypt of decrypt the message. Each algorithm's implementation differs, but they do the same exact actions. Thus, the strategy pattern was selected for this functionality.

When the messages are received or are about to be sent, their encryption happens. The following code snippet shows that we use the same object for any of the encryption methods, because they implement the same interface. 

```java
Encryption encryptionClass;

if (encryptionAlg.equals(Algorithms.AES)) {
	encryptionClass = new AESEncryption(jsonEncryptionKey);
} else { // encryption is string reverse
	encryptionClass = new ReverseStringEncryption();
}

jsonForConnection.put(JSONKeys.ENCRYPTED_MESSAGE.toString(), encryptionClass.encrypt(message));
```

The benefit of using this pattern is in the possibility to add more encryption algorithms without changing many places in the code. The class of the new algorithm should implement the same interface and override _encrypt_ and _decrypt_ functions. Moreover, the code snipped above should include one more else-if statement that checks whether the new algorithm is the encryption method used. Other than that no more places have to be changed. 

Strategy pattern allows us to expand the list of possible encryption methods as much as we want.

## Observer pattern for the chatapp backend

We decided upon using the observer pattern to increase the seperation between the frondend and the backend. The ChatBackEnd class is an subject class which keeps listening in on communication from the server on its own thread. The frontend of the chatapp registers as an observer and is notified by the ChatBackEnd whenever a message is received.

This process works as follows: The ChatBackEnd thread loops indefinately and requests the messages from the server every time period through the ServerConnection. The ServerConnection retrieves these messages and delivers them. If there has been an update the observer is notified. The ChatWindow will then recieve these messages through a PropertyChangeEvent and update its frontend accordingly.

The advantages of using this pattern is that the frontend can spend all its resources on the rendering of the application. There is also an increased level of modularity, the frontend could be swapped out for any observer class or multiple if needed. The ChatBackEnd could for example act as an acces point to the server in a group chat, whenever a new message is recieved all of the participants can be notified.
