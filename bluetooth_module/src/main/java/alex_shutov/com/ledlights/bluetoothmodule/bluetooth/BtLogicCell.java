package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.LogConnectorListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnListenerEsbReceiveMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnListenerEsbSendMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.LogScannerListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanListenerEsbReceiveMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanListenerEsbSendMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStorageAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import alex_shutov.com.ledlights.hex_general.LogicCell;

/**
 * Created by lodoss on 24/08/16.
 */

/**
 * Logic cell, connecting all ports from Bluetooth module.
 * For two- way communication with ports it implement listener interfaces from
 * every port :BtScanPortListener,  BtConnPortListener,
 */
public class BtLogicCell extends LogicCell {
    private static final String LOG_TAG = LogicCell.class.getSimpleName();
    /** references to ports connectoed to this LogicCell
     * Those intances should be created by DI - we can configure port types by
     * changing modules during creation of DI component (.createPortAdapterCreator()) method.
     * Adapters are set by CellDeployer, but other
     */
    private BtScanAdapter btScanAdapter;
    private BtConnAdapter btConnAdapter;
    private BtStorageAdapter btStorageAdapter;
    private BtCommAdapter btCommAdapter;

    private BtLogicCellFacade btFacade;

    @Inject
    public Context context;
    @Inject
    public EventBus eventBus;
    // loggers
    @Inject
    public LogScannerListener logScannerListener;
    @Inject
    public LogConnectorListener logConnectorListener;
    /**
     * Setting external port listener is up to logic cell, so this backup listener is created here.
     */
    @Inject
    @Named("dummy_comm_listener")
    BtCommPortListener dummyCommPortLogger;

    // ESB mappers
    // maps bt connection listener's method to event bus events (send mapper ) and
    // listens for event bus events and notifies registered receiver
    @Inject
    BtConnListenerEsbSendMapper connListenerSendMapper;
    @Inject
    BtConnListenerEsbReceiveMapper connListenerEsbReceiveMapper;
    // BtScanner event mappers (maps listener)
    @Inject
    BtScanListenerEsbSendMapper scanListenerSendMapper;
    @Inject
    BtScanListenerEsbReceiveMapper scanListenerReceiveMapper;

    /**
     *  Initialize all internal dependencies here
     *  do nothing for now, all BT pairing, data transfer algorithims
     *  has to be initialized here.
     */
    @Override
    public void init() {
        Log.i(LOG_TAG, "BtLogicCell.init()");
        btConnAdapter.initialize();
        btScanAdapter.initialize();
        btStorageAdapter.initialize();
        // connect external port first, then call 'initialize', because it is a
        // decorator.
        btCommAdapter.initialize();
        initializeEsbMappers();
        btFacade = new BtLogicCellFacade(eventBus,
                btStorageAdapter, btCommAdapter);
        btCommAdapter.setDecoree(btFacade);
        btFacade.onInitialized();
    }

    @Override
    public void suspend() {
        btFacade.onDestroying();
        suspendEsbMappers();
    }

    @Override
    protected void injectThisCell() {
        BtPortAdapterCreator creator = (BtPortAdapterCreator) getAdaperCreator();
        creator.injectBtLogicCell(this);
    }

    /**
     *  Accessors
     */
    public BtConnPort getBtConnPort() {
        return btConnAdapter;
    }
    public BtScanPort getBtScanPort() {
        return btScanAdapter;
    }
    public BtStoragePort getBtStoragePort(){
        return btStorageAdapter;
    }
    public BtCommPort getBtCommPort(){
        return btCommAdapter;
    }

    public void setBtScanAdapter(BtScanAdapter btScanAdapter) {
        this.btScanAdapter = btScanAdapter;
    }

    public void setBtConnAdapter(BtConnAdapter btConnAdapter) {
        this.btConnAdapter = btConnAdapter;
    }

    public void setBtStorageAdapter(BtStorageAdapter btStorageAdapter) {
        this.btStorageAdapter = btStorageAdapter;
    }

    public void setBtCommAdapter(BtCommAdapter btCommAdapter) {
        this.btCommAdapter = btCommAdapter;
    }

    public void setBtCommPortListener(BtCommPortListener btCommPortListener) {
        if (null != btCommPortListener) {
            if (null != btFacade){
                btFacade.setCommFeeback(btCommPortListener);
            }
        } else {
            // facade use dummy value by default

        }
    }

    public Context getContext() {
        return context;
    }

    /**
     * Subscribe mappers to EventBus and register those mappers with adapters
     */
    private void initializeEsbMappers(){
        // initialize bluetoooth connector mappers
        connListenerSendMapper.register();
        connListenerEsbReceiveMapper.register();
        // register logger as wrapped callback in receiving mapper
        connListenerEsbReceiveMapper.setListener(logConnectorListener);
        btConnAdapter.setPortListener(connListenerSendMapper);

        // initialize bluetooth scanner mapper
        scanListenerSendMapper.register();
        scanListenerReceiveMapper.register();
        btScanAdapter.setPortListener(scanListenerSendMapper);
        scanListenerReceiveMapper.setListener(logScannerListener);

    }

    /**
     * Unregister mappers from adapters and unsubscribe mappers from EventBus
     */
    private void suspendEsbMappers(){
        connListenerEsbReceiveMapper.unregister();
        connListenerSendMapper.unregister();
        btConnAdapter.setPortListener(null);
        scanListenerSendMapper.unregister();
        scanListenerReceiveMapper.unregister();
    }

    /**
     * For now it will be used for testing
     * @return
     */
    public BtLogicCellFacade getBtFacade() {
        return btFacade;
    }
}
