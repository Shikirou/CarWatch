package com.example.carwatch.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwatch.R
import com.example.carwatch.ui.theme.LogoBlack
import com.example.carwatch.ui.theme.LogoRed

data class DrawerItem(val title: String, val icon: ImageVector)

@Composable
fun AppDrawer(
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        DrawerItem("Perfil", Icons.Default.Person),
        DrawerItem("Meus Veículos", Icons.Default.DirectionsCar),
        DrawerItem("Mensagens", Icons.Default.Chat),
        DrawerItem("Favoritos", Icons.Default.Favorite),
        DrawerItem("Configurações", Icons.Default.Settings),
        DrawerItem("Sobre", Icons.Default.Info),
        DrawerItem("Sair", Icons.AutoMirrored.Filled.ExitToApp)
    )

    ModalDrawerSheet(
        modifier = modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LogoBlack)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "CarWatch Logo",
                    modifier = Modifier.size(70.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CarWatch",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Premium Automotive Hub",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title, fontSize = 16.sp) },
                selected = false,
                onClick = { onItemClick(item.title) },
                icon = { Icon(item.icon, contentDescription = null, tint = LogoRed) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = LogoRed,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    AppDrawer(onItemClick = {})
}
