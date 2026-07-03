package com.example.carwatch.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.carwatch.ui.theme.LogoRed
import kotlinx.coroutines.launch

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carwatch.domain.repository.ChatEntry

data class Message(val text: String, val isMe: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onBack: () -> Unit,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    var selectedChat by remember { mutableStateOf<ChatEntry?>(null) }
    val chats by viewModel.chats.collectAsState()

    if (selectedChat == null) {
        ChatListScreen(onBack, chats, onChatClick = { selectedChat = it })
    } else {
        ChatDetailScreen(chat = selectedChat!!, onBack = { selectedChat = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onBack: () -> Unit, chats: List<ChatEntry>, onChatClick: (ChatEntry) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mensagens", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (chats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma conversa iniciada", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(chats) { chat ->
                    ChatItem(chat, onClick = { onChatClick(chat) })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp)) {
            AsyncImage(
                model = chat.vehiclePhoto,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = chat.userPhoto,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(1.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(chat.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(chat.time, fontSize = 12.sp, color = Color.Gray)
            }
            Text(chat.vehicleName, fontSize = 13.sp, color = LogoRed, fontWeight = FontWeight.Medium)
            Text(
                chat.lastMessage, 
                fontSize = 14.sp, 
                color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onBackground else Color.Gray,
                maxLines = 1,
                fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
            )
        }
        
        if (chat.unreadCount > 0) {
            Surface(
                modifier = Modifier.padding(start = 8.dp).size(20.dp),
                shape = CircleShape,
                color = LogoRed
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(chat.unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(chat: ChatEntry, onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(
        Message("Olá! O ${chat.vehicleName} ainda está disponível?", true),
        Message("Olá! Sim, está disponível para visita amanhã.", false)
    ) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = chat.userPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(chat.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(chat.vehicleName, fontSize = 12.sp, color = LogoRed)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = null) }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Digite sua mensagem...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { 
                            if (messageText.isNotBlank()) {
                                messages.add(Message(messageText, true))
                                messageText = ""
                                scope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        },
                        modifier = Modifier.clip(CircleShape).background(if (messageText.isNotBlank()) LogoRed else Color.Gray)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(msg.text, msg.isMe)
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, isMe: Boolean) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(
            color = if (isMe) LogoRed else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp, 
                topEnd = 16.dp, 
                bottomStart = if (isMe) 16.dp else 0.dp, 
                bottomEnd = if (isMe) 0.dp else 16.dp
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
