package lab

import ai.koog.agents.core.agent.entity.AIAgentGraphStrategy
import ai.koog.agents.core.dsl.builder.node
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.message.MessagePart

fun revisionGraph(rounds: Int = 3): AIAgentGraphStrategy<RunState, RunState> {
    require(rounds in 1..10) { "Число раундов должно быть от 1 до 10" }

    return strategy<RunState, RunState>("revision-cycle") {
        val generate by node<RunState, RunState> { state ->
            val round = state.round + 1
            println("[generate] раунд $round: запрос к модели")
            val answer = llm.writeSession {
                appendPrompt {
                    user("""
                        /no_think
                        Раунд: $round
                        Задача: ${state.task}
                        Предыдущий ответ: ${state.answer}
                        Если предыдущего ответа нет, выполни задачу. Иначе исправь неточности
                        и сделай объяснение понятнее. Ответь по-русски, максимум тремя предложениями.
                        Верни только новую версию ответа без вступления.
                    """.trimIndent())
                }
                requestLLMWithoutTools().parts.filterIsInstance<MessagePart.Text>().joinToString("\n") { it.text }
            }
            state.copy(round = round, answer = answer)
        }

        val review by node<RunState, RunState> { state ->
            println("[review] ${state.answer}")
            println(if (state.round < rounds) "[review] → generate" else "[review] → finish")
            state
        }

        // Здесь описываются связи графа; сами переходы выполнятся позже, в agent.run().
        // A forwardTo B создаёт промежуточное описание перехода из A в B.
        // onCondition добавляет условие: его проверят по результату исходного узла.
        // edge(...) создаёт объект ребра и добавляет его в список исходящих рёбер A.
        // Без transformed результат A передаётся в B без изменения.

        // Начальное RunState из agent.run(...) передаём в первую генерацию.
        edge(nodeStart forwardTo generate)

        // Результат generate (новый RunState с ответом и номером раунда) передаём в review.
        edge(generate forwardTo review)

        // it — результат review, то есть RunState. rounds — заданное число раундов.
        // Пока раундов недостаточно, возвращаемся в generate: это обратное ребро цикла.
        edge(review forwardTo generate onCondition { it.round < rounds })

        // Когда нужное число раундов выполнено, передаём последнее состояние в finish.
        edge(review forwardTo nodeFinish onCondition { it.round >= rounds })

        // Koog проверяет исходящие рёбра по порядку и выбирает первое подходящее.
        // Наши условия взаимоисключающие: подходит либо возврат, либо завершение.
    }
}
