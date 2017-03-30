package de.a_berisha.testp2pnetwork;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch;
    private TextView textView;

    private WifiP2pManager.Channel gameChannel;
    private WifiP2pManager manager;
    private Receiver receiver;

    private IntentFilter filter = new IntentFilter();

    private Collection<WifiP2pDevice> wifiDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Views
        textView = (TextView) findViewById(R.id.textV);
        btnSearch = (Button) findViewById(R.id.buttonSearch);

        //Add Actions if Peers is changed
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        //Initialize WifiP2pManager, Channel and Receiver
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        gameChannel = manager.initialize(this, getMainLooper(), null);
        receiver = new Receiver(manager, gameChannel, this);



       //Button onClickListener
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchP2PDevices();
            }
        });

    }

    public void searchP2PDevices(){

        // Discover for Peer to Peer Devices
        manager.discoverPeers(gameChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll("Discover Peers success");
            }

            @Override
            public void onFailure(int reason) {
                logAll("Discover for peers fails." + codeErrorMessage(reason));
            }
        });
    }

    // Connect to the Device
    public void connect(WifiP2pDevice dev) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = dev.deviceAddress;

        // Connect to the Device
        manager.connect(gameChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll("Connecting successful.");
            }

            @Override
            public void onFailure(int reason) {
                logAll("Connecting failed."+ codeErrorMessage(reason));
            }
        });

    }

    // This Method log the Messages in the Console
    // and in the TextView of the Application
    public void logAll(String log) {
        textView.append(log+'\n');
        System.out.println(log);
    }
    public String codeErrorMessage(int code){
        String error;
        switch(code){
            case 0: error = " (Error)"; break;
            case 1: error = " (P2P not supported)"; break;
            case 2: error = " (Busy)"; break;
            default: error = ""; break;
        }
        return error;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    public void setWifiDeviceList(Collection<WifiP2pDevice> dev){ this.wifiDeviceList = dev; }

        /*
    public void searchForServices(){
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
                logAll("Domainname: " + fullDomainName + " Port: " + txtRecordMap.get("port") + " Device: " + srcDevice.deviceAddress + " Name " + srcDevice.deviceName);
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener responseListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                logAll("Instance-Name: " + instanceName + " Type: " + registrationType + " Device: " + srcDevice.deviceAddress + " Device-Name: " + srcDevice.deviceName);
            }
        };

        manager.setDnsSdResponseListeners(gameChannel, responseListener, txtRecordListener);

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        manager.addServiceRequest(gameChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll("Service Request Success.");
            }

            @Override
            public void onFailure(int reason) {
                logAll("Service Request failed.");
                String error = "";
                switch (reason){
                    case WifiP2pManager.P2P_UNSUPPORTED: error = "P2P is not supported."; break;
                    case WifiP2pManager.BUSY: error = "P2P is busy."; break;
                    case WifiP2pManager.ERROR: error = "Error at P2P."; break;
                    default: error = "Reason-Code is unknown.";
                }
                logAll(reason + ": " + error);
            }
        });

        manager.discoverServices(gameChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll("Discover Services Success.");
            }

            @Override
            public void onFailure(int reason) {
                logAll("Discover Services Failed");
            }
        });
    }
    */

}
