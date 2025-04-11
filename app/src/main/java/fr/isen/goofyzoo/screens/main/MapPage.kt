package fr.isen.goofyzoo.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.compose.runtime.LaunchedEffect
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.goofyzoo.models.Biome
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Marker
import fr.isen.goofyzoo.utils.Node
import fr.isen.goofyzoo.utils.ZooGraph
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Polyline
import fr.isen.goofyzoo.R


@Composable
fun MapPage() {
    val showPolygons = remember { mutableStateOf(true) }
    val showRoute = remember { mutableStateOf(true) }
    val database = FirebaseDatabase.getInstance().getReference("zoo")
    val biomes = remember { mutableStateOf(emptyList<Biome>()) }
    val startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val endPointState = remember { mutableStateOf<GeoPoint?>(null) }


    val startSpinnerRef = remember { mutableStateOf<Spinner?>(null) }
    val endSpinnerRef = remember { mutableStateOf<Spinner?>(null) }
    val enclosureListRef = remember { mutableStateOf(emptyList<Pair<String, GeoPoint>>()) }
    val spinnerContainerRef = remember { mutableStateOf<LinearLayout?>(null) }


    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val biomeList = snapshot.children.mapNotNull { it.getValue(Biome::class.java) }
                biomes.value = biomeList


                val newEnclosureList = biomeList.flatMap { biome ->
                    biome.enclosures.mapNotNull { enclosure ->
                        enclosure.geopoint?.toOsmGeoPoint()?.let { geoPoint ->
                            "${biome.name} - Enclos ${enclosure.id}" to geoPoint
                        }
                    }
                }
                enclosureListRef.value = newEnclosureList


                if (biomeList.isNotEmpty() && startPoint.value == null) {
                    biomeList[0].enclosures.firstOrNull()?.geopoint?.toOsmGeoPoint()?.let {
                        startPoint.value = it
                    }
                }
                if (biomeList.size > 1 && endPointState.value == null) {
                    biomeList[1].enclosures.firstOrNull()?.geopoint?.toOsmGeoPoint()?.let {
                        endPointState.value = it
                    }
                }


                updateSpinners()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erreur Firebase: ${error.message}")
            }


            private fun updateSpinners() {
                val startSpinner = startSpinnerRef.value
                val endSpinner = endSpinnerRef.value

                if (startSpinner != null && endSpinner != null && enclosureListRef.value.isNotEmpty()) {

                    val defaultLabel = "Sélectionnez un enclos"
                    val enclosureList = enclosureListRef.value
                    val spinnerItems = listOf(defaultLabel) + enclosureList.map { it.first }

                    val ctx = startSpinner.context
                    val adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, spinnerItems)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    startSpinner.adapter = adapter
                    endSpinner.adapter = adapter

                    startSpinner.setSelection(0, false)
                    endSpinner.setSelection(0, false)
                }
            }
        })
    }

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            val frameLayout = FrameLayout(ctx)
            val mapView = MapView(ctx)

            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(16.0)
            mapView.minZoomLevel = 16.0
            startPoint.value?.let { mapView.controller.setCenter(it) }

            val limitBox = BoundingBox(43.63380, 5.21964, 43.61380, 5.19964)
            mapView.setScrollableAreaLimitDouble(limitBox)

            frameLayout.addView(mapView)


            val toggleButton = Button(ctx).apply {
                text = if (showPolygons.value) "Masquer les biômes" else "Afficher les biômes"
                setBackgroundColor(Color.argb(180, 0, 0, 0))
                setTextColor(Color.WHITE)
                textSize = 12f
            }
            val buttonParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(20, 0, 20, 180)
            }
            frameLayout.addView(toggleButton, buttonParams)


            val toggleRouteButton = Button(ctx).apply {
                text = if (showRoute.value) R.string.map_champ2_1.toString() else R.string.map_champ2_2.toString()
                setBackgroundColor(Color.argb(180, 0, 0, 0))
                setTextColor(Color.WHITE)
                textSize = 12f
            }
            val routeButtonParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(20, 0, 20, 20)
            }
            frameLayout.addView(toggleRouteButton, routeButtonParams)

            val startSpinner = Spinner(ctx)
            val endSpinner = Spinner(ctx)
            startSpinnerRef.value = startSpinner
            endSpinnerRef.value = endSpinner

            val startLabel = TextView(ctx).apply {
                text = "Départ"
                setTextColor(Color.BLACK)
                textSize = 14f
            }
            val endLabel = TextView(ctx).apply {
                text = "Arrivée"
                setTextColor(Color.BLACK)
                textSize = 14f
            }

            val startColumn = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                addView(startLabel)
                addView(startSpinner)
            }
            val endColumn = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                addView(endLabel)
                addView(endSpinner)
            }

            val spinnerContainer = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(24, 24, 24, 24)
                setBackgroundColor(Color.argb(230, 255, 255, 255))
                background = GradientDrawable().apply {
                    cornerRadius = 24f
                    setStroke(2, Color.LTGRAY)
                    setColor(Color.argb(230, 255, 255, 255))
                }

                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(16, 0, 16, 0)
                }
                addView(startColumn, params)
                addView(endColumn, params)
            }
            spinnerContainerRef.value = spinnerContainer

            val containerParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                setMargins(20, 40, 20, 0)
            }

            frameLayout.addView(spinnerContainer, containerParams)



            val legendContainer = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(Color.argb(230, 255, 255, 255))
                background = GradientDrawable().apply {
                    cornerRadius = 12f
                    setStroke(2, Color.LTGRAY)
                }
            }


            val optimalPathRow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val optimalPathLine = View(ctx).apply {
                setBackgroundColor(Color.BLUE)
                layoutParams = LinearLayout.LayoutParams(40, 6)
            }
            val optimalPathLabel = TextView(ctx).apply {
                text = "Chemin optimal"
                setTextColor(Color.BLACK)
                textSize = 12f
                setPadding(8, 0, 0, 0)
            }
            optimalPathRow.addView(optimalPathLine)
            optimalPathRow.addView(optimalPathLabel)


            val handicapPathRow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val handicapPathLine = View(ctx).apply {
                setBackgroundColor(Color.RED)
                layoutParams = LinearLayout.LayoutParams(40, 6)
            }
            val handicapPathLabel = TextView(ctx).apply {
                text = "Chemin sans escalier"
                setTextColor(Color.BLACK)
                textSize = 12f
                setPadding(8, 0, 0, 0)
            }
            handicapPathRow.addView(handicapPathLine)
            handicapPathRow.addView(handicapPathLabel)


            legendContainer.addView(optimalPathRow)
            legendContainer.addView(handicapPathRow)


            val legendParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.START
                setMargins(20, 0, 0, 20)
            }

            frameLayout.addView(legendContainer, legendParams)



            val polygonVallon = createPolygonVallon()
            val labelVallon = createLabelVallon(mapView)
            val polygonBelvedere = createPolygonBelvedere()
            val labelBelvedere = createLabelBelvedere(mapView)
            val polygonPlateau = createPolygonPlateau()
            val labelPlateau = createLabelPlateau(mapView)
            val polygonClairieres = createPolygonClairieres()
            val labelClairieres = createLabelClairieres(mapView)
            val polygonBoisDesPins = createPolygonBoisDesPins()
            val labelBoisDesPins = createLabelBoisDesPins(mapView)
            val polygonBergerie = createPolygonBergerie()
            val labelBergerie = createLabelBergerie(mapView)

            val polygons = listOf(
                polygonVallon to labelVallon,
                polygonBelvedere to labelBelvedere,
                polygonPlateau to labelPlateau,
                polygonClairieres to labelClairieres,
                polygonBoisDesPins to labelBoisDesPins,
                polygonBergerie to labelBergerie
            )


            val graph = ZooGraph(ctx)
            graph.loadGeoJsonFromAssets("ways.geojson")

            val graphHandicape = ZooGraph(ctx)
            graphHandicape.loadGeoJsonFromAssets("ways_without_stairs.geojson")

            val startMarker = Marker(mapView).apply {
                startPoint.value?.let { position = it }
                title = "Départ"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            val endMarker = Marker(mapView).apply {
                endPointState.value?.let { position = it }
                title = "Arrivée"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            val polyline = Polyline().apply {
                title = R.string.itinerary_title.toString()
                outlinePaint.color = Color.BLUE
                outlinePaint.strokeWidth = 6f
            }

            val polylineHandicape = Polyline().apply {
                title = R.string.itinerary_handicape_title.toString()
                outlinePaint.color = Color.RED
                outlinePaint.strokeWidth = 6f
            }


            fun updatePath() {
                startPoint.value?.let { start ->
                    endPointState.value?.let { end ->
                        val startNode = Node(start.longitude, start.latitude)
                        val endNode = Node(end.longitude, end.latitude)
                        val path = graph.shortestPath(startNode, endNode)
                        val pathHandicape = graphHandicape.shortestPath(startNode, endNode)
                        polyline.setPoints(path.map { GeoPoint(it.lat, it.lon) })
                        polylineHandicape.setPoints(pathHandicape.map { GeoPoint(it.lat, it.lon) })
                        mapView.invalidate()
                    }
                }
            }



            val defaultLabel = "Sélectionnez un enclos"
            val enclosureList = enclosureListRef.value
            val spinnerItems = listOf(defaultLabel) + enclosureList.map { it.first }

            val ctx = startSpinner.context
            val adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, spinnerItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            startSpinner.adapter = adapter
            endSpinner.adapter = adapter

            startSpinner.setSelection(0, false)
            endSpinner.setSelection(0, false)


            startSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val enclosureList = enclosureListRef.value
                    if (position == 0) {
                        startPoint.value = null
                        return
                    }
                    if (position > 0 && position < enclosureList.size) {
                        startPoint.value = enclosureList[position - 1].second
                        startMarker.position = startPoint.value
                        updatePath()
                        mapView.invalidate()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            endSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val enclosureList = enclosureListRef.value
                    if (position == 0) {
                        endPointState.value = null
                        return
                    }
                    if (position > 0 && position < enclosureList.size) {
                        endPointState.value = enclosureList[position - 1].second
                        endMarker.position = endPointState.value
                        updatePath()
                        mapView.invalidate()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }


            fun updatePolygonsVisibility() {
                mapView.overlays.removeAll(polygons.map { it.first } + polygons.map { it.second })
                if (showPolygons.value) {
                    polygons.forEach { (polygon, label) ->
                        mapView.overlays.add(polygon)
                        mapView.overlays.add(label)
                    }
                }
                toggleButton.text = if (showPolygons.value) "Masquer les biômes" else "Afficher les biômes"
                mapView.invalidate()
            }

            fun setupPolygonsLayer() {
                polygons.forEach { (polygon, label) ->
                    mapView.overlays.add(0, polygon)  // Ajoute à l'index 0 pour qu'ils soient en-dessous
                    mapView.overlays.add(1, label)    // Juste au-dessus du polygone
                }
                mapView.invalidate()
            }


            fun updateRouteVisibility() {
                mapView.overlays.removeAll(listOf(startMarker, endMarker, polyline, polylineHandicape))

                if (showRoute.value) {
                    mapView.overlays.add(startMarker)
                    mapView.overlays.add(endMarker)

                    mapView.overlays.add(polylineHandicape)
                    mapView.overlays.add(polyline)
                }

                toggleRouteButton.text = if (showRoute.value) "Masquer l'itinéraire" else "Afficher l'itinéraire"

                spinnerContainerRef.value?.visibility = if (showRoute.value) View.VISIBLE else View.GONE

                mapView.invalidate()
            }



            toggleButton.setOnClickListener {
                showPolygons.value = !showPolygons.value
                updatePolygonsVisibility()
            }

            toggleRouteButton.setOnClickListener {
                showRoute.value = !showRoute.value
                updateRouteVisibility()
            }

            setupPolygonsLayer()
            updatePath()
            //updatePolygonsVisibility()
            updateRouteVisibility()

            val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    if (showRoute.value) {
                        p?.let {
                            endPointState.value = it
                            endMarker.position = it
                            updatePath()
                            mapView.invalidate()
                        }
                    }
                    return true
                }

            })

            mapView.overlays.add(mapEventsOverlay)

            frameLayout
        },
        modifier = Modifier.fillMaxSize()
    )
}


