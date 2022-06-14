package com.art.orion.controller.command;

import com.art.orion.controller.command.impl.EmptyCommand;
import com.art.orion.util.ErrorMessageManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;

import static com.art.orion.util.Constant.COMMAND;
import static com.art.orion.util.Constant.ERROR_CODE;
import static com.art.orion.util.Constant.ERROR_MESSAGE;

public class CommandFactory {
    static Logger logger = LogManager.getLogger();

    private CommandFactory() {
    }

    public static Command defineCommand(HttpServletRequest req) {
        String action = req.getParameter(COMMAND);
        logger.log(Level.DEBUG, () -> String.format("command action = %s", action));
        Command command;
        if (action == null || action.isEmpty()) {
            command = new EmptyCommand();
            sendPageNotFound(req);
        } else {
            try {
                command = CommandType.valueOf(action.toUpperCase()).getCommand();
            } catch (IllegalArgumentException e) {
                command = new EmptyCommand();
                logger.log(Level.ERROR, () -> String.format("Invalid action = %s", action));
                sendPageNotFound(req);
            }
        }
        return command;
    }

    public static void sendPageNotFound(HttpServletRequest req) {
        req.setAttribute(ERROR_CODE, ErrorMessageManager.getMessage("msg.errorCode404"));
        req.setAttribute(ERROR_MESSAGE, ErrorMessageManager.getMessage("msg.errorMessage404"));
        logger.log(Level.DEBUG, () -> "set of attributes for error404");
    }
}
