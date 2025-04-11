package fr.isen.goofyzoo.screens.services

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import fr.isen.goofyzoo.models.Service
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ServiceDetailScreen(navController: NavHostController) {
    val service = navController.previousBackStackEntry?.savedStateHandle?.get<Service>("service")
    service?.let {
        ServiceItem(service)
    }
}

@Composable
fun ServiceItem(service: Service) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = service.name,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))


        service.geopoint?.let { geoPoint ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                AndroidView(
                    factory = { ctx ->
                        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                        val mapView = MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            isHorizontalMapRepetitionEnabled = false
                            isVerticalMapRepetitionEnabled = false
                            setScrollableAreaLimitLatitude(43.63380, 43.61380, 0)
                            setScrollableAreaLimitLongitude(5.19964, 5.21964, 0)
                            controller.setZoom(17.0)
                            controller.setCenter(geoPoint.toOsmGeoPoint())
                            isClickable = false
                            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        }

                        val marker = Marker(mapView).apply {
                            position = geoPoint.toOsmGeoPoint()
                            title = "Enclos n°${service.id}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(marker)

                        mapView
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(4.dp)
                )
            }
        }
    }
}
