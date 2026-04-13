package com.mamon.geolocationkmp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import model.*
import rememberGeoLocation


@Composable
fun GeolocationScreen() {
    val scope = rememberCoroutineScope()
    val geoLocation = rememberGeoLocation(agentName = "example@gmail.com", languageCode = "en")
    var geoMarker: GeoMarker? by remember { mutableStateOf(null) }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text("Geolocation KMP Debugger", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            DebugCard(title = "Location Details") {
                Column {


                    InfoRow("Full Address", geoMarker?.fullAddress?.takeIf { it.isNotBlank() } ?: "No Data...")

                    InfoRow("Point of Interest", geoMarker?.pointOfInterest?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("House Number", geoMarker?.houseNumber?.takeIf { it.isNotBlank() } ?:  "No Data...")
                    InfoRow("Street", geoMarker?.street?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("Neighbourhood", geoMarker?.neighbourhood?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("Suburb", geoMarker?.suburb?.takeIf { it.isNotBlank() } ?: "No Data...")

                    InfoRow("City", geoMarker?.city?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("County/District", geoMarker?.county?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("State", geoMarker?.state?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("Postal Code", geoMarker?.postalCode?.takeIf { it.isNotBlank() } ?: "No Data...")

                    InfoRow("Country", geoMarker?.country?.takeIf { it.isNotBlank() } ?: "No Data...")
                    InfoRow("Country Code", geoMarker?.countryCode?.takeIf { it.isNotBlank() } ?: "No Data...")

                    InfoRow("Geocoded Lat", geoMarker?.latitude?.takeIf { it != 0.0 }?.toString() ?: "No Data...")
                    InfoRow("Geocoded Lon", geoMarker?.longitude?.takeIf { it != 0.0 }?.toString() ?: "No Data...")

                }
            }
        }


        item {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Button(
                    onClick = {
                        scope.launch { geoMarker = geoLocation.findLocation() }
                    }
                ) {
                    Text(text = "Get Current Location")
                }

            }
        }

    }
}

@Composable
fun DebugCard(title: String, content: @Composable () -> Unit) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}