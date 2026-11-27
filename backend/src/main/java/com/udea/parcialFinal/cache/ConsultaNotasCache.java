package com.udea.parcialFinal.cache;

import java.util.List;
import java.util.function.Supplier;

import com.udea.parcialFinal.dto.NotaResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConsultaNotasCache {

	public static final String CACHE = "notas-por-cedula";

	@Cacheable(cacheNames = CACHE,
			key = "#cedula + ':' + (#pagina.paged ? #pagina.pageNumber + ':' + #pagina.pageSize : 'sin-paginar') + ':' + #pagina.sort")
	public Pagina consultar(String cedula, Pageable pagina, Supplier<Pagina> consulta) {
		return consulta.get();
	}

	public record Pagina(List<NotaResponse> notas, long total) {
		public Pagina {
			notas = List.copyOf(notas);
		}
	}
}
