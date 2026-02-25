package io.shubham0204.smollmandroid.ui.screens.ai_agents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Check
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import compose.icons.feathericons.Wifi
import io.shubham0204.smollmandroid.data.AIAgentsSettings
import io.shubham0204.smollmandroid.ui.components.MatrixRainBackground
import io.shubham0204.smollmandroid.ui.theme.MatrixBg
import io.shubham0204.smollmandroid.ui.theme.MatrixGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixGreenDark
import io.shubham0204.smollmandroid.ui.theme.MatrixGreenDim
import io.shubham0204.smollmandroid.ui.theme.MatrixError
import io.shubham0204.smollmandroid.ui.theme.MatrixOutline
import io.shubham0204.smollmandroid.ui.theme.MatrixSurface
import io.shubham0204.smollmandroid.ui.theme.MatrixSurfaceContainer
import io.shubham0204.smollmandroid.ui.theme.SmolLMAndroidTheme

class AIAgentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmolLMAndroidTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    AIAgentsScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAgentsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val settings = remember { AIAgentsSettings(context) }

    Scaffold(
        containerColor = MatrixBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "> AI АГЕНТЫ_",
                        color = MatrixGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(FeatherIcons.ArrowLeft, contentDescription = "Back", tint = MatrixGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MatrixSurface,
                ),
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MatrixRainBackground(modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD010D01))
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = "// Подключите облачные AI агенты. API ключи хранятся только на устройстве.",
                        color = MatrixGreenDim,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                items(AIAgentsSettings.Agent.entries) { agent ->
                    AgentCard(agent = agent, settings = settings)
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun AgentCard(agent: AIAgentsSettings.Agent, settings: AIAgentsSettings) {
    var expanded by remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf(settings.getApiKey(agent)) }
    var isEnabled by remember { mutableStateOf(settings.isEnabled(agent)) }
    var selectedModel by remember { mutableStateOf(settings.getSelectedModel(agent)) }
    var showKey by remember { mutableStateOf(false) }
    var modelMenuExpanded by remember { mutableStateOf(false) }
    var saveStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isEnabled) 1.5.dp else 1.dp,
                color = if (isEnabled) MatrixGreen else MatrixOutline,
                shape = RoundedCornerShape(4.dp),
            )
            .background(MatrixSurface, RoundedCornerShape(4.dp))
    ) {
        // ── HEADER ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Status dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (isEnabled) MatrixGreen else MatrixGreenDim,
                        RoundedCornerShape(4.dp)
                    )
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = agent.displayName,
                    color = if (isEnabled) MatrixGreen else MatrixGreenDark,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = if (isEnabled) "[АКТИВЕН] ${selectedModel}" else "[ОТКЛЮЧЁН]",
                    color = if (isEnabled) MatrixGreenDark else MatrixGreenDim,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                )
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = { checked ->
                    isEnabled = checked
                    settings.setEnabled(agent, checked)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MatrixGreen,
                    checkedTrackColor = MatrixGreenDim,
                    uncheckedThumbColor = MatrixGreenDim,
                    uncheckedTrackColor = MatrixSurfaceContainer,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                if (expanded) FeatherIcons.ChevronUp else FeatherIcons.ChevronDown,
                contentDescription = null,
                tint = MatrixGreenDark,
            )
        }

        // ── EXPANDED SETTINGS ──
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixSurfaceContainer)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // API Key field
                Text(
                    text = "> API_KEY",
                    color = MatrixGreenDark,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                )
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "sk-••••••••••••••••••••••",
                            color = MatrixGreenDim,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                        )
                    },
                    visualTransformation = if (showKey) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) FeatherIcons.EyeOff else FeatherIcons.Eye,
                                contentDescription = null,
                                tint = MatrixGreenDark,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MatrixGreen,
                        unfocusedBorderColor = MatrixOutline,
                        focusedTextColor = MatrixGreen,
                        unfocusedTextColor = MatrixGreenDark,
                        cursorColor = MatrixGreen,
                    ),
                    singleLine = true,
                )

                // Model selector
                Text(
                    text = "> МОДЕЛЬ",
                    color = MatrixGreenDark,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                )
                Box {
                    OutlinedTextField(
                        value = selectedModel,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { modelMenuExpanded = true },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                FeatherIcons.ChevronDown,
                                contentDescription = null,
                                tint = MatrixGreenDark,
                                modifier = Modifier.clickable { modelMenuExpanded = true },
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MatrixGreen,
                            unfocusedBorderColor = MatrixOutline,
                            focusedTextColor = MatrixGreen,
                            unfocusedTextColor = MatrixGreenDark,
                        ),
                    )
                    DropdownMenu(
                        expanded = modelMenuExpanded,
                        onDismissRequest = { modelMenuExpanded = false },
                        modifier = Modifier.background(MatrixSurfaceContainer),
                    ) {
                        agent.models.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        model,
                                        color = if (model == selectedModel) MatrixGreen else MatrixGreenDark,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                    )
                                },
                                onClick = {
                                    selectedModel = model
                                    settings.setSelectedModel(agent, model)
                                    modelMenuExpanded = false
                                },
                            )
                        }
                    }
                }

                // API URL info
                Text(
                    text = "> ENDPOINT: ${agent.apiUrl.take(45)}${if (agent.apiUrl.length > 45) "..." else ""}",
                    color = MatrixGreenDim,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                )

                // Save + Test buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(
                        onClick = {
                            settings.setApiKey(agent, apiKey)
                            settings.setEnabled(agent, isEnabled)
                            settings.setSelectedModel(agent, selectedModel)
                            saveStatus = "[OK] Настройки сохранены"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MatrixGreen, RoundedCornerShape(4.dp)),
                    ) {
                        Icon(FeatherIcons.Check, contentDescription = null, tint = MatrixGreen)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "СОХРАНИТЬ",
                            color = MatrixGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                        )
                    }
                    TextButton(
                        onClick = {
                            saveStatus = if (apiKey.isNotEmpty())
                                "> Тест: key=${apiKey.take(8)}..."
                            else "> ОШИБКА: API ключ не задан"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MatrixOutline, RoundedCornerShape(4.dp)),
                    ) {
                        Icon(FeatherIcons.Wifi, contentDescription = null, tint = MatrixGreenDark)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "ТЕСТ",
                            color = MatrixGreenDark,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                        )
                    }
                }

                // Save status message
                saveStatus?.let { status ->
                    Text(
                        text = status,
                        color = if (status.startsWith("[OK]")) MatrixGreen else MatrixError,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}
