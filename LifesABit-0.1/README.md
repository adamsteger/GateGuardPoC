# Gate Guard

### First-time setup instructions
1. Ensure that you have `npm` and `maven` installed
2. Clone the repository, and `cd` into the `gate-guard` directory.
3. Run `mvn package`
4. `cd` into the frontend directory and run `npm install`

### Build instructions
- To build backend changes, simply run `mvn spring-boot:run` in the `gate-guard` directory again.
  - If you are already running `mvn spring-boot:run`, kill the existing process and start it again.
- To build frontend changes, leave the npm daemon running. It will automatically re-compile and re-deploy after you make changes.

### Run instructio
- Backend: Run `mvn spring-boot:run` in the `gate-guard` directory
- Frontend: Run `npm run start` in the `frontend` directory
- The website can be accessed at `localhost:3000`

### Connect to the Virtual Linux Server Enviroment via SSH
- In a terminal do the command: ssh lab@146.190.222.5
- Enter the password: tooR1Root.
- This will ssh you into the virtual enviroment.
