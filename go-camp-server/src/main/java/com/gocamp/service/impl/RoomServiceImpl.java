package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.*;
import com.gocamp.mapper.*;
import com.gocamp.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final RoomOccupancyMapper occupancyMapper;
    private final RegistrationOrderMapper orderMapper;
    private final StudentMapper studentMapper;

    public RoomServiceImpl(RoomOccupancyMapper occupancyMapper, RegistrationOrderMapper orderMapper, StudentMapper studentMapper) {
        this.occupancyMapper = occupancyMapper;
        this.orderMapper = orderMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public Page<Room> list(Long sessionId, String roomType, String status, int page, int size) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (sessionId != null) wrapper.eq(Room::getSessionId, sessionId);
        if (roomType != null && !roomType.isEmpty()) wrapper.eq(Room::getRoomType, roomType);
        if (status != null && !status.isEmpty()) wrapper.eq(Room::getMatchStatus, status);
        wrapper.orderByAsc(Room::getBuilding, Room::getRoomNo);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Room getDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public Room create(Room room, Long operatorId) {
        room.setOccupiedBeds(0);
        room.setMatchStatus("AVAILABLE");
        room.setStatus(1);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        save(room);
        return room;
    }

    @Override
    @Transactional
    public Room update(Room room, Long operatorId) {
        Room existing = getById(room.getId());
        if (existing == null) throw new BusinessException("房间不存在");
        room.setUpdateTime(LocalDateTime.now());
        updateById(room);
        return room;
    }

    @Override
    @Transactional
    public void delete(Long id, Long operatorId) {
        Room existing = getById(id);
        if (existing == null) throw new BusinessException("房间不存在");
        removeById(id);
    }

    @Override
    public Map<String, Object> getStats(Long sessionId) {
        List<Room> rooms = list(new LambdaQueryWrapper<Room>().eq(Room::getSessionId, sessionId));
        int totalRooms = rooms.size();
        int totalBeds = rooms.stream().mapToInt(Room::getBedCount).sum();
        int occupiedBeds = rooms.stream().mapToInt(r -> r.getOccupiedBeds() != null ? r.getOccupiedBeds() : 0).sum();
        int remainingBeds = totalBeds - occupiedBeds;
        long pendingConfirm = rooms.stream().filter(r -> "PENDING_CONFIRM".equals(r.getMatchStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", totalRooms);
        stats.put("totalBeds", totalBeds);
        stats.put("occupiedBeds", occupiedBeds);
        stats.put("remainingBeds", remainingBeds);
        stats.put("pendingConfirm", pendingConfirm);
        return stats;
    }

    @Override
    @Transactional
    public void matchRoom(Long orderId) {
        RegistrationOrder order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getPaymentStatus() == null || !"paid".equals(order.getPaymentStatus())) throw new BusinessException("订单未支付，无法匹配房间");

        Student student = studentMapper.selectById(order.getStudentId());
        if (student == null) throw new BusinessException("学员不存在");

        // Simple matching: find first available room matching session
        List<Room> availableRooms = list(new LambdaQueryWrapper<Room>()
                .eq(Room::getSessionId, order.getSessionId())
                .gt(Room::getBedCount, 0)
                .eq(Room::getMatchStatus, "AVAILABLE"));

        if (availableRooms.isEmpty()) {
            order.setRoomMatchStatus("MATCH_FAILED");
            orderMapper.updateById(order);
            throw new BusinessException("无可用房间，请联系管理员处理");
        }

        Room room = availableRooms.get(0);
        RoomOccupancy occupancy = new RoomOccupancy();
        occupancy.setRoomId(room.getId());
        occupancy.setStudentId(student.getId());
        occupancy.setOrderId(orderId);
        occupancy.setSessionId(order.getSessionId());
        occupancy.setCheckinDate(LocalDate.now());
        occupancy.setCheckoutDate(LocalDate.now().plusDays(7));
        occupancy.setStatus("PENDING_CONFIRM");
        occupancy.setCreateTime(LocalDateTime.now());
        occupancyMapper.insert(occupancy);

        room.setOccupiedBeds((room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0) + 1);
        int remaining = room.getBedCount() - room.getOccupiedBeds();
        if (remaining <= 0) room.setMatchStatus("FULL");
        updateById(room);

        order.setRoomMatchStatus("MATCHED");
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void manualAssign(Long roomId, Long studentId, Long operatorId) {
        Room room = getById(roomId);
        if (room == null) throw new BusinessException("房间不存在");
        int remaining = room.getBedCount() - (room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0);
        if (remaining <= 0) throw new BusinessException("房间已满");

        RoomOccupancy occupancy = new RoomOccupancy();
        occupancy.setRoomId(roomId);
        occupancy.setStudentId(studentId);
        occupancy.setCheckinDate(LocalDate.now());
        occupancy.setCheckoutDate(LocalDate.now().plusDays(7));
        occupancy.setStatus("CONFIRMED");
        occupancy.setCreateTime(LocalDateTime.now());
        occupancyMapper.insert(occupancy);

        room.setOccupiedBeds((room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0) + 1);
        if (room.getBedCount() - room.getOccupiedBeds() <= 0) room.setMatchStatus("FULL");
        updateById(room);
    }

    @Override
    @Transactional
    public void releaseRoom(Long occupancyId, String reason, Long operatorId) {
        RoomOccupancy occupancy = occupancyMapper.selectById(occupancyId);
        if (occupancy == null) throw new BusinessException("入住记录不存在");

        Room room = getById(occupancy.getRoomId());
        if (room != null) {
            room.setOccupiedBeds(Math.max(0, (room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0) - 1));
            if ("FULL".equals(room.getMatchStatus()) && room.getBedCount() - room.getOccupiedBeds() > 0) {
                room.setMatchStatus("AVAILABLE");
            }
            updateById(room);
        }

        occupancy.setStatus("RELEASED");
        occupancy.setUpdateTime(LocalDateTime.now());
        occupancyMapper.updateById(occupancy);
    }

    @Override
    public List<RoomOccupancy> getOccupancyList(Long roomId) {
        return occupancyMapper.selectList(new LambdaQueryWrapper<RoomOccupancy>()
                .eq(RoomOccupancy::getRoomId, roomId));
    }

    @Override
    @Transactional
    public void confirmRoom(Long roomId, Long operatorId) {
        Room room = getById(roomId);
        if (room == null) throw new BusinessException("房间不存在");
        room.setMatchStatus("CONFIRMED");
        room.setUpdateTime(LocalDateTime.now());
        updateById(room);
    }
}