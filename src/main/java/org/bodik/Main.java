package org.bodik;

import java.awt.*;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static Robot robot;
    private static long lastActivityTime;
    private static final long WORKING_DAY_START = 9;
    private static final long WORKING_DAY_END = 18;
    private static final long INACTIVITY_THRESHOLD = 300000; // 5 minutes in milliseconds
    private static final long MOVEMENT_INTERVAL = 5000; // Move the mouse every 10 seconds after inactivity (in ms)
    private static Timer movementTimer;
    private static int lastMousePositionX; // Track the last mouse position
    private static int lastMousePositionY; // Track the last mouse position

    public static void main(String[] args) {
        try {
            // Create a Robot instance to control the mouse
            robot = new Robot();
            // Initialize last activity time (current time)
            lastActivityTime = System.currentTimeMillis();
            // Initialize last mouse position to current mouse position
            Point location = MouseInfo.getPointerInfo().getLocation();
            lastMousePositionX = location.x;
            lastMousePositionY = location.y;
            // Set up the mouse and keyboard listeners
            setupListeners();
            // Start a timer to check for inactivity every second
            Timer inactivityChecker = new Timer();
            inactivityChecker.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkInactivity();
                }
            }, 0, 1000); // Check inactivity every 1 second
            System.out.println("Program is running. Waiting for inactivity...");
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void setupListeners() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event ->
                resetInactivityTimer(""), AWTEvent.ACTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    private static void resetInactivityTimer(String msg) {
        lastActivityTime = System.currentTimeMillis(); // Reset the activity timer
        // Stop the mouse movement if it's active
        if (movementTimer != null) {
            movementTimer.cancel(); // Stop the movement timer
            movementTimer = null; // Set it to null to indicate that it's stopped
            System.out.println(msg);
        }
    }

    // Method to check if the current time is within working hours (9 AM - 6 PM)
    private static boolean isWithinWorkingHours() {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY); // Get current hour (24-hour format)
        return currentHour >= WORKING_DAY_START && currentHour < WORKING_DAY_END; // Check if current time is between 9 AM and 6 PM
    }

    // Check if the user has been inactive for too long
    private static void checkInactivity() {
        if (isWithinWorkingHours()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastActivityTime;
            // Get the current mouse position
            Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();
            // If the mouse position has changed, reset the inactivity timer
            if (currentMousePosition.x != lastMousePositionX && currentMousePosition.y != lastMousePositionY) {
                resetInactivityTimer("User became active. Mouse movement disabled.");
                lastMousePositionX = currentMousePosition.x;
                lastMousePositionY = currentMousePosition.y;
                return; // Exit early since we detected activity
            }

            // If inactivity time exceeds the threshold start simulating mouse movements
            if (elapsedTime >= INACTIVITY_THRESHOLD && movementTimer == null) {
                // Start the movement timer if not already started
                movementTimer = new Timer();
                movementTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        simulateMouseMovement();
                    }
                }, 0, MOVEMENT_INTERVAL); // Simulate mouse movement every 10 seconds
                System.out.println("User inactive for " + INACTIVITY_THRESHOLD / 60000 + " minutes. Mouse movement started.");
            }
        } else {
            resetInactivityTimer("Its not working hours. Mouse movement disabled.");
        }
    }

    // Method to simulate user-like mouse movement
    private static void simulateMouseMovement() {
        // Get the current mouse position
        Point currentPosition = MouseInfo.getPointerInfo().getLocation();

        // Simulate user-like mouse movement by adding small random changes
        Random random = new Random();
        int deltaX = random.nextInt(20) - 10; // Random movement between -10 and 10
        int deltaY = random.nextInt(20) - 10; // Random movement between -10 and 10

        // Calculate the new position based on the random deltas
        int newX = currentPosition.x + deltaX;
        int newY = currentPosition.y + deltaY;

        // Ensure the new position is within screen bounds (prevent going outside the screen)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        newX = Math.max(0, Math.min(screenSize.width - 1, newX));
        newY = Math.max(0, Math.min(screenSize.height - 1, newY));
        lastMousePositionX = newX;
        lastMousePositionY = newY;
        // Move the mouse to the new position
        robot.mouseMove(newX, newY);
    }
}