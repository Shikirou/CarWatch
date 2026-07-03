package com.example.carwatch.ui.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwatch.ui.theme.LogoRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(onBack: () -> Unit, onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    
    // Form States
    var placa by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var quilometragem by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vender Veículo", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { if (step > 1) step-- else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LinearProgressIndicator(
                progress = { step / 5f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = LogoRed,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()).weight(1f)) {
                Text("Passo $step de 5", color = LogoRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                when (step) {
                    1 -> StepIdentification(placa, { placa = it }, marca, { marca = it }, modelo, { modelo = it })
                    2 -> StepDetails(quilometragem, { quilometragem = it })
                    3 -> StepMedia()
                    4 -> StepPrice(preco, { preco = it }, descricao, { descricao = it })
                    5 -> StepReview(marca, modelo, quilometragem, preco)
                }
            }
            
            Button(
                onClick = { if (step < 5) step++ else onFinish() },
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogoRed)
            ) {
                Text(if (step < 5) "Próximo Passo" else "Publicar Anúncio", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepIdentification(
    placa: String, onPlacaChange: (String) -> Unit,
    marca: String, onMarcaChange: (String) -> Unit,
    modelo: String, onModeloChange: (String) -> Unit
) {
    Column {
        Text("Identificação Básica", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = placa, onValueChange = onPlacaChange, 
            label = { Text("Placa (Opcional)") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = marca, onValueChange = onMarcaChange, 
            label = { Text("Marca") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = modelo, onValueChange = onModeloChange, 
            label = { Text("Modelo") }, 
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StepDetails(quilometragem: String, onKmChange: (String) -> Unit) {
    Column {
        Text("Detalhes e Condição", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = quilometragem, onValueChange = onKmChange, 
            label = { Text("Quilometragem (KM)") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Opcionais", fontWeight = FontWeight.Bold)
        var airCond by remember { mutableStateOf(true) }
        var sunRoof by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = airCond, onCheckedChange = { airCond = it })
            Text("Ar-condicionado")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = sunRoof, onCheckedChange = { sunRoof = it })
            Text("Teto Solar")
        }
    }
}

@Composable
fun StepMedia() {
    Column {
        Text("Fotos e Vídeos", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Adicione de 3 a 15 fotos. A primeira será a capa.", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp), tint = LogoRed)
                Text("Toque para subir fotos", color = LogoRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepPrice(
    preco: String, onPrecoChange: (String) -> Unit,
    descricao: String, onDescricaoChange: (String) -> Unit
) {
    Column {
        Text("Preço e Descrição", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = preco, onValueChange = onPrecoChange, 
            label = { Text("Preço Pretendido") }, 
            modifier = Modifier.fillMaxWidth()
        )
        Text("Média FIPE: R$ 45.200,00", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = descricao, onValueChange = onDescricaoChange, 
            label = { Text("Descrição detalhada") }, 
            modifier = Modifier.fillMaxWidth().height(150.dp),
            maxLines = 5
        )
    }
}

@Composable
fun StepReview(marca: String, modelo: String, km: String, preco: String) {
    Column {
        Text("Revisão", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Confira os dados antes de publicar.", color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("$marca $modelo".ifBlank { "Veículo Sem Nome" }, fontWeight = FontWeight.Bold)
                Text("2024 • $km km", color = Color.Gray)
                Text("R$ $preco".ifBlank { "R$ 0,00" }, color = LogoRed, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
