package com.vaszilvalentin.educore.log;

import com.vaszilvalentin.educore.data.LoginLogDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages login/logout logs and interactions with the database.
 */
public class LoginLogManager {

    /**
     * Logs a login event for a user.
     *
     * @param userId ID of the user logging in
     */
    public static void logLogin(String userId) {
        addLog(userId, LoginLog.ActionType.LOGIN);
    }

    /**
     * Logs a logout event for a user.
     *
     * @param userId ID of the user logging out
     */
    public static void logLogout(String userId) {
        addLog(userId, LoginLog.ActionType.LOGOUT);
    }

    private static void addLog(String userId, LoginLog.ActionType action) {
        List<LoginLog> logs = LoginLogDatabase.loadLogs();
        LoginLog log = new LoginLog(
                LoginLogDatabase.generateLogId(),
                userId,
                LocalDateTime.now(),
                action
        );
        logs.add(log);
        LoginLogDatabase.saveLogs(logs);
    }

    /**
     * Gets all login/logout logs for a user.
     *
     * @param userId The user ID to filter by
     * @return List of login/logout events
     */
    public static List<LoginLog> getLogsForUser(String userId) {
        List<LoginLog> logs = LoginLogDatabase.loadLogs();
        List<LoginLog> filtered = new ArrayList<>();

        for (LoginLog log : logs) {
            if (log.getUserId().equals(userId)) {
                filtered.add(log);
            }
        }

        return filtered;
    }

    /**
     * Gets all login/logout logs in the system.
     *
     * @return List of all login/logout logs
     */
    public static List<LoginLog> getAllLogs() {
        return LoginLogDatabase.loadLogs();
    }
}