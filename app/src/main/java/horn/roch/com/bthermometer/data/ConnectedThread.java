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
    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private EventBus eventBus = EventBus.getDefault();
    public static final int msgSize = 5;
    byte[] msgBuffer = new byte[msgSize];
    int msgIndex = 0;


        public ConnectedThread(BluetoothSocket socket) {
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        inStream = tmpIn;
        outStream = tmpOut;
    }

    public void run() {
        Log.i("BT_CONNECTED", "Connected");

        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inStream.read(buffer);
                parseMessage(buffer, bytes);
            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outStream.write(bytes);
        } catch (IOException e) {
        }
    }

    public void cancel() {
        try {
            socket.close();
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
