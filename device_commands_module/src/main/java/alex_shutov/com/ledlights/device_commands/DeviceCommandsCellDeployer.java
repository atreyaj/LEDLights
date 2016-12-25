package alex_shutov.com.ledlights.device_commands;

import javax.inject.Inject;

import alex_shutov.com.ledlights.device_commands.ControlPort.ControlPortAdapter;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortAdapter;
import alex_shutov.com.ledlights.device_commands.di.CellModule;
import alex_shutov.com.ledlights.device_commands.di.CommPortModule;
import alex_shutov.com.ledlights.device_commands.di.ControlPortModule;
import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * Responsiple for wiring up all port adapters in 'device commands' logic cell.
 */
public class DeviceCommandsCellDeployer extends CellDeployer {

    // TODO: injected adapters here
    @Inject
    public DeviceCommPortAdapter commPortAdapter;
    @Inject
    public ControlPortAdapter controlPortAdapter;

    /**
     * Instantiate and setup DI component, which will create objects for this logic cell
     * @return
     */
    @Override
    protected PortAdapterCreator createPortCreator() {
        // create di modules
        CellModule cellModule = new CellModule();
        CommPortModule commPortModule = new CommPortModule();
        ControlPortModule controlPortModule = new ControlPortModule();
        PortAdapterCreator creator = DaggerDeviceCommandsPortAdapterCreator.builder()
                .cellModule(cellModule)
                .commPortModule(commPortModule)
                .controlPortModule(controlPortModule)
                .build();
        return creator;
    }

    @Override
    public void connectPorts(LogicCell logicCell) {
        DeviceCommandsLogicCell cell = (DeviceCommandsLogicCell) logicCell;
        // set all adapter, which was injected before
        cell.setCommPortAdapter(commPortAdapter);
        cell.setControlPortAdapter(controlPortAdapter);
        cell.init();
    }

    @Override
    protected void injectCellDeployer(PortAdapterCreator injector) {
        // cast injector to type used in this logic cell
        DeviceCommandsPortAdapterCreator diComponent = (DeviceCommandsPortAdapterCreator) injector;
        // create objects
        diComponent.injectCellDeployer(this);
    }
}
