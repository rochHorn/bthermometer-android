package horn.roch.com.bthermometer.ui

import android.bluetooth.BluetoothAdapter
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.db.chart.model.LineSet
import com.db.chart.view.ChartView
import horn.roch.com.bthermometer.R
import horn.roch.com.bthermometer.data.ConnectThread
import kotlinx.android.synthetic.main.activity_temperature.*
import kotlinx.android.synthetic.main.content_temperature.*

class TemperatureActivity : AppCompatActivity() {

    val BT_MAC = "20:15:12:14:49:69"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature)
        setSupportActionBar(toolbar)
        setupBluetooth(BluetoothAdapter.getDefaultAdapter())
        setChart()
    }

    fun setChart(){
        var dataSet = LineSet()
        dataSet.addPoint("Xxx", 2.54f)
        dataSet.addPoint("Zzz", 3.54f)
        dataSet.addPoint("Ccc", 4.54f)
        dataSet.addPoint("Ddd", 5.54f)

        temperatureChart.addData(dataSet)

        val paint = Paint();
        paint.color = R.color.design_fab_shadow_end_color

        temperatureChart.setGrid(ChartView.GridType.HORIZONTAL, paint)
        temperatureChart.show()
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
                if (device.address.equals(BT_MAC)) {
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
