package com.pruebatec.producto_service.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta en formato JSON:API")
public class JsonApiResponse<T> {

    @Schema(description = "Recursos principales de la respuesta")
    private List<Resource<T>> data;
    
    @Schema(description = "Información de paginación", nullable = true)
    private Meta meta;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Recurso individual en formato JSON:API")
    public static class Resource<T> {
        
        @Schema(description = "Tipo de recurso", example = "productos")
        private String type;
        
        @Schema(description = "Identificador único del recurso", example = "1")
        private String id;
        
        @Schema(description = "Atributos del recurso")
        private T attributes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Metadatos de paginación")
    public static class Meta {
        
        @JsonProperty("total_Elementos_en_la_tabla")
        @Schema(description = "Número total de elementos", example = "100")
        private long totalElements;
        
        @JsonProperty("total_Paginas")
        @Schema(description = "Número total de páginas", example = "10")
        private int totalPages;
        
        @JsonProperty("Pagina_Actual")
        @Schema(description = "Número de página actual (empezando desde 0)", example = "0")
        private int number;
        
        @JsonProperty("Elementos_por_Pagina")
        @Schema(description = "Tamaño de página", example = "10")
        private int size;
    }

    // Método para crear respuesta a partir de una entidad
    public static <E, T> JsonApiResponse<T> desdeEntidad(
            E entidad,
            Function<E, T> mapeoAtributos,
            Function<E, String> mapeoId,
            String tipoRecurso) {
        
        Resource<T> recurso = new Resource<>(
                tipoRecurso,
                mapeoId.apply(entidad),
                mapeoAtributos.apply(entidad)
        );
        
        return new JsonApiResponse<>(List.of(recurso), null);
    }

    // Método para crear respuesta a partir de una página
    public static <E, T> JsonApiResponse<T> desdePagina(
            Page<E> pagina,
            Function<E, T> mapeoAtributos,
            Function<E, String> mapeoId,
            String tipoRecurso) {
        
        List<Resource<T>> recursos = pagina.getContent().stream()
                .map(entidad -> new Resource<>(
                        tipoRecurso,
                        mapeoId.apply(entidad),
                        mapeoAtributos.apply(entidad)
                ))
                .collect(Collectors.toList());
        
        Meta meta = new Meta(
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.getNumber(),
                pagina.getSize()
        );
        
        return new JsonApiResponse<>(recursos, meta);
    }
}