package alex_shutov.com.ledlights.bluetooth.BtScannerPort.hex;

import java.util.Set;

import alex_shutov.com.ledlights.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.PortListener;

/**
 * Created by lodoss on 27/07/16.
 */
public interface BtScanPortListener extends PortListener {

    /**
     * Here we use our on 'BtDevice' class for decoupling with Android
     * @param devices
     */
    void onPairedDevicesReceived(Set<BtDevice> devices);

    /**
     * Notifies that new device is found
     * @param device
     */
    void onDeviceFound(BtDevice device);

    /**
     * Is called when all devices were being found.
     */
    void onScanCompleted();

}
