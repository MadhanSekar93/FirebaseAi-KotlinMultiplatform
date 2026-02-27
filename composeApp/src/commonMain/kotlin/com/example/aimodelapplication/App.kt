package com.example.aimodelapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Defining a futuristic color palette
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
fun App(onPromptChange: (String, String) -> Unit, onGenerateClick: () -> Unit, responseText: String) {
    var promptView by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val models = listOf(
        "gemini-3.1-pro-preview",
        "gemini-3-flash-preview",
        "gemini-2.5-pro",
        "gemini-2.5-flash",
        "gemini-2.0-pro",
        "gemini-2.0-flash",
        "gemini-1.5-pro",
        "gemini-1.5-pro-latest",
        "gemini-1.5-flash-latest",
        "gemini-pro",
        "gemini-pro-vision",
        "gemini-1.5-pro-001",
        "gemini-1.5-flash-001",
        "gemini-1.0-pro",
        "gemini-1.0-pro-001",
        "gemini-1.0-pro-002",
        "text-embedding-004",
        "embedding-001",
        "aqa"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf(models[0]) }

    var isLoading by remember { mutableStateOf(false) }

    // Reset loading state when a new response is received
    LaunchedEffect(responseText) {
        if(responseText.isNotBlank()) isLoading = false
    }

    MaterialTheme(colorScheme = futuristicColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gemini AI Explorer", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Makes it blend with background
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Model Selector
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
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
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceColor).clip(RoundedCornerShape(12.dp))
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    selectedModel = model
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = TextColor,
                                )
                            )
                        }
                    }
                }

                // Prompt Input
                OutlinedTextField(
                    value = promptView,
                    onValueChange = { promptView = it },
                    label = { Text("Enter your prompt here...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = futuristicTextFieldColors(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }),
                    textStyle = TextStyle(fontSize = 16.sp)
                )

                // Generate Button
                Button(
                    onClick = {
                        isLoading = true
                        onPromptChange(promptView, selectedModel)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    enabled = !isLoading && promptView.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50), // Pill shape
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentColor,
                        contentColor = Color.Black,
                        disabledContainerColor = SurfaceColor,
                        disabledContentColor = LightBlue,
                    )
                ) {
                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.Black,
                            strokeWidth = 3.dp
                        )
                    }
                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Text("Generate Response", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Response Area
                AnimatedVisibility(
                    visible = true, // Always visible to maintain layout space, content changes inside
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceColor.copy(alpha = 0.5f))
                            .border(1.dp, Brush.linearGradient(listOf(LightBlue.copy(alpha=0.5f), AccentColor.copy(alpha=0.5f))), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        item {
                            if (responseText.isNotBlank()) {
                                Text(
                                    text = responseText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextColor
                                )
                            } else {
                                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                     Text(
                                        "AI response will appear here...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = LightBlue.copy(alpha = 0.8f)
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
private fun futuristicTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = TextColor,
    unfocusedTextColor = TextColor,
    focusedContainerColor = SurfaceColor.copy(alpha = 0.7f),
    unfocusedContainerColor = SurfaceColor.copy(alpha = 0.5f),
    cursorColor = AccentColor,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedLabelColor = AccentColor,
    unfocusedLabelColor = LightBlue,
)

@Preview
@Composable
fun AppPreview() {
    App(onPromptChange = { _, _ -> }, onGenerateClick = {}, responseText = "This is a preview of a futuristic AI response. The interface is clean and modern, with a dark theme and vibrant accent colors.")
}
