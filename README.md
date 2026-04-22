1. Project Overview

Code Collab is a real-time collaborative web-based code editor that allows multiple users to write, edit, and execute code simultaneously in shared rooms. It uses WebSocket communication for live synchronization and integrates a cloud-based code execution API.

The system simulates a collaborative IDE environment similar to modern tools like VS Code Live Share.

2. Features
Real-time collaborative code editing
Multi-user room-based system
Live code synchronization using WebSockets
Integrated chat system
Multi-language code execution (Java, Python, C++)
Monaco Editor (VS Code-like interface)
User presence tracking


3. Technology Stack
Frontend
HTML5
CSS3
JavaScript
Monaco Editor
Backend
Java
Spring Boot
Spring WebSocket
REST API
External Services
JDoodle API (Code Execution)


4. Project Structure
code-collab/
│
├── backend/
│   ├── CodeController.java
│   ├── CodeHandler.java
│   ├── RoomManager.java
│   ├── WebSocketConfig.java
│   ├── CodeRequest.java
│
├── frontend/
│   ├── index.html
│
├── resources/
│   ├── application.properties
│
├── README.md
├── PROJECT_REPORT.docx



5. Installation Guide
5.1 Prerequisites

Install the following before running the project:

Java (JDK 17+)

Verify installation:

java -version
Maven

Verify installation:

mvn -version

If not installed:

Download Maven from: https://maven.apache.org/download.cgi
Set environment variables (MAVEN_HOME + PATH)
5.2 Backend Setup
cd code-collab/backend
mvn clean install
mvn spring-boot:run

Backend will run at:

http://localhost:8080
5.3 Frontend Setup

Simply open:

frontend/index.html

OR use Live Server in VS Code.

6. How to Use the Application
Open the frontend in browser
Enter Room ID
Enter Username
Click Join Room
Start typing code in editor
Open same room in another tab/device to collaborate
Click Run to execute code



7. API Endpoints
POST /run

Executes code using JDoodle API.

Request Body:
{
  "language": "java",
  "code": "System.out.println(\"Hello World\");"
}
Response:
Execution output or error message


8. WebSocket Events
Event Type	Description
join	User joins a room
code	Real-time code sync
chat	Chat messages
users	Active users list
system	System notifications


9. Core Modules
9.1 CodeHandler

Handles WebSocket messages and broadcasts updates to all users in a room.

9.2 RoomManager

Manages user sessions and room mapping.

9.3 CodeController

Handles /run API requests for code execution.

9.4 WebSocketConfig

Configures WebSocket endpoint /code.

10. Deployment Instructions
Local Deployment
Run backend using Maven
Open frontend in browser
Cloud Deployment (Optional)
Backend: Railway / Render / AWS
Frontend: Netlify / Vercel
Update BASE_URL in frontend


11. Common Issues & Fixes
1. Code not syncing
Ensure WebSocket connection is active
Check room ID is same in both tabs
2. Execution error
Verify JDoodle API credentials
Check network connectivity
3. Backend not starting
Check Java and Maven installation
Ensure port 8080 is free


12. Limitations
No authentication system
No database persistence
External API dependency for execution
Basic UI design


13. Future Enhancements
User login system (Spring Security)
Database integration (MySQL/MongoDB)
File saving system
Voice/video collaboration
Git integration
AI code suggestions



14. Conclusion

Code Collab demonstrates a scalable architecture for real-time collaborative coding using WebSockets and cloud execution APIs. It provides a foundation for building advanced collaborative development environments.