# Telemedicine System: Essential Tremor

## Description
This project implements a telemedicine system allowing users, both patients and doctors, to interact with a central server to manage medical data. The system includes:
- **Server**: Handles communication and medical data storage.
- **Clients**: Interfaces for patients and doctors.

## Execution Instructions
### **1. Prerequisites**
- **Java installed**: Ensure you have the Java Runtime Environment (JRE) or Java Development Kit (JDK) installed on your system. Verify installation with the command java -version
- Required `.jar` files for each component:
  - `EssenetialTremor_Server.jar` for the server.
  - `EssentialTremor_Client.jar` for the patient client.
  - `EssentialTremor_Doctor.jar` for the doctor client.
### **2. Running the server**
1. Navigate to the directory containing the `EssentialTremor_Server.jar` file.
2. Execute the following command in your terminal: java -jar EssentialTremor_Server.jar
The server will now start listening for client connections.
### **3. Running the clients**
1. Once the server is running, launch the clients:
   - For the patient client: java -jar EssentialTremor_Client.jar
   - For the doctor client: java -jar EssentialTremor_Doctor.jar
2. **Specify the IP Address**:
   - If the client and server are on the same machine, use localhost as the IP address.
   - If the client is on another computer connected to the same network as the server, obtain the IP address as follows:
     - On Windows, use the command: ipconfig
     - On Linux/Mac, use the command: ifconfig
     - Enter the IP address shown under the network configuration.
       
### **Example execution**
1. **Server**: java -jar EssenetialTremor_Server.jar
2. **Patient Client**: java -jar EssentialTremor_Client.jar
3. **Doctor Client**: java -jar EssentialTremor_Doctor.jar
