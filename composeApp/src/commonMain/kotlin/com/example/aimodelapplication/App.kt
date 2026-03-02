package com.example.aimodelapplication

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val DarkBlue = Color(0xFF0D1B2A)
private val LightBlue = Color(0xFF769FCD)
private val AccentColor = Color(0xFF00F5D4)
private val TextColor = Color(0xFFE0E1DD)
private val SurfaceColor = Color(0xFF1B263B)

private val futuristicColorScheme = darkColorScheme(
    primary = AccentColor,
    onPrimary = Color.Black,
    background = DarkBlue,
    onBackground = TextColor,
    surface = SurfaceColor,
    onSurface = TextColor,
    surfaceVariant = Color(0xFF415A77),
    onSurfaceVariant = LightBlue,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: AppViewModel = viewModel { AppViewModel() }) {
    var promptView by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val models = listOf(
        "gemini-3.1-pro-preview",
        "gemini-3-flash-preview",
        "gemini-2.5-pro",
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite",
        "gemini-3-pro-image-preview",
        "gemini-3-flash-image-preview",
        "gemini-2.5-pro-image",
        "gemini-2.5-flash-image",
        "imagen-4.0-generate-001",
        "imagen-4.0-fast-generate-001",
        "imagen-4.0-ultra-generate-001"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf(models[0]) }

    var isLoading by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }

    val result = viewModel.generationResult
    val history = viewModel.storedResponses

    LaunchedEffect(result) {
        if (result !is GenerationResult.None) {
            isLoading = false
        }
    }

    MaterialTheme(colorScheme = futuristicColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            if (showHistory) "Interaction History" else "Gemini AI Explorer", 
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        ) 
                    },
                    actions = {
                        TextButton(
                            onClick = { showHistory = !showHistory },
                            colors = ButtonDefaults.textButtonColors(contentColor = AccentColor)
                        ) {
                            Icon(
                                imageVector = if (showHistory) Icons.Default.Home else Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (showHistory) "Home" else "History")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = LightBlue
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            containerColor = Color.Transparent,
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0d1b2a), Color(0xFF1b263b), Color(0xFF415a77))
                )
            )
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                AnimatedContent(
                    targetState = showHistory,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    }
                ) { isHistory ->
                    if (isHistory) {
                        HistoryView(history)
                    } else {
                        MainView(
                            promptView,
                            { promptView = it },
                            selectedModel,
                            { selectedModel = it },
                            expanded,
                            { expanded = it },
                            models,
                            isLoading,
                            {
                                isLoading = true
                                viewModel.generateContent(promptView, selectedModel)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            result
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryView(history: List<StoredResponse>) {
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.History, 
                    contentDescription = null, 
                    modifier = Modifier.size(64.dp), 
                    tint = LightBlue.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(16.dp))
                Text("No history yet", color = LightBlue.copy(alpha = 0.5f))
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(history) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(16.dp),
                    border = borderStroke()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (item.type == "error") Color.Red else AccentColor)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.model,
                                style = MaterialTheme.typography.labelMedium,
                                color = LightBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.prompt,
                            fontWeight = FontWeight.Bold,
                            color = TextColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = LightBlue.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.response,
                            color = TextColor.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    prompt: String,
    onPromptChange: (String) -> Unit,
    selectedModel: String,
    onModelSelect: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    models: List<String>,
    isLoading: Boolean,
    onGenerate: () -> Unit,
    result: GenerationResult
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
            TextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                value = selectedModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selected Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(12.dp),
                colors = futuristicTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(SurfaceColor).clip(RoundedCornerShape(12.dp))
            ) {
                models.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model) },
                        onClick = {
                            onModelSelect(model)
                            onExpandedChange(false)
                        },
                        colors = MenuDefaults.itemColors(textColor = TextColor)
                    )
                }
            }
        }

        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            label = { Text(if (selectedModel.contains("image")) "Describe the image..." else "Enter your prompt...") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = futuristicTextFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Button(
            onClick = onGenerate,
            enabled = !isLoading && prompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor, 
                contentColor = Color.Black,
                disabledContainerColor = SurfaceColor
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.Black, strokeWidth = 3.dp)
            } else {
                Text(
                    if (selectedModel.contains("image")) "Generate Image" else "Generate Text", 
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor.copy(alpha = 0.5f)).border(borderStroke()).padding(16.dp)
        ) {
            when (result) {
                is GenerationResult.Text -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item { 
                            Text(
                                text = result.text, 
                                color = TextColor,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            ) 
                        }
                    }
                }
                is GenerationResult.Image -> {
                    Image(
                        bitmap = result.bitmap, 
                        contentDescription = null, 
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)), 
                        contentScale = ContentScale.Fit
                    )
                }
                is GenerationResult.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Generation Failed", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(text = result.message, color = TextColor.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                    }
                }
                GenerationResult.None -> {
                    Text(
                        "Ready to explore? Enter a prompt and hit generate.", 
                        color = LightBlue.copy(alpha = 0.6f), 
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(
    1.dp,
    Brush.linearGradient(listOf(LightBlue.copy(alpha = 0.3f), AccentColor.copy(alpha = 0.3f)))
)

@Composable
private fun futuristicTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = TextColor,
    unfocusedTextColor = TextColor,
    focusedContainerColor = SurfaceColor.copy(alpha = 0.7f),
    unfocusedContainerColor = SurfaceColor.copy(alpha = 0.5f),
    cursorColor = AccentColor,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = AccentColor,
    unfocusedLabelColor = LightBlue,
)
