
package com.crio.starter.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "memes")
public class MemeEntity {


@Id
  private String id;
  private String name;
  private String url;
  private String caption;
  private LocalDateTime timestamp = LocalDateTime.now();

  public MemeEntity(String id, String name, String url, String caption) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.caption = caption;
}
}
