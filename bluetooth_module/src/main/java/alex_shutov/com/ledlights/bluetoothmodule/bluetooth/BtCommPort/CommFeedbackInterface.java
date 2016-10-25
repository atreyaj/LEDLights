package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort;

/**
 * Created by lodoss on 12/10/16.
 */

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * External output port for Bluetooth logic cell.
 * Notifies external part of the app about results of operations (methods of external input port),
 * as well as changes in connection state (if device was reconnected after loss of connection,
 * if bluetooth cell is using dummy source right now due to lack of actual device)
 */
public interface CommFeedbackInterface {

    /**
     * Called as response to request to start connection from input port.
     * Workflow - app calls .startConnection() method on input port, resulting in establishing
     * a connection or error either.
     */
    void onConnectionStarted(BtDevice btDevice);

    /**
     * Tell app that Bluetooth cell did not established connection to any devices. Notice,
     * dummy device will be used only when connection were being established and then lost
     * by some reason.
     */
    void onConnectionFailed();

    /**
     * Tell app that data sent - result of .sendData() method
     */
    void onDataSent();

    /**
     * Tell app that data sending failed - result of .sendData() method.
     */
    void onDataSendFailed();

    /**
     * Bluetooth logic cell fires this callback if device were connected and connection were
     * lost by some reason, but, cell have managed to reconnect to this device.
     * This can happen if Motorcycle's module is cut from power.
     * @param isSameDevice
     */
    void onReconnected(boolean isSameDevice);

    /**
     * Tell app that connection to previously connected device were lost and
     * Bluetooth cell switched to dummy device (doing nothing).
     * Cell will, eventually, try to re- establish connection.
     * Re- establishing behaviour will depend on Bluetooth logic cell settings.
     * Compared to method below .onReconnectAttemptFailed(), this callback gets called
     * just once.
     */
    void onDummyDeviceSelected();

    /**
     * Notify app, that attempt to re-establish lost connection has failed.
     */
    void onReconnectAttemptFailed();

}