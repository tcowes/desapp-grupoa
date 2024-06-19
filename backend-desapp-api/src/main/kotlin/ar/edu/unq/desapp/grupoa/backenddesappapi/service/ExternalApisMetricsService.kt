package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Component

@Component
class ExternalApisMetricsService(meterRegistry: MeterRegistry) {

    private val requestTimerAllCryptosQuotes: Timer = meterRegistry.timer("all_cryptos_quotes_request_duration")

    fun <T> callExternalService(block: () -> T): T {
        return requestTimerAllCryptosQuotes.record(block)!!
    }
}