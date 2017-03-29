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

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnCreate;
    Button btnSearch;
    TextView textView;
    EditText serviceName;

    WifiP2pManager.Channel gameChannel;
    WifiP2pManager manager;

    WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pDnsSdServiceInfo wifiServiceInfo;

    private final IntentFilter filter = new IntentFilter();
    Receiver receiver;

    MainActivity activity = this;

    final static String serviceType = "_tennis._tcp";

    Collection<WifiP2pDevice> wifiDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add Actions if Peers is changed
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        //Initialize WifiP2pManager and Channel
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        gameChannel = manager.initialize(this, getMainLooper(), null);

        //Initialize BroadCastReceiver
        receiver = new Receiver(manager, gameChannel, this);

        //Initialize Views
        textView = (TextView)findViewById(R.id.textView);
        btnCreate = (Button) findViewById(R.id.buttonCreate);
        btnSearch = (Button) findViewById(R.id.buttonSearch);
        serviceName = (EditText) findViewById(R.id.serviceName);


        //Button onClickListener
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Map<String, String> txtMap = new HashMap<>();
//                txtMap.put("port", "9876");
//
//                wifiServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(serviceName.getText().toString(), serviceType,txtMap);
//
//                manager.addLocalService(gameChannel, wifiServiceInfo, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        logAll("Create Local Service successfully.");
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//                        logAll("Creating the Local Service failed.");
//                    }
//                });

                searchP2PDevices();

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchForServices();
            }
        });

    }

    // This Method log the Messages in the Console
    // and on the TextView in the Application
    public void logAll(String log) {
        textView.append(log+"\n");
        System.out.println(log);
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
                logAll("Discover Peers Fails.");
            }
        });
    }


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

    public void connect(WifiP2pDevice dev) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = dev.deviceAddress;

        // Connect to the Device
        manager.connect(gameChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll("Connecting successful");

                // Create Group
                manager.createGroup(gameChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        logAll("Created Group successful.");

                        // Request Group Infos
                        manager.requestGroupInfo(gameChannel, new WifiP2pManager.GroupInfoListener() {
                            @Override
                            public void onGroupInfoAvailable(WifiP2pGroup group) {
                                // Show Network-Name of this Group
                                logAll("Network-Name: " + group.getNetworkName());

                                // Show all Clients
                                Collection<WifiP2pDevice> clientList =  group.getClientList();
                                while(clientList.iterator().hasNext()){
                                    logAll("Client-Adr: " + clientList.iterator().next());
                                }


                                // Do this only with more then one Device
                                if(clientList.size()>1) {
                                    //Group Owner should send Data to another Device
                                    if (group.isGroupOwner()) {
                                        logAll("You Are The Group Owner");

                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int reason) {
                        logAll("Creating Group failed.");
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                logAll("Connecting failed");
            }
        });
    }




 /*   public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            wifiDeviceList = peers.getDeviceList();

            WifiP2pDevice device = null;
            while(wifiDeviceList.iterator().hasNext()){
                device = wifiDeviceList.iterator().next();
                System.out.println("Mac: " + device.deviceAddress + "Name: " + device.deviceName+'\n');
            }
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            manager.connect(gameChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(activity, "Connection Success", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(activity, "Connection Fails", Toast.LENGTH_SHORT);
                }
            });
        }
    };*/




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

    @Override
    protected void onStop() {
        super.onStop();

        if(wifiServiceInfo!=null)
            manager.removeLocalService(gameChannel, wifiServiceInfo, null);
        if(serviceRequest != null)
            manager.removeServiceRequest(gameChannel, serviceRequest, null);
    }

    public void setWifiDeviceList(Collection<WifiP2pDevice> dev){
        this.wifiDeviceList = dev;
    }
}
