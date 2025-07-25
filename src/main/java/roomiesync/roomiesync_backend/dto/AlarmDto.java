package roomiesync.roomiesync_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDto {
  private UUID id;
  private UUID userId;
  private String name;
  private LocalTime time;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer consecutiveDays;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private LocalDateTime expirationDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private UserDto user;
}