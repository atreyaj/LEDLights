package alex_shutov.com.ledlights.Bluetooth;

/**
 * Created by Alex on 7/25/2016.
 */

/**
 * Model for Bluetooth device. It is replicatioin of Android's
 * BluetoothDevice class, but it also has other fields -
 * UUID (hardware module or handset) and device description
 */
public class BtDevice {

    private String deviceName;
    private String deviceAddress;
    private String deviceUuId;
    private String deviceDescription;
    /** Optional*/
    private Boolean isPaired;
    /** whether to use secure or insecure connection */
    private Boolean isSecureOperation;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceUuId() {
        return deviceUuId;
    }

    public void setDeviceUuId(String deviceUuId) {
        this.deviceUuId = deviceUuId;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public Boolean getPaired() {
        return isPaired;
    }

    public void setPaired(Boolean paired) {
        isPaired = paired;
    }

    public Boolean getSecureOperation() {
        return isSecureOperation;
    }

    public void setSecureOperation(Boolean secureOperation) {
        isSecureOperation = secureOperation;
    }
}
