import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    withJansi = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%.8thread] %highlight(%-5level) %cyan(%logger{36}) - %boldWhite(%msg) %n"

    }
}

root(DEBUG, ["CONSOLE"])
