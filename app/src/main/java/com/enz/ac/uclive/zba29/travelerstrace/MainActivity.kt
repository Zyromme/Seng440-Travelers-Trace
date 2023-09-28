package com.enz.ac.uclive.zba29.travelerstrace


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.enz.ac.uclive.zba29.travelerstrace.dat.FakeDatabase
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.TravelersTraceTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.enz.ac.uclive.zba29.travelerstrace.Screens.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Create a subdirectory within external storage, if not available then use the in app storage
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    @androidx.annotation.OptIn(androidx.camera.view.video.ExperimentalVideo::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        //TODO
        val isDark = mutableStateOf(false)

        fun toggleTheme(boolean: Boolean) {
            isDark.value = boolean
        }

        setContent {
            TravelersTraceTheme(
                darkTheme = isDark.value
            ) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
                    composable(route = Screen.MainScreen.route) {
                        MainScreen(navController = navController, FakeDatabase.journeyList)
                    }
                    composable(
                        route = Screen.MapScreen.route,
                    ) {
                        MapScreen(navController = navController)
                    }
                    composable(route = Screen.SettingsScreen.route) {
                        SettingsScreen(
                            navController = navController,
                            currentTheme = isDark.value,
                            onToggleTheme = { boolean -> toggleTheme(boolean) })
                    }
                    composable(
                        route = Screen.JourneyDetailScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) { entry ->
                        JourneyDetailScreen(
                            journeyId = entry.arguments?.getString("journeyId"),
                            navController = navController
                        )
                    }
                    composable(route = Screen.CameraScreen.route) {
                        CameraScreen(
                            navController = navController,
                            outputDirectory = outputDirectory,
                            cameraExecutor = cameraExecutor
                        )
                    }
                }
            }
        }
        requestCameraPermission()
    }
}