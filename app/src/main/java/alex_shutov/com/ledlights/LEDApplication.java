package alex_shutov.com.ledlights;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import alex_shutov.com.ledlights.Bluetooth.BtCellDeployer;
import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.Bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.Bluetooth.BtPortAdapterCreator;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.BTDeviceScanner;
import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.LogListener;
import alex_shutov.com.ledlights.Bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.LogScannerListener;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.Bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.Bluetooth.DaggerBtPortAdapterCreator;
import alex_shutov.com.ledlights.HexGeneral.CellDeployer;
import alex_shutov.com.ledlights.HexGeneral.LogicCell;
import alex_shutov.com.ledlights.HexGeneral.PortAdapterCreator;
import alex_shutov.com.ledlights.HexGeneral.di.SystemModule;

/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends Application{
    private static final String LOG_TAG = LEDApplication.class.getSimpleName();

    // Unique UUID for this application
    public static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
    public static final UUID HC_05_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /** Connector port */
    BtConnAdapter btAdapter;
    BtConnPort btPort;
    private LogListener btPortListener;

    /** Scanner port */
    BtScanAdapter scanAdapter;
    LogScannerListener scannerListener;
    BtScanPort scanPort;

    CellDeployer btCellDeployer;
    LogicCell cell;

    void initCell(){
        // create cell deployer
        btCellDeployer = new BtCellDeployer(this);
        // create new logic cell
        cell = new BtLogicCell();
        // deploy this cell- create and init ports, connect ports to the cell
        btCellDeployer.deploy(cell);

        Context context = cell.getContext();
        String msg = context == null ? "Context is null" : "Context is not null, DI work";
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // init ConnectorPort
        btPortListener = new LogListener(this);
        btAdapter = new BtConnAdapter(this);
        btAdapter.setPortListener(btPortListener);
        btAdapter.initialize();
        /** adapter is a port */
        btPort = btAdapter;

        /** init ScannerPort */
        scannerListener = new LogScannerListener(this);
        scanAdapter = new BtScanAdapter(this);
        scanAdapter.setPortListener(scannerListener);
        scanAdapter.initialize();
        scanPort = scanAdapter;

        initCell();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



    private UUID uuidFromResource(int resId){
        String id = getResources().getString(resId);
        return UUID.fromString(id);
    }

    public BtConnPort getBtPort() {
        return btPort;
    }

    public BtScanPort getBtScanPort(){
        return scanPort;
    }
}
