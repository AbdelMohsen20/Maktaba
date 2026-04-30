package com.ElOuedUniv.maktaba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.ElOuedUniv.maktaba.data.local.UserPreferences
import com.ElOuedUniv.maktaba.presentation.navigation.NavGraph
import com.ElOuedUniv.maktaba.presentation.navigation.Screen
import com.ElOuedUniv.maktaba.presentation.theme.MaktabaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            var startDestination by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(Unit) {
                val completed = userPreferences.hasCompletedOnboarding.first()
                startDestination = if (completed) Screen.BookList.route else Screen.Onboarding.route
            }

            MaktabaTheme {
                if (startDestination != null) {
                    NavGraph(startDestination = startDestination!!)
                }
            }
        }
    }
}
