package com.scooterrental.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scooterrental.backend.entity.Scooter;
import com.scooterrental.backend.mapper.ScooterMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing Scooter logic.
 * Extends ServiceImpl to use MyBatis-Plus built-in methods.
 */
@Service
public class ScooterService extends ServiceImpl<ScooterMapper, Scooter> {

    /**
     * Retrieve all available scooters.
     * Optionally sorts them by distance if user coordinates are provided.
     *
     * @param userLat User's latitude (can be null)
     * @param userLng User's longitude (can be null)
     * @return List of available scooters
     */
    public List<Scooter> getAvailableScooters(Double userLat, Double userLng) {
        // 1. Create a query wrapper to filter by status
        QueryWrapper<Scooter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "AVAILABLE");

        // Optional: Filter out low battery (e.g., below 20%)
        // queryWrapper.ge("battery_level", 20);

        List<Scooter> scooters = baseMapper.selectList(queryWrapper);

        // 2. Sort by distance if user location is provided (Smart Recommendation)
        if (userLat != null && userLng != null) {
            scooters.sort(Comparator.comparingDouble(
                    s -> calculateDistance(userLat, userLng, s.getLatitude(), s.getLongitude())));
        }

        return scooters;
    }

    /**
     * Calculate Euclidean distance between two points.
     * Note: For production, use Haversine formula for better accuracy.
     *
     * @param lat1 User latitude
     * @param lon1 User longitude
     * @param lat2 Scooter latitude
     * @param lon2 Scooter longitude
     * @return Estimated distance value
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDiff = lat1 - lat2;
        double lonDiff = lon1 - lon2;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }
}
