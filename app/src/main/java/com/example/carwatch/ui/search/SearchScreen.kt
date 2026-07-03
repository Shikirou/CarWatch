package com.example.carwatch.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwatch.domain.model.Vehicle
import com.example.carwatch.ui.components.PremiumSearchBar
import com.example.carwatch.ui.components.VehicleCard
import com.example.carwatch.ui.theme.LogoRed
import kotlin.math.roundToInt

enum class SortOption(val label: String) {
    RELEVANCE("Relevância"),
    PRICE_LOW_HIGH("Menor Preço"),
    PRICE_HIGH_LOW("Maior Preço"),
    YEAR_NEWEST("Mais Novos")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    vehicles: List<Vehicle>,
    initialBrand: String? = null,
    onBackClick: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf(SortOption.RELEVANCE) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    
    // Estados dos Filtros
    var selectedBrand by remember { mutableStateOf<String?>(initialBrand) }
    var minYear by remember { mutableFloatStateOf(2010f) }
    var maxMileage by remember { mutableFloatStateOf(200000f) }
    var selectedColor by remember { mutableStateOf<String?>(null) }

    val brands = listOf("Tesla", "BMW", "Ferrari", "Mercedes", "Audi", "Porsche", "Volkswagen", "Toyota")
    val colors = listOf("Preto", "Branco", "Cinza", "Vermelho", "Azul", "Prata")

    val filteredAndSortedVehicles = remember(vehicles, searchQuery, selectedSort, selectedBrand, minYear, maxMileage, selectedColor) {
        vehicles.filter { 
            (searchQuery.isEmpty() || it.model.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true)) &&
            (selectedBrand == null || it.brand.equals(selectedBrand, ignoreCase = true)) &&
            (it.year >= minYear.toInt()) &&
            (it.mileage <= maxMileage.toInt()) &&
            (selectedColor == null || it.color == selectedColor)
        }.sortedWith { a, b ->
            when (selectedSort) {
                SortOption.PRICE_LOW_HIGH -> a.price.compareTo(b.price)
                SortOption.PRICE_HIGH_LOW -> b.price.compareTo(a.price)
                SortOption.YEAR_NEWEST -> b.year.compareTo(a.year)
                SortOption.RELEVANCE -> 0
            }
        }
    }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortSheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "Buscar Veículos",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        PremiumSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onFilterClick = { showFilterSheet = true },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredAndSortedVehicles.size} Veículos encontrados",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            IconButton(
                onClick = { showSortSheet = true },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert, 
                    contentDescription = "Ordenar", 
                    modifier = Modifier.size(18.dp),
                    tint = if (selectedSort != SortOption.RELEVANCE) LogoRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredAndSortedVehicles) { vehicle ->
                VehicleCard(
                    vehicle = vehicle,
                    onClick = { onVehicleClick(vehicle.id) },
                    onFavoriteClick = { onFavoriteClick(vehicle.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Modal de Filtros
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = filterSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtros", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Limpar tudo", 
                        color = LogoRed, 
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { 
                            selectedBrand = null 
                            minYear = 2010f
                            maxMileage = 200000f
                            selectedColor = null
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Filtro de Marca
                Text("Marca", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    brands.forEach { brand ->
                        FilterChip(
                            selected = selectedBrand?.equals(brand, ignoreCase = true) == true,
                            onClick = { selectedBrand = if (selectedBrand?.equals(brand, ignoreCase = true) == true) null else brand },
                            label = { Text(brand) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LogoRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtro de Ano
                Text("Ano (A partir de: ${minYear.roundToInt()})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Slider(
                    value = minYear,
                    onValueChange = { minYear = it },
                    valueRange = 2000f..2025f,
                    steps = 25,
                    colors = SliderDefaults.colors(thumbColor = LogoRed, activeTrackColor = LogoRed)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filtro de KM
                Text("Km Máximo (${maxMileage.roundToInt()} km)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Slider(
                    value = maxMileage,
                    onValueChange = { maxMileage = it },
                    valueRange = 0f..200000f,
                    colors = SliderDefaults.colors(thumbColor = LogoRed, activeTrackColor = LogoRed)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filtro de Cor
                Text("Cor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        FilterChip(
                            selected = selectedColor == color,
                            onClick = { selectedColor = if (selectedColor == color) null else color },
                            label = { Text(color) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LogoRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LogoRed)
                ) {
                    Text("Aplicar Filtros", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // Modal de Ordenação
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = sortSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
                Text("Ordenar por", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                SortOption.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                selectedSort = option
                                showSortSheet = false
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option.label,
                            fontSize = 16.sp,
                            color = if (selectedSort == option) LogoRed else MaterialTheme.colorScheme.onSurface
                        )
                        if (selectedSort == option) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = LogoRed)
                        }
                    }
                }
            }
        }
    }
}
