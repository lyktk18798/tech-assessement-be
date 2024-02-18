package com.lyktk.urlshortener.entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="urls")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UrlMapping {
    @Id
    private UUID id;
    private String originalUrl;
    private String shortUrl;
    private Date creationDate;
}
