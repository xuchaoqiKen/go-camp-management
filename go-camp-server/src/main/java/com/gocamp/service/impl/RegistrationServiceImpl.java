package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gocamp.common.BusinessException;
import com.gocamp.dto.RegistrationRequest;
import com.gocamp.dto.RegistrationResult;
import com.gocamp.entity.*;
import com.gocamp.mapper.*;
import com.gocamp.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final StudentMapper studentMapper;
    private final StudentParentMapper studentParentMapper;
    private final RegistrationOrderMapper orderMapper;
    private final PaymentRecordMapper paymentMapper;
    private final DailyScheduleMapper scheduleMapper;
    private final CampSessionMapper sessionMapper;
    private final FeeConfigMapper feeConfigMapper;
    private final RoomOccupancyMapper roomOccupancyMapper;

    @Override
    @Transactional
    public RegistrationResult submitRegistration(RegistrationRequest request) {
        if (request.getStudents() == null || request.getStudents().isEmpty()) {
            throw new BusinessException("至少需要为一名学员报名");
        }

        String paymentNo = "PAY" + System.currentTimeMillis();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<RegistrationResult.OrderSummary> orderSummaries = new ArrayList<>();

        // Create payment record
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentNo(paymentNo);
        paymentRecord.setParentUserId(request.getParentUserId());
        paymentRecord.setTotalAmount(BigDecimal.ZERO);
        paymentRecord.setPaymentStatus(0);
        paymentRecord.setCreateTime(LocalDateTime.now());
        paymentMapper.insert(paymentRecord);

        for (RegistrationRequest.StudentRegistration studentReq : request.getStudents()) {
            // Validate age
            if (studentReq.getAge() != null && (studentReq.getAge() < 7 || studentReq.getAge() > 15)) {
                throw new BusinessException("学员" + studentReq.getName() + "年龄需在7-15岁之间");
            }

            // Create student
            Student student = new Student();
            student.setName(studentReq.getName());
            student.setGender(studentReq.getGender());
            student.setAge(studentReq.getAge());
            student.setRankCode(studentReq.getRank());
            student.setCity(studentReq.getCity());
            student.setAccompanyPerson(studentReq.getCompanion());
            student.setCampStatus("ENROLLED");
            student.setCreateTime(LocalDateTime.now());
            studentMapper.insert(student);

            // Link student to parent
            StudentParent sp = new StudentParent();
            sp.setStudentId(student.getId());
            sp.setParentUserId(request.getParentUserId());
            studentParentMapper.insert(sp);

            for (RegistrationRequest.SessionSelection sessionSel : studentReq.getSessions()) {
                CampSession session = sessionMapper.selectById(sessionSel.getSessionId());
                if (session == null || session.getStatus() != 1) {
                    throw new BusinessException("营期不存在或已停用");
                }

                // Calculate order amount from fee config
                BigDecimal orderAmount = calculateOrderAmount(sessionSel);
                totalAmount = totalAmount.add(orderAmount);

                // Create order
                String orderNo = "ORD" + System.currentTimeMillis() + student.getId();
                RegistrationOrder order = new RegistrationOrder();
                order.setOrderNo(orderNo);
                order.setPaymentNo(paymentNo);
                order.setStudentId(student.getId());
                order.setSessionId(sessionSel.getSessionId());
                order.setAccommodationType(getAccommodationType(sessionSel));
                order.setTotalAmount(orderAmount);
                order.setPaymentStatus("unpaid");
                order.setOrderStatus("pending");
                order.setRoomMatchStatus("PENDING");
                order.setCreateTime(LocalDateTime.now());
                orderMapper.insert(order);

                // Create daily schedules
                if (sessionSel.getSchedules() != null) {
                    for (RegistrationRequest.DailyScheduleItem item : sessionSel.getSchedules()) {
                        DailySchedule schedule = new DailySchedule();
                        schedule.setOrderId(order.getId());
                        schedule.setStudentId(student.getId());
                        schedule.setSessionId(sessionSel.getSessionId());
                        schedule.setScheduleDate(LocalDate.parse(item.getDate()));
                        schedule.setHasClass(item.getHasClass() != null && item.getHasClass() ? 1 : 0);
                        schedule.setHasBreakfast(item.getHasBreakfast() != null && item.getHasBreakfast() ? 1 : 0);
                        schedule.setHasLunch(item.getHasLunch() != null && item.getHasLunch() ? 1 : 0);
                        schedule.setHasDinner(item.getHasDinner() != null && item.getHasDinner() ? 1 : 0);
                        schedule.setAccommodationType(item.getAccommodationType());
                        schedule.setBreakfastSource(item.getBreakfastSource());
                        schedule.setBreakfastEditable(1);
                        schedule.setCreateTime(LocalDateTime.now());
                        scheduleMapper.insert(schedule);
                    }
                }

                // Create room occupancy placeholder for accommodation orders
                if (order.getAccommodationType() != null && !"NONE".equals(order.getAccommodationType())) {
                    RoomOccupancy occupancy = new RoomOccupancy();
                    occupancy.setOrderId(order.getId());
                    occupancy.setStudentId(student.getId());
                    occupancy.setSessionId(sessionSel.getSessionId());
                    occupancy.setStatus("PENDING_MATCH");
                    occupancy.setCreateTime(LocalDateTime.now());
                    roomOccupancyMapper.insert(occupancy);
                }

                RegistrationResult.OrderSummary summary = new RegistrationResult.OrderSummary();
                summary.setOrderNo(orderNo);
                summary.setStudentId(student.getId());
                summary.setStudentName(student.getName());
                summary.setSessionId(sessionSel.getSessionId());
                summary.setSessionName(session != null ? session.getSessionName() : "");
                summary.setOrderAmount(orderAmount);
                summary.setAccommodationType(order.getAccommodationType());
                orderSummaries.add(summary);
            }
        }

        // Update payment total
        paymentRecord.setTotalAmount(totalAmount);
        paymentMapper.updateById(paymentRecord);

        RegistrationResult result = new RegistrationResult();
        result.setPaymentNo(paymentNo);
        result.setTotalAmount(totalAmount);
        result.setOrders(orderSummaries);
        return result;
    }

    private BigDecimal calculateOrderAmount(RegistrationRequest.SessionSelection sessionSel) {
        BigDecimal total = BigDecimal.ZERO;
        if (sessionSel.getSchedules() == null) return total;

        // Get fee configs
        List<FeeConfig> feeConfigs = feeConfigMapper.selectList(new LambdaQueryWrapper<FeeConfig>()
                .eq(FeeConfig::getStatus, 1));

        for (RegistrationRequest.DailyScheduleItem item : sessionSel.getSchedules()) {
            // Breakfast (only charge for non-boarding add-on)
            if (item.getHasBreakfast() != null && item.getHasBreakfast()
                    && "WALK_IN_ADD".equals(item.getBreakfastSource())) {
                total = total.add(getFeePrice(feeConfigs, "BREAKFAST"));
            }
            // Lunch
            if (item.getHasLunch() != null && item.getHasLunch()) {
                total = total.add(getFeePrice(feeConfigs, "LUNCH"));
            }
            // Dinner
            if (item.getHasDinner() != null && item.getHasDinner()) {
                total = total.add(getFeePrice(feeConfigs, "DINNER"));
            }
            // Accommodation
            if (item.getAccommodationType() != null) {
                switch (item.getAccommodationType()) {
                    case "FULL_BOARD":
                        total = total.add(getFeePrice(feeConfigs, "FULL_BOARD"));
                        break;
                    case "ACCOMPANY_STANDARD":
                        total = total.add(getFeePrice(feeConfigs, "ACCOMPANY_STANDARD"));
                        break;
                    case "ACCOMPANY_KING":
                        total = total.add(getFeePrice(feeConfigs, "ACCOMPANY_KING"));
                        break;
                }
            }
        }
        return total;
    }

    private BigDecimal getFeePrice(List<FeeConfig> configs, String feeCode) {
        return configs.stream()
                .filter(c -> feeCode.equals(c.getFeeCode()))
                .findFirst()
                .map(FeeConfig::getUnitPrice)
                .orElse(BigDecimal.ZERO);
    }

    private String getAccommodationType(RegistrationRequest.SessionSelection sessionSel) {
        if (sessionSel.getSchedules() == null) return "NONE";
        return sessionSel.getSchedules().stream()
                .filter(s -> s.getAccommodationType() != null && !"NONE".equals(s.getAccommodationType()))
                .findFirst()
                .map(RegistrationRequest.DailyScheduleItem::getAccommodationType)
                .orElse("NONE");
    }

    @Override
    public RegistrationOrder getOrderDetail(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public List<RegistrationOrder> getOrdersByParent(Long parentUserId) {
        // Find students linked to parent
        List<StudentParent> links = studentParentMapper.selectList(
                new LambdaQueryWrapper<StudentParent>().eq(StudentParent::getParentUserId, parentUserId));
        if (links.isEmpty()) return List.of();

        List<Long> studentIds = links.stream().map(StudentParent::getStudentId).toList();
        return orderMapper.selectList(
                new LambdaQueryWrapper<RegistrationOrder>().in(RegistrationOrder::getStudentId, studentIds));
    }

    @Override
    public List<RegistrationOrder> getOrdersByStudent(Long studentId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<RegistrationOrder>().eq(RegistrationOrder::getStudentId, studentId));
    }
}