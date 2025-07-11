# Tic Tac Toe Game

---
Greetings, my name is Muhammad Shiddiq Fizuhri, and this is my web-based implementation of the classic **Tic Tac Toe** game built using **Java Spring Boot** for the backend and **Vaadin** for the frontend UI.

## Features
### Core Functionality

----
- Play **Tic Tac Toe** with customizable settings.
- Configurable board size (e.g., 3x3, 5x5, 9x9, etc.) and its winning streak length.
- Playable in **Single Player Mode** (vs AI/Computer) or **Multiplayer Mode** (two players).
- Tracks game progress, winner, or if the game ends in a draw.
- Implements **soft delete** functionality:
    - Deleted games are excluded from the UI but retained in the database.

### Architecture and Best Practices

---
- **Object-Oriented Approach**: The entire project leverages OOP principles for modularity and extensibility.
    - Example classes: `Game`, `GameConfig`, `TicTacToeService`, and `TicTacToeServiceImpl`. 

- **Separation of Concerns**:
    - ViewModels that handles UI rendering.
    - Services that focuses on business logic.
    - Repositories manages database interactions.

- **Soft Delete Handling**:
    - Instead of physically deleting records, the `isDeleted` flag ensures "deleted" games remain loggable and auditable.

- **Dynamic Game Configuration**:
    - No hardcoded values for board size, winning streak, or player settings.
    - Configurable during game creation through the UI fields.

- **Readability and Maintainability**:
    - Clear class and method naming conventions.
    - Logging for easier debugging and monitoring.

## Deploying and Running the Application

---

There are three method to run and deploy this application and to play the game open http://localhost:8080/ on your 
browser after the application has started.

### Import to IDE

---
Import this project into an IDE, i.e., IntelliJ IDEA or VSCode, and then directly fill the environment variables
on `application.properties`.
Then you can directly execute the application using the IDE run buttons.
Be sure to have **PostgreSQL** already installed on your machine.

### Using Docker

---
If you already have **PostgreSQL** installed on your machine and docker already installed, you can execute the
application using the `run_app_with_docker.sh` script.
Before running this script, be sure to create and fill the `.env` file first (you can copy the variable keys from `.env.example` file).
If your 
**PostgreSQL** is running on the 
same
machine with this application, you can fill the `.env` file like this:
```dotenv
APP_NAME=tic-tac-toe-game
DB_HOST=host.docker.internal
DB_PORT=5432
DB_NAME=mydb
DB_USER=user
DB_PASS=password
```

### Using Docker Compose

---
If you don't have **PostgreSQL** installed on your machine, but docker does, then you can execute the application
using the `run_app_with_docker_compose.sh` script.
The `docker-compose` will create a **PostgreSQL** database for you.
Before executing this script, remember to fill the `.env` file first.
You can fill it like the example above.

## SQL

---
The SQL files for the database are located in `/sql` folder.

## Usage Instruction

### Starting a New Game 

---
1. Click the `Start New Game` button on the home page.
2. Configure game settings (board size, winning streak, single/multi-player).
3. Click `Start Game` to begin playing.

### Continuing or Opening a Game

---
- To continue a game, click any game record with `ongoing` status. Then you will be redirected to the the game 
   board page.
   The table will always show the last modified ongoing games on the top.
- To open a finished game, click any game record with `over` status. Then you will also be redirected to the game 
   board page.

### Deleting a Game

---
- Games can be deleted by using the `Delete` button next to each game record in the table.
- Confirm the deletion via the modal pop-up dialog. Deleted games are excluded from the UI but remain in the 
  database for audit purposes.

## Example Game Scenarios

### Single Player Mode

---

- Player 1 (human) vs Player 2 (AI).
- AI moves are chosen randomly from the available cells.

### Multiplayer Mode

---

- Player 1 vs Player 2
- Each player's move are recorded in the database by the backend and reflected on the UI in real-time.