private fun createPolygonVallon(): Polygon {
    val polygon = Polygon()
    polygon.setOnClickListener { _, _, _ -> false }
    polygon.fillPaint.color = Color.argb(140, 164, 189, 204)
    polygon.outlinePaint.apply {
        color = Color.rgb(164, 189, 204)
        strokeWidth = 10f
    }
    polygon.points = listOf(
        GeoPoint(43.62671, 5.205213),
        GeoPoint(43.626508, 5.205476),
        GeoPoint(43.626446, 5.205454),
        GeoPoint(43.626392, 5.205454),
        GeoPoint(43.626291, 5.20546),
        GeoPoint(43.626198, 5.205508),
        GeoPoint(43.626104, 5.205604),
        GeoPoint(43.626015, 5.205696),
        GeoPoint(43.625995, 5.205717),
        GeoPoint(43.625922, 5.205674),
        GeoPoint(43.625871, 5.205637),
        GeoPoint(43.62579, 5.205481),
        GeoPoint(43.625793, 5.205368),
        GeoPoint(43.625716, 5.205288),
        GeoPoint(43.625584, 5.205309),
        GeoPoint(43.62542, 5.205374),
        GeoPoint(43.625343, 5.205395),
        GeoPoint(43.625218, 5.205599),
        GeoPoint(43.625028, 5.205765),
        GeoPoint(43.624865, 5.205851),
        GeoPoint(43.624783, 5.205958),
        GeoPoint(43.624647, 5.206136),
        GeoPoint(43.624608, 5.206248),
        GeoPoint(43.624476, 5.206232),
        GeoPoint(43.624329, 5.206318),
        GeoPoint(43.624232, 5.206404),
        GeoPoint(43.6242, 5.206672),
        GeoPoint(43.624014, 5.206742),
        GeoPoint(43.623932, 5.206769),
        GeoPoint(43.623839, 5.206887),
        GeoPoint(43.623746, 5.207037),
        GeoPoint(43.623645, 5.20723),
        GeoPoint(43.623583, 5.207273),
        GeoPoint(43.623571, 5.207471),
        GeoPoint(43.623624, 5.207871),
        GeoPoint(43.623686, 5.207938),
        GeoPoint(43.6237, 5.208037),
        GeoPoint(43.623673, 5.208182),
        GeoPoint(43.623634, 5.208292),
        GeoPoint(43.623636, 5.208592),
        GeoPoint(43.623636, 5.208654),
        GeoPoint(43.623595, 5.208756),
        GeoPoint(43.623358, 5.209078),
        GeoPoint(43.623086, 5.209456),
        GeoPoint(43.622966, 5.209681),
        GeoPoint(43.622698, 5.209676),
        GeoPoint(43.6227, 5.209558),
        GeoPoint(43.622583, 5.209319),
        GeoPoint(43.622244, 5.208976),
        GeoPoint(43.622394, 5.208571),
        GeoPoint(43.622419, 5.208383),
        GeoPoint(43.622419, 5.208096),
        GeoPoint(43.622394, 5.207798),
        GeoPoint(43.622361, 5.207423),
        GeoPoint(43.622355, 5.207348),
        GeoPoint(43.622394, 5.20712),
        GeoPoint(43.622549, 5.207045),
        GeoPoint(43.622675, 5.207066),
        GeoPoint(43.622763, 5.206991),
        GeoPoint(43.622904, 5.206701),
        GeoPoint(43.623168, 5.20638),
        GeoPoint(43.623419, 5.206256),
        GeoPoint(43.623807, 5.2062),
        GeoPoint(43.624083, 5.206074),
        GeoPoint(43.624372, 5.205771),
        GeoPoint(43.624834, 5.205277),
        GeoPoint(43.624967, 5.205138),
        GeoPoint(43.62517, 5.20503),
        GeoPoint(43.625452, 5.204979),
        GeoPoint(43.625951, 5.204993),
        GeoPoint(43.626273, 5.204998),
        GeoPoint(43.62652, 5.205052),
        GeoPoint(43.626621, 5.20514),
        GeoPoint(43.62671, 5.205213)
    )
    return polygon
}

