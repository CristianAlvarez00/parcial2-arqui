package com.udea.parcialFinal.dto;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "Respuesta paginada con metadatos de navegación y enlaces HATEOAS")
@JsonPropertyOrder({"content", "page", "size", "totalElements", "totalPages", "first", "last", "_links"})
public class PageResponse<T> extends RepresentationModel<PageResponse<T>> implements Serializable {

    @Schema(description = "Elementos contenidos en la página actual")
    private final List<T> content;

    @Schema(description = "Número de página actual (basado en 0)", example = "0")
    private final int page;

    @Schema(description = "Cantidad máxima de elementos por página", example = "10")
    private final int size;

    @Schema(description = "Cantidad total de registros disponibles", example = "25")
    private final long totalElements;

    @Schema(description = "Cantidad total de páginas disponibles", example = "3")
    private final int totalPages;

    @Schema(description = "Indica si esta es la primera página", example = "true")
    private final boolean first;

    @Schema(description = "Indica si esta es la última página", example = "false")
    private final boolean last;

    public PageResponse(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
        this.content = List.copyOf(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static <T> PageResponse<T> de(Page<T> pagina) {
        return new PageResponse<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );
    }

    public <R> PageResponse<R> map(Function<? super T, ? extends R> transformador) {
        List<R> transformados = content.stream()
                .map(transformador)
                .<R>map(valor -> valor)
                .toList();
        return new PageResponse<>(transformados, page, size, totalElements, totalPages, first, last);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }
}
