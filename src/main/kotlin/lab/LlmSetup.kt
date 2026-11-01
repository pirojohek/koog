package lab

import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.executor.ollama.client.toLLModel
import ai.koog.prompt.llm.LLModel

data class LlmSetup(val executor: PromptExecutor, val model: LLModel)

suspend fun ollamaLlm(): LlmSetup {
    val client = OllamaClient(baseUrl = "http://localhost:11434")
    try {
        val model = client.getModelOrNull("qwen3:8b")?.toLLModel()
            ?: error("Модель qwen3:8b не найдена. Установите её командой: ollama pull qwen3:8b")
        return LlmSetup(MultiLLMPromptExecutor(client), model)
    } catch (error: Throwable) {
        client.close()
        throw error
    }
}
