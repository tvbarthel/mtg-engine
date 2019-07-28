package fr.tvbarthel.mtg.engine

/**
 * Used to perform log.
 */
class Logger {

    companion object {

        /**
         * Debug level.
         */
        fun d(message: String) = println("D: $message")

        /**
         * Info level
         */
        fun i(message: String) = println("I: $message")

        /**
         * Error level
         */
        fun e(message: String) = println("E: $message")
    }
}