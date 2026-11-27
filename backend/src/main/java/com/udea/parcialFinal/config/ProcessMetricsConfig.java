package com.udea.parcialFinal.config;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Métricas del proceso que Micrometer no trae por defecto (librería micrometer-jvm-extras).
 *
 * <ul>
 *   <li>{@code process_memory_rss_bytes}, {@code process_memory_vss_bytes} y {@code process_memory_swap_bytes}:
 *       panel "JVM Process Memory" del dashboard de Grafana.</li>
 *   <li>{@code process_threads}: hilos del sistema operativo del proceso.</li>
 * </ul>
 *
 * Se leen de {@code /proc/self/status}, así que solo tienen valor en Linux (por ejemplo, en un contenedor).
 * En macOS y Windows las métricas existen pero no reportan datos.
 */
@Configuration
public class ProcessMetricsConfig {

    @Bean
    MeterBinder processMemoryMetrics() {
        return new ProcessMemoryMetrics();
    }

    @Bean
    MeterBinder processThreadMetrics() {
        return new ProcessThreadMetrics();
    }
}
