# Anti-AFK Application

This project is a Java-based Anti-AFK (Away From Keyboard) application that simulates user activity by moving the mouse during periods of inactivity. It is designed to work within specified working hours and provides efficiency tracking based on user activity.

## Features

- Simulates mouse movements after a period of inactivity.
- Tracks user activity and calculates efficiency during working hours.
- Configurable working hours and inactivity thresholds.
- Provides user efficiency labels based on activity percentage.

## Requirements

- **Java 8 or higher**
- **Gradle** (for building the project)

## Configuration

You can configure the following parameters in the source code:

- **Working Hours**: Modify `START_WORK` and `END_WORK` in `AntiAFKApp.java`.
- **Inactivity Threshold**: Adjust `IDLE_THRESHOLD` for inactivity duration.
- **Mouse Movement Interval**: Change `MOVEMENT_INTERVAL` for the frequency of simulated mouse movements.

## Usage

1. Run the application.
2. The program will monitor user activity and simulate mouse movements during inactivity within working hours.
3. Efficiency statistics will be displayed in the console after working hours.

## Build and Run

Build the JAR file:
   ```bash
    gradle shadowJar
   ```
Run the JAR file:
   ```bash
    java -jar build/libs/Dragger-1.0.jar
   ```