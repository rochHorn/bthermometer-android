package horn.roch.com.bthermometer.ui

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import horn.roch.com.bthermometer.R
import horn.roch.com.bthermometer.data.ConnectThread
import kotlinx.android.synthetic.main.activity_temperature.*

class TemperatureActivity : AppCompatActivity() {

    companion object {
        val BT_MAC = "20:15:12:14:49:69"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature)
        setSupportActionBar(toolbar)

        setupBluetooth(BluetoothAdapter.getDefaultAdapter())
    }

    fun setupBluetooth(bluetoothAdapter: BluetoothAdapter) {
        if (bluetoothAdapter == null) {
            showSnackbar(getString(R.string.bt_not_supported))
        } else {
            if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
            }
            getPairedDevices(bluetoothAdapter)
        }
    }

    fun getPairedDevices(bluetoothAdapter: BluetoothAdapter) {
        val pairedDevices = bluetoothAdapter.bondedDevices;
        if (!pairedDevices.isEmpty()) {
            for (device in pairedDevices) {
                if(device.address.equals(BT_MAC)){
                    val connectThread = ConnectThread(device, bluetoothAdapter)
                    connectThread.start()
                }
                Log.i("PAIRED_DEVICES", device.name + "\n" + device.address);
            }
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(layoutCoordinator, message, Snackbar.LENGTH_LONG).show()
    }
}
