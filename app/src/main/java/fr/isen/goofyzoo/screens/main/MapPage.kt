package fr.isen.goofyzoo.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.BoundingBox



@Composable
fun MapPage() {
    val startPoint = GeoPoint(43.62380, 5.20964)

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            val mapView = MapView(ctx)

            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(16.0) // Zoom initial
            mapView.controller.setCenter(startPoint)

            mapView.minZoomLevel = 16.0

            val limitBox = BoundingBox(43.63380, 5.21964, 43.61380, 5.19964)
            mapView.setScrollableAreaLimitDouble(limitBox)
            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}