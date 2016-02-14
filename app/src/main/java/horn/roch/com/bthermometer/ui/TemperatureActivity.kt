package horn.roch.com.bthermometer.ui

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import horn.roch.com.bthermometer.R
import horn.roch.com.bthermometer.data.ConnectThread
import horn.roch.com.bthermometer.events.NewMessage
import kotlinx.android.synthetic.main.activity_temperature.*
import kotlinx.android.synthetic.main.content_temperature.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class TemperatureActivity : AppCompatActivity() {

    val BT_MAC = "20:15:12:14:49:69"
    val eventBus = EventBus.getDefault()
    val data = ArrayList<Entry>()
    val xVals = ArrayList<String>()
    val dataSets = ArrayList<ILineDataSet>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventBus.register(this)
        setContentView(R.layout.activity_temperature)
        setSupportActionBar(toolbar)
        setupBluetooth(BluetoothAdapter.getDefaultAdapter())
        setChart()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    @Subscribe
    fun onEventMainThread(event: NewMessage) {
        Log.i("NEW_MESSAGE", event.msg.toString())
        runOnUiThread(Runnable { addData(event.msg) });
    };

    fun addData(temp : Float){
        var entry = Entry(temp, data.size);
        data.add(entry)
        xVals.add("");

        temperatureChart.notifyDataSetChanged()
        temperatureChart.invalidate()
    }

    fun setChart(){
        val setComp1 = LineDataSet(data, "Temperature \u00B0C");

        dataSets.add(setComp1);
        val lineDataSet = LineData(xVals, dataSets);

        temperatureChart.data = lineDataSet
        val leftAxis = temperatureChart.getAxis(YAxis.AxisDependency.LEFT);
        leftAxis.axisMaxValue = 50f
        leftAxis.axisMinValue = 0f

        val rightAxis = temperatureChart.getAxis(YAxis.AxisDependency.RIGHT);
        rightAxis.axisMaxValue = 100f
        rightAxis.axisMinValue = 0f

        temperatureChart.invalidate()
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
