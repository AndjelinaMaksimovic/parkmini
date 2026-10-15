package rs.pmf.parkmini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import rs.pmf.parkmini.ui.ParkMiniTheme
import rs.pmf.parkmini.ui.ParkingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParkMiniTheme {
                ParkingScreen()
            }
        }
    }
}
