package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.CvEditorScreen
import com.example.ui.screens.CvPreviewScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JobSearchScreen
import com.example.ui.screens.SavedCvAndJobsScreen
import com.example.ui.theme.CvMakerTheme
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.CvViewModel
import com.example.ui.viewmodel.JobViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Beranda", Icons.Filled.Home, Icons.Outlined.Home)
    object Editor : Screen("editor", "Edit CV", Icons.Filled.Edit, Icons.Outlined.Edit)
    object Preview : Screen("preview", "Pratinjau", Icons.Filled.PictureAsPdf, Icons.Outlined.PictureAsPdf)
    object AiAssistant : Screen("ai_assistant", "AI Guide", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object JobSearch : Screen("jobs", "Cari Kerja", Icons.Filled.Search, Icons.Outlined.Search)
    object Saved : Screen("saved", "Tersimpan", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}

class MainActivity : ComponentActivity() {

    private val cvViewModel: CvViewModel by viewModels()
    private val jobViewModel: JobViewModel by viewModels()
    private val aiViewModel: AiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CvMakerTheme {
                MainAppStructure(
                    cvViewModel = cvViewModel,
                    jobViewModel = jobViewModel,
                    aiViewModel = aiViewModel
                )
            }
        }
    }
}

@Composable
fun MainAppStructure(
    cvViewModel: CvViewModel,
    jobViewModel: JobViewModel,
    aiViewModel: AiViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        Screen.Home,
        Screen.Editor,
        Screen.Preview,
        Screen.AiAssistant,
        Screen.JobSearch,
        Screen.Saved
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                tonalElevation = 8.dp
            ) {
                navigationItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        cvViewModel = cvViewModel,
                        jobViewModel = jobViewModel,
                        onNavigateToEditor = { navController.navigate(Screen.Editor.route) },
                        onNavigateToPreview = { navController.navigate(Screen.Preview.route) },
                        onNavigateToJobs = { navController.navigate(Screen.JobSearch.route) },
                        onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                        onNavigateToSaved = { navController.navigate(Screen.Saved.route) }
                    )
                }

                composable(Screen.Editor.route) {
                    CvEditorScreen(
                        cvViewModel = cvViewModel,
                        aiViewModel = aiViewModel,
                        onNavigateToPreview = { navController.navigate(Screen.Preview.route) }
                    )
                }

                composable(Screen.Preview.route) {
                    CvPreviewScreen(
                        cvViewModel = cvViewModel
                    )
                }

                composable(Screen.AiAssistant.route) {
                    AiAssistantScreen(
                        aiViewModel = aiViewModel,
                        cvViewModel = cvViewModel
                    )
                }

                composable(Screen.JobSearch.route) {
                    JobSearchScreen(
                        jobViewModel = jobViewModel
                    )
                }

                composable(Screen.Saved.route) {
                    SavedCvAndJobsScreen(
                        cvViewModel = cvViewModel,
                        jobViewModel = jobViewModel,
                        onNavigateToEditor = { navController.navigate(Screen.Editor.route) },
                        onNavigateToPreview = { navController.navigate(Screen.Preview.route) }
                    )
                }
            }
        }
    }
}