private fun createLabelVallon(mapView: MapView): Marker {
    return Marker(mapView).apply {
        position = GeoPoint(43.623183, 5.207493)
        textLabelBackgroundColor = Color.TRANSPARENT
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setTextIcon("Le Vallon des Cascades")
    }
}


private fun createPolygonBelvedere(): Polygon {
    val polygon = Polygon()
    polygon.setOnClickListener { _, _, _ -> false }
    polygon.fillPaint.color = Color.argb(140, 181, 165, 137)
    polygon.outlinePaint.apply {
        color = Color.rgb(181, 165, 137)
        strokeWidth = 10f
    }
    polygon.points = listOf(
        GeoPoint(43.626713, 5.205224),
        GeoPoint(43.626886, 5.207077),
        GeoPoint(43.626423, 5.207997),
        GeoPoint(43.626628, 5.209217),
        GeoPoint(43.626222, 5.209824),
        GeoPoint(43.626032, 5.209842),
        GeoPoint(43.625948, 5.209947),
        GeoPoint(43.625849, 5.209563),
        GeoPoint(43.625752, 5.209563),
        GeoPoint(43.625667, 5.209295),
        GeoPoint(43.625602, 5.209199),
        GeoPoint(43.625519, 5.209142),
        GeoPoint(43.625433, 5.209145),
        GeoPoint(43.625406, 5.209166),
        GeoPoint(43.625191, 5.208946),
        GeoPoint(43.625169, 5.208737),
        GeoPoint(43.625187, 5.208482),
        GeoPoint(43.625194, 5.208364),
        GeoPoint(43.62522, 5.208225),
        GeoPoint(43.625241, 5.208169),
        GeoPoint(43.625319, 5.207898),
        GeoPoint(43.62534, 5.207785),
        GeoPoint(43.625342, 5.207691),
        GeoPoint(43.625325, 5.20763),
        GeoPoint(43.625346, 5.20756),
        GeoPoint(43.625484, 5.207383),
        GeoPoint(43.625653, 5.20734),
        GeoPoint(43.625758, 5.207187),
        GeoPoint(43.625783, 5.207077),
        GeoPoint(43.625787, 5.206948),
        GeoPoint(43.625696, 5.206892),
        GeoPoint(43.625643, 5.206817),
        GeoPoint(43.62555, 5.206777),
        GeoPoint(43.625626, 5.206482),
        GeoPoint(43.625826, 5.205867),
        GeoPoint(43.625876, 5.205728),
        GeoPoint(43.625911, 5.205666),
        GeoPoint(43.625997, 5.205714),
        GeoPoint(43.626201, 5.205505),
        GeoPoint(43.626292, 5.205457),
        GeoPoint(43.626453, 5.205452),
        GeoPoint(43.626508, 5.20547),
        GeoPoint(43.626694, 5.20524)
    )
    return polygon
}

