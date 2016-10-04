package com.alex.hextest.hex;

import android.content.Context;
import android.renderscript.Script;
import android.util.Log;

import com.alex.hextest.hex.di.DaggerTestPortCreator;
import com.alex.hextest.hex.di.SystemModule;
import com.alex.hextest.hex.di.TestLogicModule;
import com.alex.hextest.hex.di.TestPortCreator;
import com.alex.hextest.hex.test_logic.TestObjectA;
import com.alex.hextest.hex.test_logic.TestObjectBSingleton;

import javax.inject.Inject;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestCellDeployer extends CellDeployer {
    private static final String LOG_TAG = TestCellDeployer.class.getSimpleName();

    // We need context, because CellDeployer creates di component, responsible for
    // injecting context
    private Context context;

    @Inject
    public Context injectedContext;

    @Inject
    TestObjectA objA;

    @Inject
    @Singleton
    TestObjectBSingleton objB;

    private SystemModule systemModule;
    private TestLogicModule testLogicModule;

    public TestCellDeployer(Context context){
        this.context = context;
    }

    /**
     * In this method we have to create Dagger2 component, which will be
     * instantiating all objects inside this logic cell TestLogicCell
     * @return
     */
    @Override
    protected PortAdapterCreator createPortCreator() {
        // port creator is dagger component. create modules, used by it.
        createModules();
        TestPortCreator portCreator = DaggerTestPortCreator.builder()
                .systemModule(systemModule)
                .testLogicModule(testLogicModule)
                .build();
        return portCreator;
    }

    private void createModules(){
        systemModule = new SystemModule(context);
        testLogicModule = new TestLogicModule();
    }

    /**
     * At this point all Port objects is created, we can initialize ports and then connect those
     * @param logicCell
     */
    @Override
    public void connectPorts(LogicCell logicCell) {
        Log.i(LOG_TAG, "connecting ports in connectPorts()");
        TestLogicCell testLogicCell = (TestLogicCell) logicCell;
        // initialize 'testPort' and other ports inside a cell
        TestPort testPort =  testLogicCell.getTestPort();

        if (null != injectedContext && objA != null && objB != null){
            Log.i(LOG_TAG, "Injected context is not null, objects inside TestCellDeployer were" +
                    "created during deployment process");
        } else {
            Log.e(LOG_TAG, "Objects were not injected during deployment, something is broken");
        }
    }

    @Override
    protected void injectCellDeployer(PortAdapterCreator injector) {
        TestPortCreator testPortCreator = (TestPortCreator) injector;
        testPortCreator.injectTestCellDeployed(this);
    }
}