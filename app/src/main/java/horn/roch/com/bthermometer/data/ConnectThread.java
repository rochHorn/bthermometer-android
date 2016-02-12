package horn.roch.com.bthermometer.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by roch on 2/8/2016.
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.mBluetoothAdapter = bluetoothAdapter;
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // Well known SPP UUID
            tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            Log.i("BT_CONNECTING", "Connecting");

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
                Log.i("BT_CONNECTION_ERROR", connectException.toString());
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        ConnectedThread connectedThread = new ConnectedThread(mmSocket);
        connectedThread.run();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