private fun createLabelBelvedere(mapView: MapView): Marker {
    return Marker(mapView).apply {
        position = GeoPoint(43.6259, 5.2074)
        textLabelBackgroundColor = Color.TRANSPARENT
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setTextIcon("Le Belvédère")
    }
}


private fun createPolygonPlateau(): Polygon {
    val polygon = Polygon()
    polygon.setOnClickListener { _, _, _ -> false }
    polygon.fillPaint.color = Color.argb(140, 226, 165, 157)
    polygon.outlinePaint.apply {
        color = Color.rgb(226, 165, 157)
        strokeWidth = 10f
    }
    polygon.points = listOf(
        GeoPoint(43.625912, 5.205661),
        GeoPoint(43.625881, 5.205722),
        GeoPoint(43.625632, 5.206468),
        GeoPoint(43.625553, 5.206771),
        GeoPoint(43.625646, 5.206811),
        GeoPoint(43.625692, 5.206892),
        GeoPoint(43.625788, 5.206954),
        GeoPoint(43.625784, 5.207069),
        GeoPoint(43.625764, 5.207176),
        GeoPoint(43.625656, 5.207337),
        GeoPoint(43.6255, 5.207372),
        GeoPoint(43.625417, 5.207452),
        GeoPoint(43.625347, 5.207554),
        GeoPoint(43.625325, 5.20763),
        GeoPoint(43.625341, 5.207694),
        GeoPoint(43.625339, 5.20779),
        GeoPoint(43.625314, 5.207919),
        GeoPoint(43.625218, 5.208252),
        GeoPoint(43.625193, 5.208364),
        GeoPoint(43.625174, 5.20881),
        GeoPoint(43.625195, 5.208952),
        GeoPoint(43.625409, 5.209161),
        GeoPoint(43.625455, 5.209142),
        GeoPoint(43.625516, 5.209142),
        GeoPoint(43.625609, 5.209201),
        GeoPoint(43.625656, 5.209274),
        GeoPoint(43.625713, 5.209416),
        GeoPoint(43.625744, 5.209569),
        GeoPoint(43.62527, 5.209781),
        GeoPoint(43.624773, 5.210092),
        GeoPoint(43.624577, 5.210347),
        GeoPoint(43.624371, 5.210381),
        GeoPoint(43.624322, 5.210368),
        GeoPoint(43.624336, 5.210204),
        GeoPoint(43.624336, 5.210132),
        GeoPoint(43.624309, 5.210006),
        GeoPoint(43.624262, 5.209958),
        GeoPoint(43.62428, 5.209917),
        GeoPoint(43.624212, 5.20988),
        GeoPoint(43.624144, 5.209786),
        GeoPoint(43.624099, 5.209762),
        GeoPoint(43.623893, 5.209743),
        GeoPoint(43.623767, 5.209665),
        GeoPoint(43.62367, 5.209663),
        GeoPoint(43.623541, 5.209724),
        GeoPoint(43.623493, 5.209781),
        GeoPoint(43.6234, 5.20995),
        GeoPoint(43.623172, 5.209738),
        GeoPoint(43.623077, 5.209475),
        GeoPoint(43.623598, 5.208756),
        GeoPoint(43.623637, 5.208649),
        GeoPoint(43.623637, 5.208292),
        GeoPoint(43.623674, 5.208179),
        GeoPoint(43.623697, 5.208037),
        GeoPoint(43.623683, 5.207933),
        GeoPoint(43.623629, 5.207871),
        GeoPoint(43.623574, 5.207469),
        GeoPoint(43.623588, 5.207273),
        GeoPoint(43.623646, 5.207227),
        GeoPoint(43.623759, 5.207013),
        GeoPoint(43.623928, 5.206766),
        GeoPoint(43.6242, 5.206669),
        GeoPoint(43.624235, 5.206401),
        GeoPoint(43.624353, 5.206299),
        GeoPoint(43.624478, 5.206229),
        GeoPoint(43.624604, 5.206248),
        GeoPoint(43.624651, 5.206133),
        GeoPoint(43.62487, 5.205851),
        GeoPoint(43.625033, 5.205757),
        GeoPoint(43.625218, 5.205602),
        GeoPoint(43.625344, 5.205393),
        GeoPoint(43.625597, 5.205304),
        GeoPoint(43.625715, 5.205283),
        GeoPoint(43.625793, 5.205363),
        GeoPoint(43.625793, 5.205473),
        GeoPoint(43.625869, 5.205637),
        GeoPoint(43.6259, 5.205653),
        GeoPoint(43.625912, 5.205661)
    )
    return polygon
}

