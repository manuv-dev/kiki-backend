package com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private long totalRequests;
    private long acceptedRequests;
    private long pendingRequests;
    private long rejectedRequests;
    private long urgentRequests;
    private double conversionRate;
    private double totalRevenue;
    private long totalClients;
    private long particuliersCount;
    private long entreprisesCount;
    private List<GestionnaireDemandeDto> recentRequests;
}
