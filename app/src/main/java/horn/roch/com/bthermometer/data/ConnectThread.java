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
    private final BluetoothSocket socket;
    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
        BluetoothSocket tmp = null;

        try {
            // Well known SPP UUID
            tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) { }
        socket = tmp;
    }

    public void run() {

        bluetoothAdapter.cancelDiscovery();

        try {
            socket.connect();
            Log.i("BT_CONNECTING", "Connecting");

        } catch (IOException connectException) {
                Log.i("BT_CONNECTION_ERROR", connectException.toString());
            try {
                socket.close();
            } catch (IOException closeException) { }
            return;
        }

        manageConnectedSocket(socket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        ConnectedThread connectedThread = new ConnectedThread(mmSocket);
        connectedThread.run();
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }
}