private fun createLabelPlateau(mapView: MapView): Marker {
    return Marker(mapView).apply {
        position = GeoPoint(43.6248, 5.2072)
        textLabelBackgroundColor = Color.TRANSPARENT
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setTextIcon("Le Plateau")
    }
}

private fun createPolygonClairieres(): Polygon {
    val polygon = Polygon()
    polygon.setOnClickListener { _, _, _ -> false }
    polygon.fillPaint.color = Color.argb(140, 226, 202, 157)
    polygon.outlinePaint.apply {
        color = Color.rgb(226, 202, 157)
        strokeWidth = 10f
    }
    polygon.points = listOf(
        GeoPoint(43.624578, 5.210355),
        GeoPoint(43.624679, 5.210534),
        GeoPoint(43.624623, 5.210644),
        GeoPoint(43.624648, 5.21088),
        GeoPoint(43.62465, 5.211087),
        GeoPoint(43.624609, 5.211342),
        GeoPoint(43.624496, 5.211602),
        GeoPoint(43.624384, 5.211902),
        GeoPoint(43.624238, 5.212216),
        GeoPoint(43.624123, 5.212374),
        GeoPoint(43.623929, 5.212559),
        GeoPoint(43.623832, 5.212632),
        GeoPoint(43.623737, 5.212664),
        GeoPoint(43.623651, 5.212635),
        GeoPoint(43.623599, 5.212838),
        GeoPoint(43.623533, 5.213037),
        GeoPoint(43.623457, 5.213101),
        GeoPoint(43.62341, 5.21327),
        GeoPoint(43.623296, 5.213624),
        GeoPoint(43.623259, 5.213884),
        GeoPoint(43.623253, 5.214035),
        GeoPoint(43.623078, 5.214721),
        GeoPoint(43.623019, 5.215054),
        GeoPoint(43.623046, 5.215266),
        GeoPoint(43.623028, 5.215572),
        GeoPoint(43.623059, 5.215652),
        GeoPoint(43.622669, 5.215759),
        GeoPoint(43.622319, 5.215773),
        GeoPoint(43.621919, 5.215837),
        GeoPoint(43.621935, 5.215408),
        GeoPoint(43.621928, 5.21515),
        GeoPoint(43.621881, 5.214941),
        GeoPoint(43.621821, 5.214751),
        GeoPoint(43.621714, 5.214297),
        GeoPoint(43.621723, 5.21419),
        GeoPoint(43.62178, 5.213965),
        GeoPoint(43.621875, 5.213871),
        GeoPoint(43.621873, 5.213445),
        GeoPoint(43.621961, 5.21323),
        GeoPoint(43.62204, 5.213262),
        GeoPoint(43.622194, 5.213259),
        GeoPoint(43.622291, 5.212922),
        GeoPoint(43.622454, 5.212696),
        GeoPoint(43.622514, 5.212321),
        GeoPoint(43.622514, 5.212227),
        GeoPoint(43.622477, 5.212066),
        GeoPoint(43.622205, 5.211948),
        GeoPoint(43.622129, 5.2119),
        GeoPoint(43.622003, 5.211596),
        GeoPoint(43.621982, 5.211175),
        GeoPoint(43.622028, 5.211068),
        GeoPoint(43.62219, 5.210945),
        GeoPoint(43.622277, 5.2108),
        GeoPoint(43.622314, 5.21069),
        GeoPoint(43.622364, 5.210516),
        GeoPoint(43.622442, 5.21047),
        GeoPoint(43.622469, 5.210301),
        GeoPoint(43.622532, 5.210121),
        GeoPoint(43.622621, 5.209925),
        GeoPoint(43.622706, 5.20977),
        GeoPoint(43.622769, 5.209711),
        GeoPoint(43.622848, 5.209679),
        GeoPoint(43.622969, 5.209681),
        GeoPoint(43.623083, 5.209467),
        GeoPoint(43.623169, 5.209735),
        GeoPoint(43.623404, 5.209947),
        GeoPoint(43.623501, 5.209767),
        GeoPoint(43.623546, 5.209719),
        GeoPoint(43.62367, 5.20966),
        GeoPoint(43.623779, 5.209665),
        GeoPoint(43.623899, 5.209746),
        GeoPoint(43.624115, 5.209765),
        GeoPoint(43.624156, 5.209791),
        GeoPoint(43.624212, 5.209888),
        GeoPoint(43.624284, 5.209915),
        GeoPoint(43.624268, 5.209955),
        GeoPoint(43.624313, 5.210003),
        GeoPoint(43.624336, 5.210145),
        GeoPoint(43.624329, 5.210363),
        GeoPoint(43.624369, 5.210387),
        GeoPoint(43.624566, 5.210347)
    )
    return polygon
}

