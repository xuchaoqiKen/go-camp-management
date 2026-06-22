package com.gocamp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.entity.Room;
import com.gocamp.entity.RoomOccupancy;

import java.util.List;
import java.util.Map;

public interface RoomService {

    Page<Room> list(Long sessionId, String roomType, String status, int page, int size);

    Room getDetail(Long id);

    Room create(Room room, Long operatorId);

    Room update(Room room, Long operatorId);

    void delete(Long id, Long operatorId);

    Map<String, Object> getStats(Long sessionId);

    void matchRoom(Long orderId);

    void manualAssign(Long roomId, Long studentId, Long operatorId);

    void releaseRoom(Long occupancyId, String reason, Long operatorId);

    List<RoomOccupancy> getOccupancyList(Long roomId);

    void confirmRoom(Long roomId, Long operatorId);
}