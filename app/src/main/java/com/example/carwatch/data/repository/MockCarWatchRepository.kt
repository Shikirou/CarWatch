package com.example.carwatch.data.repository

import android.location.Location
import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import com.example.carwatch.domain.repository.CarWatchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockCarWatchRepository @Inject constructor() : CarWatchRepository {

    private val mockAgencies = listOf(
        Agency("1", "Auto Prime", "12.345.678/0001-90", "https://placehold.co/100x100?text=Auto+Prime", -23.5505, -46.6333, 4.8f, 120),
        Agency("2", "Mega Car", "98.765.432/0001-10", "https://placehold.co/100x100?text=Mega+Car", -23.5555, -46.6388, 4.2f, 85),
        Agency("3", "Luxury Motors", "11.222.333/0001-44", "https://placehold.co/100x100?text=Luxury", -23.5400, -46.6200, 4.9f, 45),
        Agency("4", "Far Away Cars", "00.000.000/0001-00", "https://placehold.co/100x100?text=Far", -22.9068, -43.1729, 3.5f, 10)
    )

    private val _vehicles = MutableStateFlow(listOf(
        Vehicle(
            id = "1", agencyId = "1", brand = "Tesla", model = "Model 3", version = "Long Range",
            year = 2024, mileage = 0, price = 42990.0, originalPrice = 45000.0,
            transmission = "Automático", fuel = "Elétrico",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Tesla+Model+3"),
            description = "Elegante e inconfundivelmente único, o novo Tesla Model 3 é a silhueta da nova era da mobilidade elétrica.",
            location = "São Paulo, SP", rating = 4.9f, reviewCount = 124, statusTag = "VENDA",
            specs = mapOf("Aceleração" to "4.4s", "Velocidade Máxima" to "220 Km/h"),
            color = "Branco"
        ),
        Vehicle(
            id = "2", agencyId = "1", brand = "BMW", model = "X5 M50i", version = "Edição 2024",
            year = 2024, mileage = 1200, price = 89500.0, originalPrice = 98500.0,
            transmission = "Automático", fuel = "Gasolina",
            imageUrlsList = listOf("https://placehold.co/600x400?text=BMW+X5"),
            description = "O BMW X5 M50i combina desempenho e luxo de uma forma que só a BMW consegue proporcionar.",
            location = "São Caetano, SP", rating = 4.8f, reviewCount = 85, statusTag = "OFERTA",
            specs = mapOf("Aceleração" to "4.1s", "Velocidade Máxima" to "250 Km/h"),
            color = "Preto"
        ),
        Vehicle(
            id = "3", agencyId = "2", brand = "Mercedes Benz", model = "S 680 Guard", version = "Exclusivo",
            year = 2023, mileage = 5000, price = 120000.0,
            transmission = "Automático", fuel = "Gasolina",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Mercedes+S+Guard"),
            description = "Carro exclusivo com o mais alto nível de blindagem e proteção.",
            location = "Barueri, SP", rating = 4.5f, reviewCount = 13, statusTag = "NOVO",
            specs = mapOf("Aceleração" to "4.4s", "Velocidade Máxima" to "210 Km/h"),
            color = "Cinza"
        ),
        Vehicle(
            id = "4", agencyId = "3", brand = "Volkswagen", model = "Golf GTI", version = "Performance",
            year = 2024, mileage = 0, price = 25000.0,
            transmission = "Manual", fuel = "Gasolina",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Golf+GTI"),
            description = "O Golf GTI é a essência do hatch esportivo.",
            location = "Santos, SP", rating = 4.3f, reviewCount = 56,
            specs = mapOf("Aceleração" to "6.2s", "Velocidade Máxima" to "240 Km/h"),
            color = "Vermelho"
        ),
        Vehicle(
            id = "5", agencyId = "1", brand = "Ferrari", model = "F8 Tributo", version = "V8 Turbo",
            year = 2023, mileage = 500, price = 350000.0,
            transmission = "Automático", fuel = "Gasolina",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Ferrari+F8"),
            description = "O motor V8 mais premiado do mundo agora em um design icônico.",
            location = "São Paulo, SP", rating = 5.0f, reviewCount = 42, statusTag = "EXCLUSIVO",
            specs = mapOf("Aceleração" to "2.9s", "Velocidade Máxima" to "340 Km/h"),
            color = "Vermelho"
        ),
        Vehicle(
            id = "6", agencyId = "2", brand = "Audi", model = "RS e-tron GT", version = "Quattro",
            year = 2024, mileage = 0, price = 110000.0,
            transmission = "Automático", fuel = "Elétrico",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Audi+RS+GT"),
            description = "O futuro da performance elétrica com o DNA Audi Sport.",
            location = "Campinas, SP", rating = 4.9f, reviewCount = 28, statusTag = "NOVO",
            specs = mapOf("Aceleração" to "3.3s", "Velocidade Máxima" to "250 Km/h"),
            color = "Azul"
        ),
        Vehicle(
            id = "7", agencyId = "3", brand = "Porsche", model = "911 Carrera S", version = "992",
            year = 2024, mileage = 100, price = 155000.0,
            transmission = "PDK", fuel = "Gasolina",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Porsche+911"),
            description = "O ícone atemporal da Porsche em sua versão mais refinada.",
            location = "Curitiba, PR", rating = 4.9f, reviewCount = 67, statusTag = "VENDA",
            specs = mapOf("Aceleração" to "3.5s", "Velocidade Máxima" to "308 Km/h"),
            color = "Prata"
        ),
        Vehicle(
            id = "8", agencyId = "1", brand = "Tesla", model = "Model Y", version = "Performance",
            year = 2024, mileage = 0, price = 52000.0,
            transmission = "Automático", fuel = "Elétrico",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Tesla+Model+Y"),
            description = "Espaço, segurança e performance extrema em um SUV elétrico.",
            location = "Florianópolis, SC", rating = 4.7f, reviewCount = 89, statusTag = "NOVO",
            specs = mapOf("Aceleração" to "3.7s", "Velocidade Máxima" to "250 Km/h"),
            color = "Azul"
        ),
        Vehicle(
            id = "9", agencyId = "2", brand = "BMW", model = "M3 Competition", version = "G80",
            year = 2023, mileage = 3000, price = 95000.0,
            transmission = "Automático", fuel = "Gasolina",
            imageUrlsList = listOf("https://placehold.co/600x400?text=BMW+M3"),
            description = "Pura adrenalina e precisão alemã nas pistas e nas ruas.",
            location = "Rio de Janeiro, RJ", rating = 4.8f, reviewCount = 45, statusTag = "OFERTA",
            specs = mapOf("Aceleração" to "3.9s", "Velocidade Máxima" to "290 Km/h"),
            color = "Cinza"
        ),
        Vehicle(
            id = "10", agencyId = "3", brand = "Toyota", model = "Hilux SRX", version = "4x4 Turbo",
            year = 2024, mileage = 0, price = 65000.0,
            transmission = "Automático", fuel = "Diesel",
            imageUrlsList = listOf("https://placehold.co/600x400?text=Toyota+Hilux"),
            description = "A picape mais robusta e confiável do mercado brasileiro.",
            location = "Goiânia, GO", rating = 4.6f, reviewCount = 112, statusTag = "VENDA",
            specs = mapOf("Potência" to "204 cv", "Torque" to "50.9 kgfm"),
            color = "Branco"
        )
    ))

    override fun getNearbyAgencies(latitude: Double, longitude: Double, radiusKm: Int): Flow<List<Agency>> = flow {
        delay(1000)
        val filtered = mockAgencies.filter { agency ->
            val results = FloatArray(1)
            Location.distanceBetween(latitude, longitude, agency.latitude, agency.longitude, results)
            val distanceInKm = results[0] / 1000
            distanceInKm <= radiusKm
        }
        emit(filtered)
    }

    override fun getFeaturedOffers(): Flow<List<Vehicle>> = _vehicles.asStateFlow()

    override fun getAgencyVehicles(agencyId: String): Flow<List<Vehicle>> = 
        _vehicles.map { list -> list.filter { it.agencyId == agencyId } }

    override fun searchVehicles(query: String): Flow<List<Vehicle>> = 
        _vehicles.map { list -> 
            list.filter { it.model.contains(query, ignoreCase = true) || it.brand.contains(query, ignoreCase = true) } 
        }

    override suspend fun toggleFavorite(vehicleId: String) {
        val currentList = _vehicles.value
        val newList = currentList.map { 
            if (it.id == vehicleId) it.copy(isFavorite = !it.isFavorite) else it 
        }
        _vehicles.value = newList
    }
}
