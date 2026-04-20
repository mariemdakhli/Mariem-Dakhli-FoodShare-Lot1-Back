package com.microservice.foodsharepi.Service;

import com.microservice.foodsharepi.Entity.Badge;

import java.util.List;

public interface IBadgeService {
    Badge createBadge(Badge badge);
    List<Badge> getAllBadges();
}
