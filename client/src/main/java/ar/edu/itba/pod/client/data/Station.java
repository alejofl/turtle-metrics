package ar.edu.itba.pod.client.data;

import java.math.BigDecimal;

public record Station(
        int pk,
        String name,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
