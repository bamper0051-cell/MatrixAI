package io.shubham0204.smollmandroid.data

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.annotation.Single

/**
 * Represents a cloud AI agent provider.
 */
data class AIAgent(
    val id: String,
    val name: String,
    val defaultBaseUrl: String,
    val defaultModel: String,
    var apiKey: String = "",
    var baseUrl: String = "",
    var modelName: String = "",
    var enabled: Boolean = false,
)

/**
 * Manages AI agent configurations (API keys, base URLs, model names)
 * using SharedPreferences for secure local storage.
 */
@Single
class AIAgentsDB(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("matrix_ai_agents", Context.MODE_PRIVATE)

    companion object {
        val AVAILABLE_AGENTS = listOf(
            AIAgent(
                id = "openai",
                name = "OpenAI (GPT)",
                defaultBaseUrl = "https://api.openai.com/v1",
                defaultModel = "gpt-4o-mini",
            ),
            AIAgent(
                id = "grok",
                name = "Grok (xAI)",
                defaultBaseUrl = "https://api.x.ai/v1",
                defaultModel = "grok-beta",
            ),
            AIAgent(
                id = "claude",
                name = "Claude (Anthropic)",
                defaultBaseUrl = "https://api.anthropic.com/v1",
                defaultModel = "claude-sonnet-4-20250514",
            ),
            AIAgent(
                id = "deepseek",
                name = "DeepSeek",
                defaultBaseUrl = "https://api.deepseek.com/v1",
                defaultModel = "deepseek-chat",
            ),
            AIAgent(
                id = "gemini",
                name = "Gemini (Google)",
                defaultBaseUrl = "https://generativelanguage.googleapis.com/v1beta",
                defaultModel = "gemini-2.0-flash",
            ),
        )
    }

    fun getAgents(): List<AIAgent> {
        return AVAILABLE_AGENTS.map { agent ->
            agent.copy(
                apiKey = prefs.getString("${agent.id}_api_key", "") ?: "",
                baseUrl = prefs.getString("${agent.id}_base_url", agent.defaultBaseUrl) ?: agent.defaultBaseUrl,
                modelName = prefs.getString("${agent.id}_model", agent.defaultModel) ?: agent.defaultModel,
                enabled = prefs.getBoolean("${agent.id}_enabled", false),
            )
        }
    }

    fun getAgent(agentId: String): AIAgent? {
        return getAgents().find { it.id == agentId }
    }

    fun saveAgent(agent: AIAgent) {
        prefs.edit().apply {
            putString("${agent.id}_api_key", agent.apiKey)
            putString("${agent.id}_base_url", agent.baseUrl)
            putString("${agent.id}_model", agent.modelName)
            putBoolean("${agent.id}_enabled", agent.enabled)
            apply()
        }
    }

    fun getEnabledAgents(): List<AIAgent> {
        return getAgents().filter { it.enabled && it.apiKey.isNotBlank() }
    }

    fun getSelectedAgentId(): String? {
        return prefs.getString("selected_agent_id", null)
    }

    fun setSelectedAgentId(agentId: String) {
        prefs.edit().putString("selected_agent_id", agentId).apply()
    }
}
