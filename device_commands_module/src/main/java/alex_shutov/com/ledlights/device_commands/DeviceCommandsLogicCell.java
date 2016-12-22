package alex_shutov.com.ledlights.device_commands;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPort;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortAdapter;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortListener;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * This cell contains:
 *  - logic, used for converting Application's commands into device commands.
 *  - logic for sending commands to device. The point is, that sending data need some time, so,
 *    we have to wait until current command is sent before attempting to send another command.
 *  - Port, responsible for communication with actual device. This logic cell know nothing about
 *    how connection to device is established and how data transferred.
 *  - Emulation of device. User may not have assembled device yet (DIY), but want to see if
 *    this app is a good idea and worth buying details and soldering a device. To do so we need
 *    to emulate device workflow (UI will show changes in device state).
 */
public class DeviceCommandsLogicCell extends LogicCell {

    private DeviceCommPortAdapter commPortAdapter;
    // Reference to interface for sending serialized command to connected device.
    // DeviceCommPortAdapter adapt this interface.
    private DeviceSender sendInterface;

    /**
     * Initialize components, used in this logic cell in this method
     */
    @Override
    public void init() {
        commPortAdapter.initialize();
        // use adapter to output port for sending data.
        sendInterface = commPortAdapter;
    }

    /**
     * Stop activity of all components here - app is about to be destroyed
     */
    @Override
    public void suspend() {

    }

    /**
     * Initialize all objects in this cell by DI
     */
    @Override
    protected void injectThisCell() {
        // get reference to DI component
        DeviceCommandsPortAdapterCreator creator =
                (DeviceCommandsPortAdapterCreator) getAdaperCreator();
        // init all objects
        creator.injectLogicCell(this);
    }

    int count = 0;

    public void sendTestCommand() {
        if (count++ % 2 == 0) {
            sendColorToDevice(0, 0, 0);
        } else {
            sendColorToDevice(255, 255, 255);
        }
    }

    // accessors

    public DeviceCommPort getCommPort() {
        return commPortAdapter;
    }

    public void setCommPortAdapter(DeviceCommPortAdapter commPortAdapter) {
        this.commPortAdapter = commPortAdapter;
    }

    /**
     * Set port listener to adapter, responsible for external communications.
     * @param listener
     */
    public void setDeviceCommPortListener(DeviceCommPortListener listener) {
        commPortAdapter.setPortListener(listener);
    }

    // private methods
    private void sendColorToDevice(int red, int green, int blue) {
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.computation())
                .map(t -> {
                    byte[] bytes = new byte[7];
                    bytes[0] = '!';
                    bytes[1] = 0;
                    bytes[2] = 3;
                    bytes[3] = '\n';
                    bytes[4] = (byte) red;
                    bytes[5] = (byte) green;
                    bytes[6] = (byte) blue;
                    return bytes;
                })
                .observeOn(Schedulers.io())
                .subscribe(d -> {
                    sendInterface.sendData(d);
                });
    }

}