package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.CompositeExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CommandSerializer;

/**
 * Created by lodoss on 22/12/16.
 */

public class CompositeSerializer extends CompositeExecutor {

    public CompositeSerializer() {
        super();
    }

    /**
     * We know that all executors here is of type CommandSerializer
     * @param sender
     */
    public void setDeviceSender(DeviceSender sender) {
        for (CommandExecutor e : executors) {
            CommandSerializer s = (CommandSerializer) e;
            s.setDeviceSender(sender);
        }
    }

    public CommandSerializer getRightExecutor(Command command) throws IllegalArgumentException {
        for (CommandExecutor e : executors) {
            if (e.canExecute(command)) {
                return (CommandSerializer) e;
            }
        }
        throw new IllegalArgumentException("Unsupported command");
    }


}
