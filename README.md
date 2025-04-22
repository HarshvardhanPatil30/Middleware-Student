# Student Management System

A Java-based Student Management System that implements Remote Method Invocation (RMI) with a Swing GUI frontend. This application demonstrates client-server architecture using Java RMI and provides CRUD operations for managing student data.

## Project Structure

### Core Files

| File                           | Description                                                                                                                                                                                                                               |
| ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Student.java`                 | Data model class that represents a student entity. Implements `Serializable` to enable transfer over RMI connections. Contains student properties such as ID, name, age, course, and grade along with getters and setters.                |
| `StudentService.java`          | RMI interface that defines the contract for remote operations. Extends `Remote` interface to mark it as an RMI interface. Defines methods for adding, updating, deleting, and retrieving student records that can be called remotely.     |
| `StudentServiceImpl.java`      | Implementation of the `StudentService` interface. Extends `UnicastRemoteObject` to make it available for RMI operations. Contains the actual business logic for managing student records and maintains an in-memory database of students. |
| `StudentServer.java`           | Server application that registers the RMI service. Creates and binds the `StudentServiceImpl` to the RMI registry, making it available for client connections on port 1099.                                                               |
| `StudentManagementClient.java` | Swing-based client application that provides a GUI for interacting with the student management system. Connects to the RMI server and presents a user interface for viewing and manipulating student data.                                |
| `main.java`                    | Entry point for the client application that launches the Swing GUI.                                                                                                                                                                       |

## How RMI is Used in This Project

Remote Method Invocation (RMI) is used to enable communication between the client (Swing frontend) and the server (data management backend):

1. **Interface Definition**: `StudentService.java` defines the remote interface that extends `java.rmi.Remote`, declaring methods that can be invoked remotely.

2. **Server Implementation**: `StudentServiceImpl.java` implements the remote interface and extends `UnicastRemoteObject` to make its methods callable from remote clients.

3. **Server Registration**: `StudentServer.java` creates an instance of the implementation and registers it with the RMI registry using `registry.rebind("StudentService", service)`, making it discoverable by clients.

4. **Client Connection**: `StudentManagementClient.java` connects to the RMI registry with `LocateRegistry.getRegistry("localhost", 1099)` and looks up the service with `registry.lookup("StudentService")` to get a reference to the remote object.

5. **Remote Method Calls**: The client invokes methods on the remote object as if it were local, while RMI handles the network communication, parameter marshalling, and exception handling.

## How to Run the Application

1. **Compile all Java files:**

   ```
   javac *.java
   ```

2. **Start the RMI Server:**

   ```
   java StudentServer
   ```

   You should see a message: "Student Management Server is running..."

3. **Run the Client Application:**
   ```
   java main
   ```

**Important**: Always start the server before the client to ensure proper connection.

## Features

- View all student records in a table format
- Add new students with validation for all inputs
- Update existing student information
- Delete students from the system
- Automatic refresh of data after operations
