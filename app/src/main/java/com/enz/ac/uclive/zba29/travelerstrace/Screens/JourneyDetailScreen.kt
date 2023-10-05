package com.enz.ac.uclive.zba29.travelerstrace.Screens


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.JourneyDetailViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyDetailScreen(
    journeyId: String?,
    navController: NavController,
    journeyDetailViewModel: JourneyDetailViewModel,
    sharePhotoIntent: (File) -> Unit
) {
    journeyId?.let { journeyDetailViewModel.getJourneyById(it.toLong()) }
    val journey = journeyDetailViewModel.currentJourney
    val journeyImagePainter = rememberImagePainter(data = journey?.image)


    if (journey == null) {
        navController.navigate(Screen.MainScreen.route)
        Toast.makeText(LocalContext.current, "Journey does not exist \uD83D\uDE22", Toast.LENGTH_SHORT).show()
    } else {
        Scaffold(
            topBar = {
                TopAppBar (
                    title = { Text("Map") },
                    navigationIcon = {
                        IconButton(onClick = {navController.navigate(Screen.MainScreen.route)}) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ){
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item{
                            Column (
                                modifier = Modifier.fillMaxWidth(0.9f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp)),
                                    painter = journeyImagePainter,
                                    contentDescription = "Journey Image",
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = journey.title,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = journey.date,
                                            color = Color.LightGray,
                                            fontSize = 13.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { /*TODO: Call the share photo intent*/ },
                                        content = {
                                            Icon(
                                                imageVector = Icons.Sharp.Share,
                                                contentDescription = "Share Icon",
                                                modifier = Modifier.fillMaxSize(0.2f)
                                            )
                                        }

                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = journey.description
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
