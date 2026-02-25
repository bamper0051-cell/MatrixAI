package io.shubham0204.smollmandroid.ui.screens.ai_agents

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import io.shubham0204.smollmandroid.data.AIAgent
import io.shubham0204.smollmandroid.data.AIAgentsDB
import io.shubham0204.smollmandroid.ui.components.MatrixRainBackground
import io.shubham0204.smollmandroid.ui.theme.MatrixGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkBg
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkGreen
import io.shubham0204.smollmandroid.ui.theme.MatrixDarkSurface
import io.shubham0204.smollmandroid.ui.theme.MatrixLightGreen
import io.shubham0204.smollmandroid.ui.theme.SmolLMAndroidTheme
import org.koin.android.ext.android.inject

class AIAgentsActivity : ComponentActivity() {
    private val aiAgentsDB by inject<AIAgentsDB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmolLMAndroidTheme {
                AIAgentsScreen(
                    aiAgentsDB = aiAgentsDB,
                    onBackClick = { finish() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAgentsScreen(
    aiAgentsDB: AIAgentsDB,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val agents = remember { mutableStateListOf<AIAgent>() }
    var selectedAgent by remember { mutableStateOf<AIAgent?>(null) }

    // Load agents
    if (agents.isEmpty()) {
        agents.addAll(aiAgentsDB.getAgents())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI AGENTS",
                        color = MatrixGreen,
                        fontFamily = FontFamily.Monospace,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            FeatherIcons.ArrowLeft,
                            contentDescription = "Back",
                            tint = MatrixGreen,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A150A),
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MatrixDarkBg),
        ) {
            MatrixRainBackground(
                columnCount = 15,
                speedFactor = 0.3f,
                opacity = 0.1f,
            )

            if (selectedAgent != null) {
                AgentDetailScreen(
                    agent = selectedAgent!!,
                    onSave = { updatedAgent ->
                        aiAgentsDB.saveAgent(updatedAgent)
                        val index = agents.indexOfFirst { it.id == updatedAgent.id }
                        if (index >= 0) {
                            agents[index] = updatedAgent
                        }
                        selectedAgent = null
                        Toast.makeText(context, "Agent configuration saved", Toast.LENGTH_SHORT).show()
                    },
                    onBack = { selectedAgent = null },
                )
            } else {
                AgentListScreen(
                    agents = agents,
                    onAgentClick = { agent -> selectedAgent = agent },
                )
            }
        }
    }
}

@Composable
private fun AgentListScreen(
    agents: List<AIAgent>,
    onAgentClick: (AIAgent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "> SELECT AGENT TO CONFIGURE_",
                color = MatrixGreen.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(agents) { agent ->
            AgentListItem(agent = agent, onClick = { onAgentClick(agent) })
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Configure API keys to enable cloud AI agents.\nLocal models work without API keys.",
                color = MatrixDarkGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun AgentListItem(agent: AIAgent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MatrixDarkGreen, RoundedCornerShape(8.dp))
            .background(Color(0xFF0A150A), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Status indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    if (agent.enabled && agent.apiKey.isNotBlank()) MatrixGreen
                    else Color(0xFF333333)
                ),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = agent.name,
                color = MatrixGreen,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = if (agent.apiKey.isNotBlank()) {
                    "API Key: ${"*".repeat(8)}${agent.apiKey.takeLast(4)}"
                } else {
                    "Not configured"
                },
                color = if (agent.apiKey.isNotBlank()) MatrixDarkGreen else Color(0xFF555555),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = "Model: ${agent.modelName.ifBlank { agent.defaultModel }}",
                color = MatrixDarkGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
            )
        }

        Icon(
            compose.icons.FeatherIcons.ChevronRight,
            contentDescription = null,
            tint = MatrixDarkGreen,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun AgentDetailScreen(
    agent: AIAgent,
    onSave: (AIAgent) -> Unit,
    onBack: () -> Unit,
) {
    var apiKey by remember { mutableStateOf(agent.apiKey) }
    var baseUrl by remember { mutableStateOf(agent.baseUrl.ifBlank { agent.defaultBaseUrl }) }
    var modelName by remember { mutableStateOf(agent.modelName.ifBlank { agent.defaultModel }) }
    var enabled by remember { mutableStateOf(agent.enabled) }
    var showApiKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Back button
        Row(
            modifier = Modifier
                .clickable { onBack() }
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                FeatherIcons.ArrowLeft,
                contentDescription = "Back",
                tint = MatrixGreen,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "< BACK",
                color = MatrixGreen,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
            )
        }

        // Agent name header
        Text(
            text = "// ${agent.name.uppercase()}",
            color = MatrixLightGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Enabled toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "ENABLED",
                color = MatrixGreen,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
            )
            Switch(
                checked = enabled,
                onCheckedChange = { enabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MatrixLightGreen,
                    checkedTrackColor = MatrixDarkGreen,
                    uncheckedThumbColor = Color(0xFF555555),
                    uncheckedTrackColor = Color(0xFF222222),
                ),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Key
        Text(
            text = "API_KEY:",
            color = MatrixDarkGreen,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                modifier = Modifier.weight(1f),
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = {
                    Text("sk-...", color = Color(0xFF333333), fontFamily = FontFamily.Monospace)
                },
                colors = matrixTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
            )
            IconButton(onClick = { showApiKey = !showApiKey }) {
                Icon(
                    if (showApiKey) FeatherIcons.EyeOff else FeatherIcons.Eye,
                    contentDescription = "Toggle visibility",
                    tint = MatrixDarkGreen,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Base URL
        Text(
            text = "BASE_URL:",
            color = MatrixDarkGreen,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(agent.defaultBaseUrl, color = Color(0xFF333333), fontFamily = FontFamily.Monospace)
            },
            colors = matrixTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Model name
        Text(
            text = "MODEL_NAME:",
            color = MatrixDarkGreen,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = modelName,
            onValueChange = { modelName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(agent.defaultModel, color = Color(0xFF333333), fontFamily = FontFamily.Monospace)
            },
            colors = matrixTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save button
        Button(
            onClick = {
                onSave(
                    agent.copy(
                        apiKey = apiKey,
                        baseUrl = baseUrl,
                        modelName = modelName,
                        enabled = enabled,
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MatrixDarkGreen,
                contentColor = MatrixLightGreen,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "[ SAVE CONFIGURATION ]",
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun matrixTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = MatrixGreen,
    unfocusedTextColor = MatrixGreen,
    cursorColor = MatrixLightGreen,
    focusedContainerColor = Color(0xFF0A150A),
    unfocusedContainerColor = Color(0xFF0A150A),
    focusedIndicatorColor = MatrixGreen,
    unfocusedIndicatorColor = MatrixDarkGreen,
)
