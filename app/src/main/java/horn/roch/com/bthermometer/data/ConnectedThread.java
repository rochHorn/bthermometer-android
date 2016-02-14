package horn.roch.com.bthermometer.data;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import horn.roch.com.bthermometer.events.NewMessage;

/**
 * Created by roch on 2/8/2016.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private EventBus eventBus = EventBus.getDefault();
    byte[] msgBuffer = new byte[5];
    int msgIndex = 0;


        public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        Log.i("BT_CONNECTED", "Connected");

        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                parseMessage(buffer, bytes);
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    private void parseMessage(byte[] buffer, int bytes) {
        for (int i = 0; i < bytes; ++i) {
            if(buffer[i] != "|".getBytes()[0]){
                msgBuffer[msgIndex] = buffer[i];
                ++msgIndex;
            }else {
                msgIndex = 0;
                try {
                    eventBus.post(new NewMessage(Float.valueOf(new String(msgBuffer, "UTF-8"))));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
