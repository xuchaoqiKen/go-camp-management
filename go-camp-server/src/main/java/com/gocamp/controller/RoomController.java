package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.Room;
import com.gocamp.service.RoomService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/stats")
    public Result<?> stats(@RequestParam Long sessionId) {
        return Result.success(roomService.getStats(sessionId));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long sessionId,
                           @RequestParam(required = false) String roomType,
                           @RequestParam(required = false) String status,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {
        return Result.success(roomService.list(sessionId, roomType, status, page, size));
    }

    @GetMapping("/{id}")
    public Result<Room> getDetail(@PathVariable Long id) {
        return Result.success(roomService.getDetail(id));
    }

    @PostMapping
    public Result<Room> create(@RequestBody Room room, @RequestParam Long operatorId) {
        return Result.success(roomService.create(room, operatorId));
    }

    @PutMapping
    public Result<Room> update(@RequestBody Room room, @RequestParam Long operatorId) {
        return Result.success(roomService.update(room, operatorId));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, @RequestParam Long operatorId) {
        roomService.delete(id, operatorId);
        return Result.success();
    }

    @PostMapping("/match")
    public Result<?> matchRoom(@RequestParam Long orderId) {
        roomService.matchRoom(orderId);
        return Result.success();
    }

    @PostMapping("/manual-assign")
    public Result<?> manualAssign(@RequestParam Long roomId,
                                   @RequestParam Long studentId,
                                   @RequestParam Long operatorId) {
        roomService.manualAssign(roomId, studentId, operatorId);
        return Result.success();
    }

    @PutMapping("/{occupancyId}/release")
    public Result<?> releaseRoom(@PathVariable Long occupancyId,
                                  @RequestParam String reason,
                                  @RequestParam Long operatorId) {
        roomService.releaseRoom(occupancyId, reason, operatorId);
        return Result.success();
    }

    @GetMapping("/{roomId}/occupancy")
    public Result<?> getOccupancyList(@PathVariable Long roomId) {
        return Result.success(roomService.getOccupancyList(roomId));
    }

    @PutMapping("/{roomId}/confirm")
    public Result<?> confirmRoom(@PathVariable Long roomId, @RequestParam Long operatorId) {
        roomService.confirmRoom(roomId, operatorId);
        return Result.success();
    }
}
