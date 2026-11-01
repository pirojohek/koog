package lab

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    val mock = args.firstOrNull() == "--mock"
    val arguments = if (mock) args.drop(1) else args.toList()
    val rounds = arguments.firstOrNull()?.toIntOrNull()
        ?: if (arguments.isEmpty()) 3 else error("Запуск: [--mock] [раунды 1..10] [задача]")
    val task = arguments.drop(1).joinToString(" ").ifBlank { "Объясни, как работает циклический граф Koog" }
    println("Режим: ${if (mock) "mock — ответы заданы заранее" else "Ollama — qwen3:8b"}")
    DemoAgent(rounds, mock).run(task)
}
