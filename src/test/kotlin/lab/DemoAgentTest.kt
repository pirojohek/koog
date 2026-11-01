package lab

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoAgentTest {
    @Test
    fun `mock runs the real graph and selects the response for each round`(): Unit = runBlocking {
        for (rounds in listOf(1, 3, 10)) {
            val result = DemoAgent(rounds = rounds, mock = true).run("Объясни граф Koog")
            assertEquals(rounds, result.round)
            assertEquals("Объясни граф Koog", result.task)
            val expected = if (rounds == 1) "Граф Koog состоит" else "Обратное ребро"
            assertTrue(result.answer.startsWith(expected), "Не тот mock-ответ для $rounds раундов: ${result.answer}")
        }
    }
}
