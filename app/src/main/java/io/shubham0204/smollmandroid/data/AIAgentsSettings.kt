package io.shubham0204.smollmandroid.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Stores API keys and settings for cloud AI agents.
 * Uses SharedPreferences with prefix per agent.
 */
class AIAgentsSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("matrix_ai_agents", Context.MODE_PRIVATE)

    enum class Agent(
        val displayName: String,
        val defaultModel: String,
        val apiUrl: String,
        val models: List<String>,
    ) {
        OPENAI(
            displayName = "OpenAI GPT",
            defaultModel = "gpt-4o-mini",
            apiUrl = "https://api.openai.com/v1/chat/completions",
            models = listOf("gpt-4o", "gpt-4o-mini", "gpt-4-turbo", "gpt-3.5-turbo"),
        ),
        GROK(
            displayName = "Grok (xAI)",
            defaultModel = "grok-2-latest",
            apiUrl = "https://api.x.ai/v1/chat/completions",
            models = listOf("grok-2-latest", "grok-beta"),
        ),
        CLAUDE(
            displayName = "Claude (Anthropic)",
            defaultModel = "claude-3-5-haiku-20241022",
            apiUrl = "https://api.anthropic.com/v1/messages",
            models = listOf(
                "claude-opus-4-5",
                "claude-sonnet-4-5",
                "claude-3-5-haiku-20241022",
                "claude-3-5-sonnet-20241022",
            ),
        ),
        DEEPSEEK(
            displayName = "DeepSeek",
            defaultModel = "deepseek-chat",
            apiUrl = "https://api.deepseek.com/v1/chat/completions",
            models = listOf("deepseek-chat", "deepseek-reasoner"),
        ),
        GEMINI(
            displayName = "Gemini (Google)",
            defaultModel = "gemini-2.0-flash",
            apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent",
            models = listOf(
                "gemini-2.0-flash",
                "gemini-2.0-flash-lite",
                "gemini-1.5-pro",
                "gemini-1.5-flash",
            ),
        ),
    }

    fun getApiKey(agent: Agent): String =
        prefs.getString("${agent.name}_api_key", "") ?: ""

    fun setApiKey(agent: Agent, key: String) =
        prefs.edit().putString("${agent.name}_api_key", key).apply()

    fun isEnabled(agent: Agent): Boolean =
        prefs.getBoolean("${agent.name}_enabled", false)

    fun setEnabled(agent: Agent, enabled: Boolean) =
        prefs.edit().putBoolean("${agent.name}_enabled", enabled).apply()

    fun getSelectedModel(agent: Agent): String =
        prefs.getString("${agent.name}_model", agent.defaultModel) ?: agent.defaultModel

    fun setSelectedModel(agent: Agent, model: String) =
        prefs.edit().putString("${agent.name}_model", model).apply()

    fun getEnabledAgents(): List<Agent> = Agent.entries.filter { isEnabled(it) }
}
