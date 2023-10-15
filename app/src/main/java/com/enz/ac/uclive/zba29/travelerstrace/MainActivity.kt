package com.enz.ac.uclive.zba29.travelerstrace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.enz.ac.uclive.zba29.travelerstrace.Screens.*
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.CameraScreenViewModel
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.JourneyDetailViewModel
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MainViewModel
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MapViewModel
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.OnJourneyViewModel
import com.enz.ac.uclive.zba29.travelerstrace.datastore.StoreSettings
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings
import com.enz.ac.uclive.zba29.travelerstrace.service.TrackingService
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.TravelersTraceTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    private fun checkAndRequestPermissions() {
        val permissionToRequest = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val permissionsNotGranted = ArrayList<String>()

        for (permission in permissionToRequest) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission)
            }
        }

        if (permissionsNotGranted.isNotEmpty()) {
            multiplePermissionsLauncher.launch(permissionsNotGranted.toTypedArray())
        }
    }

    // Create a subdirectory within external storage, if not available then use the in app storage
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun sharePhotoIntent(photos: List<Photo>) {
        val filesToSend: List<String> = photos.map { it.filePath }.toList()
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(Intent.EXTRA_SUBJECT, "These are my journey photos.")
        intent.type = "image/jpeg" // This example is sharing jpeg images

        val files = ArrayList<Uri>()
        for (path in filesToSend) {
            val file = File(path)
            val uri: Uri = FileProvider.getUriForFile(
                this, "com.enz.ac.uclive.zba29.travelerstrace.fileprovider", file
            )
            files.add(uri)
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(intent)
    }



    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val mapViewModel: MapViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val cameraViewModel: CameraScreenViewModel by viewModels()
    private val onJourneyViewModel: OnJourneyViewModel by viewModels()
    private val journeyDetailViewModel: JourneyDetailViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkAndRequestPermissions()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()


        setContent {
            val navController = rememberNavController()

            fun onStartTracking(journeyId: Long) {
                mainViewModel.journeyId = journeyId.toString()
                Intent(applicationContext, TrackingService::class.java).also {
                    it.action = TrackingService.Actions.START.toString()
                    it.putExtra("JOURNEY_ID", journeyId)
                    startService(it)
                }
            }

            fun onStopTracking() {
                mainViewModel.journeyId = null
                Intent(applicationContext, TrackingService::class.java).also {
                    it.action = TrackingService.Actions.STOP.toString()
                    startService(it)
                }
            }

            val scope = rememberCoroutineScope()
            val settingsStore = StoreSettings.getInstance(LocalContext.current)
            var isDark by remember { mutableStateOf(false) }
            var settings by remember {
                mutableStateOf<Settings?>(Settings(isDark = true, metric = "Metric", language = "English", trackingInterval = "5s"))
            }

            scope.launch {
                settings = settingsStore.getSettings().first()
                isDark = settings!!.isDark
            }

            fun updateSettings(newSettings: Settings) {
                scope.launch {
                    settingsStore.setSettings(newSettings)
                }
                isDark = newSettings.isDark
            }

            TravelersTraceTheme(
                darkTheme = isDark
            ) {
                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
                    composable(route = Screen.MainScreen.route) {
                        MainScreen(navController = navController, viewModel = mainViewModel, onStart = { journeyId -> onStartTracking(journeyId) }, settings = settings!!)
                    }
                    composable(route = Screen.MapScreen.route,
                    ) {
                        MapScreen(navController = navController, viewModel = mapViewModel, fusedLocationProviderClient = fusedLocationProviderClient)
                    }
                    composable(route = Screen.SettingsScreen.route) {
                        SettingsScreen(navController = navController, currentSettings = settings!!, onSettingsChange = {newSettings -> updateSettings(newSettings)})
                    }
                    composable(
                        route = Screen.JourneyDetailScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                            entry ->
                        JourneyDetailScreen(
                            journeyId = entry.arguments?.getString("journeyId"),
                            navController = navController,
                            journeyDetailViewModel = journeyDetailViewModel,
                            settings = settings!!,
                            sharePhotosIntent = ::sharePhotoIntent
                        )
                    }
                    composable(route = Screen.OnJourneyScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                            entry ->
                        OnJourneyScreen(journeyId = entry.arguments?.getString("journeyId"), navController = navController, onJourneyViewModel = onJourneyViewModel, onStop = { onStopTracking() })
                    }
                    composable(
                        route = Screen.CameraScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                            entry ->
                        CameraScreen(
                            navController = navController,
                            outputDirectory = outputDirectory,
                            cameraExecutor = cameraExecutor,
                            cameraViewModel = cameraViewModel,
                            journeyId = entry.arguments?.getString("journeyId")
                        )
                    }
                }
            }
        }
    }
}