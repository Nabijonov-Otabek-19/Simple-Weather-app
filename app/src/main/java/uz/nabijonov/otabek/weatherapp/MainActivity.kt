package uz.nabijonov.otabek.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import uz.nabijonov.otabek.weatherapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var city = "Toshkent"
    private val api = "c74a2d3f5c31d69417faf1b1b56b782e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkConnection = NetworkConnection(this)
        networkConnection.observe(this) {
            if (it) {
                binding.noInternet.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE
                getData()
            } else {
                binding.noInternet.visibility = View.VISIBLE
                binding.mainContainer.visibility = View.GONE
            }
        }


        binding.search.setOnClickListener {
            city = binding.edtCity.text.toString()
            getData()
        }

        binding.swipe.isRefreshing = true
        binding.swipe.setOnRefreshListener {
            getData()
        }

        getData()

    }

    private fun getData() {

        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api"

        val queue: RequestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            binding.swipe.isRefreshing = false

            val jsonObj = JSONObject(response.toString())
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val updatedAt: Long = jsonObj.getLong("dt")
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val temp = main.getString("temp") + "°C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")
            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description")
            val address = jsonObj.getString("name") + ", " + sys.getString("country")

            binding.address.text = address
            binding.updatedAt.text = updatedAtText
            binding.status.text = weatherDescription.uppercase()
            binding.temp.text = temp
            binding.tempMin.text = tempMin
            binding.tempMax.text = tempMax
            binding.sunrise.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            binding.sunset.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            binding.wind.text = windSpeed
            binding.pressure.text = pressure
            binding.humidity.text = humidity

        },
            {
                Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            })
        queue.add(request)
    }
}