# Matrix AI - On-Device AI with Matrix-Style Interface

Matrix AI is an Android application with a stunning Matrix-inspired interface featuring green digital rain animations on a black background. It allows you to run AI language models locally on your device using llama.cpp, and also integrates with cloud AI agents.

## Features

- **Matrix-Style UI** - Iconic falling green code rain animation background
- **On-Device AI** - Run GGUF language models locally, privately
- **Cloud AI Agents** - Integrate with OpenAI, Grok, Claude, DeepSeek, and Gemini
- **HuggingFace Integration** - Browse and download models directly
- **Animated States** - Distinct Matrix-themed animations for downloading, loading, and installing
- **Task Templates** - Quick AI actions with predefined system prompts
- **Chat Management** - Organize sessions in folders
- **Custom Parameters** - Tune temperature, min-p, context size, and threads

## Cloud AI Agents

Configure API keys to use cloud AI providers alongside local models:

| Agent | Provider | Default Model |
|-------|----------|---------------|
| OpenAI | OpenAI | gpt-4o-mini |
| Grok | xAI | grok-beta |
| Claude | Anthropic | claude-sonnet-4-20250514 |
| DeepSeek | DeepSeek | deepseek-chat |
| Gemini | Google | gemini-2.0-flash |

## Setup

1. Clone the repository with its submodule:

```bash
git clone --depth=1 https://github.com/bamper0051-cell/MatrixAI
cd MatrixAI
git submodule update --init --recursive
```

2. Open in Android Studio and build the project.

3. Connect an Android device and run the app.

## Architecture

```
UI Layer (Jetpack Compose - Matrix Theme)
    |
ViewModel + Repository Pattern
    |
Kotlin API (SmolLM.kt) + AI Agents API
    |
JNI Bindings (smollm.cpp)
    |
C++ Core (LLMInference.cpp)
    |
llama.cpp (GGUF model inference)
```

## Technologies

- **llama.cpp** - C/C++ framework for local LLM inference
- **Jetpack Compose** - Modern Android UI with custom Matrix theme
- **Room** - Local database for chats and settings
- **Koin** - Dependency injection
- **Markwon + Prism4j** - Markdown rendering with syntax highlighting

## License

Apache License 2.0
