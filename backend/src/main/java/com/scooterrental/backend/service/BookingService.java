package com.scooterrental.backend.service;

import com.scooterrental.backend.entity.Booking;
import com.scooterrental.backend.mapper.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    public List<Booking> getHistory(Integer userId) {
        return bookingMapper.selectByUserId(userId);
    }

    public boolean cancel(Integer bookingId, Integer userId) {
        return bookingMapper.cancelBooking(bookingId, userId) > 0;
    }

    public List<Map<String, Object>> getStats(Integer userId) {
        return bookingMapper.getWeeklyStats(userId);
    }
}
