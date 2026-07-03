package com.example.carwatch.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carwatch.ui.components.Brand
import com.example.carwatch.ui.components.BrandLogoList
import com.example.carwatch.ui.components.FloatingBottomNav
import com.example.carwatch.ui.components.PremiumHeroBanner
import com.example.carwatch.ui.components.PremiumSearchBar
import com.example.carwatch.ui.components.VehicleCard
import com.example.carwatch.domain.model.Vehicle

@Composable
fun HomeScreen(
    onVehicleClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    HomeScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchChange = { 
            searchQuery = it
            viewModel.onSearchQueryChanged(it)
        },
        onVehicleClick = onVehicleClick,
        onNavigate = onNavigate,
        onBrandClick = onBrandClick,
        onFavoriteClick = { vehicleId -> viewModel.toggleFavorite(vehicleId) }
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onVehicleClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            FloatingBottomNav(
                currentRoute = "home",
                onNavigate = onNavigate
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    HomeContent(
                        state = state,
                        searchQuery = searchQuery,
                        onSearchChange = onSearchChange,
                        onVehicleClick = onVehicleClick,
                        onNavigate = onNavigate,
                        onBrandClick = onBrandClick,
                        onFavoriteClick = onFavoriteClick
                    )
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onVehicleClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            PremiumHeroBanner(
                userName = state.currentUser?.displayName ?: "Usuário",
                userPhotoUrl = state.currentUser?.photoUrl,
                onProfileClick = { onNavigate("profile") },
                onNotificationClick = { onNavigate("notifications") },
                onExploreClick = { onNavigate("search") },
                onSellClick = { onNavigate("sell") }
            )
        }

        item {
            PremiumSearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange,
                onFilterClick = { onNavigate("search") },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-28).dp)
            )
        }

        item {
            BrandLogoList(
                brands = listOf(
                    Brand("Tesla", null),
                    Brand("BMW", null),
                    Brand("Ferrari", null),
                    Brand("Mercedes", null),
                    Brand("Audi", null),
                    Brand("Porsche", null),
                    Brand("Volkswagen", null),
                    Brand("Toyota", null)
                ),
                onBrandClick = { brand -> onBrandClick(brand.name) }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Carros em Destaque",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { onNavigate("search") }) {
                    Text(text = "Ver todos ↗", fontSize = 14.sp)
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(state.featuredOffers) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onClick = { onVehicleClick(vehicle.id) },
                        onFavoriteClick = { onFavoriteClick(vehicle.id) }
                    )
                }
            }
        }
    }
}