private fun createLabelClairieres(mapView: MapView): Marker {
    return Marker(mapView).apply {
        position = GeoPoint(43.623191, 5.212626)
        textLabelBackgroundColor = Color.TRANSPARENT
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setTextIcon("La Clairière")
    }
}

private fun createPolygonBoisDesPins(): Polygon {
    val polygon = Polygon()
    polygon.setOnClickListener { _, _, _ -> false }
    polygon.fillPaint.color = Color.argb(140, 197, 226, 157)
    polygon.outlinePaint.apply {
        color = Color.rgb(197, 226, 157)
        strokeWidth = 10f
    }
    polygon.points = listOf(
        GeoPoint(43.621812, 5.214732),
        GeoPoint(43.621305, 5.213766),
        GeoPoint(43.621111, 5.213367),
        GeoPoint(43.621054, 5.213187),
        GeoPoint(43.621008, 5.21309),
        GeoPoint(43.620957, 5.212895),
        GeoPoint(43.621027, 5.212452),
        GeoPoint(43.621338, 5.211905),
        GeoPoint(43.621496, 5.211428),
        GeoPoint(43.621678, 5.21047),
        GeoPoint(43.621904, 5.209697),
        GeoPoint(43.622094, 5.209335),
        GeoPoint(43.622243, 5.208971),
        GeoPoint(43.622583, 5.209317),
        GeoPoint(43.622702, 5.209555),
        GeoPoint(43.6227, 5.209665),
        GeoPoint(43.622836, 5.209681),
        GeoPoint(43.622747, 5.209727),
        GeoPoint(43.622694, 5.209773),
        GeoPoint(43.622525, 5.210121),
        GeoPoint(43.622469, 5.210288),
        GeoPoint(43.622434, 5.21047),
        GeoPoint(43.62237, 5.210508),
        GeoPoint(43.622325, 5.21062),
        GeoPoint(43.622321, 5.210668),
        GeoPoint(43.622271, 5.210811),
        GeoPoint(43.622185, 5.210942),
        GeoPoint(43.622038, 5.211068),
        GeoPoint(43.621999, 5.211122),
        GeoPoint(43.621981, 5.211181),
        GeoPoint(43.622001, 5.211572),
        GeoPoint(43.622012, 5.211631),
        GeoPoint(43.622123, 5.211894),
        GeoPoint(43.622486, 5.212077),
        GeoPoint(43.622508, 5.212243),
        GeoPoint(43.622502, 5.212439),
        GeoPoint(43.622451, 5.212696),
        GeoPoint(43.622403, 5.212766),
        GeoPoint(43.622304, 5.212892),
        GeoPoint(43.622271, 5.212959),
        GeoPoint(43.622197, 5.213249),
        GeoPoint(43.622032, 5.213259),
        GeoPoint(43.621956, 5.213225),
        GeoPoint(43.621874, 5.213445),
        GeoPoint(43.621874, 5.213868),
        GeoPoint(43.621779, 5.213962),
        GeoPoint(43.621707, 5.2143),
        GeoPoint(43.621811, 5.214729)
    )
    return polygon
}

private fun createLabelBoisDesPins(mapView: MapView): Marker {
    return Marker(mapView).apply {
        position = GeoPoint(43.621517, 5.211924)
        textLabelBackgroundColor = Color.TRANSPARENT
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setTextIcon("Le Bois des Pins")
    }
}

private fun createPolygonBergerie(): Polygon {
    val polygon = Polygon()
    polygon.setOnClickListener { _, _, _ -> false }
    polygon.fillPaint.color = Color.argb(140, 112, 213, 194)
    polygon.outlinePaint.apply {
        color = Color.rgb(112, 213, 194)
        strokeWidth = 10f
    }
    polygon.points = listOf(
        GeoPoint(43.626005, 5.204261),
        GeoPoint(43.626139, 5.204247),
        GeoPoint(43.626127, 5.203987),
        GeoPoint(43.625966, 5.204003),
        GeoPoint(43.625978, 5.204258),
        GeoPoint(43.625993, 5.204258)

    )
    return polygon
}

private fun createLabelBergerie(mapView: MapView): Marker {
    return Marker(mapView).apply {
        position = GeoPoint(43.626052, 5.204129)
        textLabelBackgroundColor = Color.TRANSPARENT
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setTextIcon("La Bergerie des Reptiles")
    }
}