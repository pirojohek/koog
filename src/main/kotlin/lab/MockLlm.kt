package lab

import ai.koog.agents.testing.tools.getMockExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

fun mockLlm(rounds: Int): LlmSetup {
    val answers = listOf(
        "Граф Koog состоит из узлов и рёбер. Узлы выполняют действия, рёбра связывают их.",
        "Узел получает состояние и возвращает результат. Koog проверяет исходящие рёбра и выбирает первое подходящее.",
        "Обратное ребро возвращает выполнение в generate, пока round < rounds. При достижении лимита другое ребро ведёт в finish. Цикл выполняет Koog, а не модель.",
    )
    val executor = getMockExecutor {
        for (round in 1..rounds) {
            // Перевод строки отличает раунд 1 от 10; после третьего ответа повторяем последний.
            mockLLMAnswer(answers[(round - 1).coerceAtMost(answers.lastIndex)]) onRequestContains "Раунд: $round\n"
        }
    }
    val model = LLModel(
        provider = LLMProvider.Ollama,
        id = "mock",
        capabilities = listOf(LLMCapability.Completion, LLMCapability.Temperature),
        contextLength = 40960,
    )
    return LlmSetup(executor, model)
}
