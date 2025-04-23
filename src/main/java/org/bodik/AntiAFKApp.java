package org.bodik;

import com.github.kwhat.jnativehook.GlobalScreen;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AntiAFKApp {

    private static final long MOVEMENT_INTERVAL = 10000; // Move the mouse every 10 seconds after inactivity (in ms)
        private static final int RANDOM_END_DAY_TIME = 10;
//    private static final int RANDOM_END_DAY_TIME = 1;
        private static final Duration IDLE_THRESHOLD = Duration.ofMinutes(5);
//    private static final Duration IDLE_THRESHOLD = Duration.ofSeconds(5);
    private static final LocalTime START_WORK = LocalTime.of(9, 0);
    private static final LocalTime END_WORK = LocalTime.of(18, 0);
    private static Robot robot;
    public static final AtomicBoolean isRunning = new AtomicBoolean(false);
    public static final AtomicBoolean isDragging = new AtomicBoolean(false);
    public static volatile Instant lastActivity = Instant.now();
    private static boolean firstRun = true;
    private static long userNotActiveTimer = 0;
    private static long userNotActiveTime = 0;
    private static long endRandomTime = 0;

    public static void main(String[] args) {
        try {
            // Register global hooks
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalInputListener());
            GlobalScreen.addNativeMouseListener(new GlobalInputListener());
            GlobalScreen.addNativeMouseMotionListener(new GlobalInputListener());
            // Create a Robot instance to control the mouse
            robot = new Robot();
            Timer movementTimer = new Timer();
            movementTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (isWithinWorkingHours()) {
                        firstRun = false;
                        Duration idleTime = Duration.between(lastActivity, Instant.now());
                        // If inactivity time exceeds the threshold start simulating mouse movements
                        if (idleTime.compareTo(IDLE_THRESHOLD) > 0) {
                            if (isRunning.compareAndSet(false, true)) {
                                System.out.println(LocalDateTime.now() + " Its working hours. Mouse movement enabled.");
                                userNotActiveTimer = System.currentTimeMillis();
                                endRandomTime = new Random().nextInt(RANDOM_END_DAY_TIME);
                            }
                            simulateMouseMovement();
                        }
                    } else if (isRunning.compareAndSet(true, false)) {
                        System.out.println(LocalDateTime.now() + " Its not working hours. Mouse movement disabled.");
                        if (!firstRun) {
                            long periodInMillis = Duration.between(START_WORK, END_WORK).toMillis();
                            double percentage = ((double) userNotActiveTime / periodInMillis) * 100;
                            System.out.println("User was not active for " + userNotActiveTime / 60000 + " minutes. Efficiency: " + (100 - percentage) + "%");
                            System.out.println(getEfficiencyLabelEn(percentage));
                            userNotActiveTime = 0;
                        }
                    }
                }
            }, 0, MOVEMENT_INTERVAL); // Simulate mouse movement every 10 seconds
            if (isWithinWorkingHours())
                System.out.println(LocalDateTime.now() + " Its working hours. Mouse movement enabled.");
            else
                System.out.println(LocalDateTime.now() + " Its not working hours. Mouse movement disabled.");
        } catch (Exception e) {
            System.out.println("Error initializing the program: " + e.getMessage());
        }
    }

    public static void resetInactivityTimer() {
        if (!isDragging.get()) {
            lastActivity = Instant.now(); // Reset the activity timer
            if (isRunning.compareAndSet(true, false)) {
                System.out.println(LocalDateTime.now() + " User activity detected. Mouse movement suspended.");
                userNotActiveTime += System.currentTimeMillis() - userNotActiveTimer;
            }
        }
    }

    // Method to check if the current time is within working hours (9 AM - 6 PM)
    private static boolean isWithinWorkingHours() {
        LocalTime nowTime = LocalTime.now();
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        boolean isWorkingDay = today != DayOfWeek.SATURDAY && today != DayOfWeek.SUNDAY;
        return isWorkingDay && nowTime.isAfter(START_WORK) && nowTime.isBefore(END_WORK.plusMinutes(endRandomTime));
    }

    private static void simulateMouseMovement() {
        isDragging.set(true);
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
        // Move the mouse to the new position
        robot.mouseMove(newX, newY);
        isDragging.set(false);
    }

    public static String getEfficiencyLabelEn(double percentage) {
        if (percentage < 10) {
            return "Today you are: Boss \nWork? Never heard of it. Busy running the universe.";
        } else if (percentage < 20) {
            return "Today you are: Office Guest \nStopped by for coffee… accidentally did some work.";
        } else if (percentage < 30) {
            return "Today you are: Focus Master \nFocused hard… on procrastination.";
        } else if (percentage < 40) {
            return "Today you are: Life Balancer \nWork isn’t a wolf, it won’t run away.";
        } else if (percentage < 50) {
            return "Today you are: Half-Worker \nNot lazy, not overdoing it. Just chill.";
        } else if (percentage < 60) {
            return "Today you are: Steady Performer \nHolding the line. Golden mean.";
        } else if (percentage < 70) {
            return "Today you are: Engine Warming Up \nA bit more and it’ll launch into orbit.";
        } else if (percentage < 80) {
            return "Today you are: Productivity Monster \nSuperpower: deadlines and coffee.";
        } else if (percentage < 90) {
            return "Today you are: Productivity Machine \nBrain in turbo mode, limits exceeded.";
        } else if (percentage < 100) {
            return "Today you are: Keyboard Cyborg \nCan’t be stopped. Not even by lunch.";
        } else {
            return "Today you are: Legend of Labor \nNot an employee. A myth.";
        }
    }

}
