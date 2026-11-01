package lab

import ai.koog.agents.core.agent.AIAgent

class DemoAgent(private val rounds: Int = 3, private val mock: Boolean = false) {
    suspend fun run(task: String): RunState {
        val graph = revisionGraph(rounds)
        val llm = if (mock) mockLlm(rounds) else ollamaLlm()
        try {
            val agent = AIAgent(
                promptExecutor = llm.executor,
                llmModel = llm.model,
                strategy = graph,
                temperature = 0.4,
                maxIterations = 30,
            )
            try {
                return agent.run(RunState(task))
            } finally {
                agent.close()
            }
        } finally {
            llm.executor.close()
        }
    }
}
