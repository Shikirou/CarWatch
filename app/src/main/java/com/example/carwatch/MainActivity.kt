package com.example.carwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.carwatch.ui.details.VehicleDetailScreen
import com.example.carwatch.ui.home.HomeUiState
import com.example.carwatch.ui.home.HomeViewModel
import com.example.carwatch.ui.home.HomeScreen
import com.example.carwatch.ui.login.LoginScreen
import com.example.carwatch.ui.search.SearchScreen
import com.example.carwatch.ui.splash.SplashScreen
import com.example.carwatch.ui.theme.CarWatchTheme
import com.example.carwatch.ui.theme.LogoRed
import com.example.carwatch.ui.components.FloatingBottomNav
import com.example.carwatch.ui.components.VehicleCard
import com.example.carwatch.ui.sell.SellScreen
import com.example.carwatch.ui.profile.MessagesScreen
import com.example.carwatch.ui.profile.NotificationsScreen
import com.example.carwatch.ui.profile.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.shape.RoundedCornerShape

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search?brand={brand}") {
        fun createRoute(brand: String? = null) = if (brand != null) "search?brand=$brand" else "search"
    }
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object Sell : Screen("sell")
    object Messages : Screen("messages")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object Details : Screen("details/{vehicleId}") {
        fun createRoute(vehicleId: String) = "details/$vehicleId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarWatchTheme {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeUiState by homeViewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route
                    ) {
                        composable(Screen.Splash.route) {
                            SplashScreen(
                                onNavigateToLogin = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onVehicleClick = { vehicleId ->
                                    navController.navigate(Screen.Details.createRoute(vehicleId))
                                },
                                onNavigate = { route ->
                                    navController.navigate(route)
                                },
                                onBrandClick = { brand ->
                                    navController.navigate(Screen.Search.createRoute(brand))
                                },
                                viewModel = homeViewModel
                            )
                        }
                        composable(
                            route = Screen.Search.route,
                            arguments = listOf(navArgument("brand") { 
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val brand = backStackEntry.arguments?.getString("brand")
                            val state = homeUiState
                            if (state is HomeUiState.Success) {
                                SearchScreen(
                                    vehicles = state.featuredOffers,
                                    initialBrand = brand,
                                    onBackClick = { navController.popBackStack() },
                                    onVehicleClick = { vehicleId ->
                                        navController.navigate(Screen.Details.createRoute(vehicleId))
                                    },
                                    onFavoriteClick = { vehicleId -> homeViewModel.toggleFavorite(vehicleId) }
                                )
                            }
                        }
                        composable(Screen.Favorites.route) {
                            val state = homeUiState
                            val favorites = if (state is HomeUiState.Success) {
                                state.featuredOffers.filter { it.isFavorite }
                            } else emptyList()
                            
                            FavoritesScreen(
                                vehicles = favorites,
                                onBackClick = { navController.popBackStack() },
                                onVehicleClick = { vehicleId ->
                                    navController.navigate(Screen.Details.createRoute(vehicleId))
                                },
                                onNavigate = { route -> navController.navigate(route) },
                                onFavoriteClick = { vehicleId -> homeViewModel.toggleFavorite(vehicleId) }
                            )
                        }
                        composable(Screen.Profile.route) {
                            val state = homeUiState
                            val user = if (state is HomeUiState.Success) state.currentUser else null
                            ProfileScreen(
                                user = user,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                        composable(Screen.Sell.route) {
                            SellScreen(
                                onBack = { navController.popBackStack() },
                                onFinish = { 
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Messages.route) {
                            MessagesScreen(onBack = { navController.popBackStack() })
                        }
                        composable(Screen.Notifications.route) {
                            NotificationsScreen(onBack = { navController.popBackStack() })
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(onBack = { navController.popBackStack() })
                        }
                        composable(
                            route = Screen.Details.route,
                            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val vehicleId = backStackEntry.arguments?.getString("vehicleId")
                            val state = homeUiState
                            if (state is HomeUiState.Success && vehicleId != null) {
                                val vehicle = state.featuredOffers.find { it.id == vehicleId }
                                
                                if (vehicle != null) {
                                    VehicleDetailScreen(
                                        vehicle = vehicle,
                                        onBackClick = { navController.popBackStack() },
                                        onFavoriteClick = { homeViewModel.toggleFavorite(vehicle.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    vehicles: List<com.example.carwatch.domain.model.Vehicle>,
    onBackClick: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            FloatingBottomNav(currentRoute = "favorites", onNavigate = onNavigate)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Meus Favoritos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            
            if (vehicles.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nenhum veículo favoritado", color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { onNavigate("search") },
                        colors = ButtonDefaults.buttonColors(containerColor = LogoRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explorar Veículos")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(vehicles.size) { index ->
                        VehicleCard(
                            vehicle = vehicles[index],
                            onClick = { onVehicleClick(vehicles[index].id) },
                            onFavoriteClick = { onFavoriteClick(vehicles[index].id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    user: com.example.carwatch.domain.repository.User?,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            FloatingBottomNav(currentRoute = "profile", onNavigate = onNavigate)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(40.dp))
            // Profile Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user?.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(user?.displayName ?: "Convidado", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(user?.email ?: "carwatch@exemplo.com", fontSize = 14.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Profile Actions
            ProfileMenuItem(Icons.Default.Email, "Mensagens", "Converse com vendedores") {
                onNavigate(Screen.Messages.route)
            }
            ProfileMenuItem(Icons.Default.Notifications, "Notificações", "Alertas de preços e outros modelos") {
                onNavigate(Screen.Notifications.route)
            }
            ProfileMenuItem(Icons.Default.Settings, "Configurações", "Preferências da conta") {
                onNavigate(Screen.Settings.route)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = LogoRed, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
