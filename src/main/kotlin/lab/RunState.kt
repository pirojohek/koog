package lab

data class RunState(
    val task: String,
    val round: Int = 0,
    val answer: String = "",
)
