package ar.edu.itba.pod.client.data;

import java.time.LocalDateTime;

public record Bike(
        LocalDateTime startDate,
        LocalDateTime endDate,
        int startStationPK,
        int endStationPK,
        boolean isMember
) {
}
