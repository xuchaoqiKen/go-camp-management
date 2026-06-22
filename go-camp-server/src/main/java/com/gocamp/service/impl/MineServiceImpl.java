package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gocamp.entity.*;
import com.gocamp.mapper.*;
import com.gocamp.service.MineService;
import com.gocamp.vo.MineProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MineServiceImpl implements MineService {

    private final SysUserMapper sysUserMapper;
    private final StudentMapper studentMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final RegistrationOrderMapper registrationOrderMapper;
    private final RoomOccupancyMapper roomOccupancyMapper;
    private final RoomMapper roomMapper;
    private final CampClassMapper campClassMapper;
    private final ClassStudentMapper classStudentMapper;

    @Override
    public MineProfileVO getProfile(Long userId) {
        MineProfileVO vo = new MineProfileVO();

        // 1. 用户基本信息
        SysUser user = sysUserMapper.selectById(userId);
        MineProfileVO.ProfileInfo profile = new MineProfileVO.ProfileInfo();
        if (user != null) {
            profile.setName(user.getRealName());
            profile.setPhone(maskPhone(user.getPhone()));
            profile.setCity("");
        }
        vo.setProfile(profile);

        // 2. 关联学员 - 直接从student表通过parent_user_id查询
        List<Student> students = studentMapper.selectList(
                new LambdaQueryWrapper<Student>().eq(Student::getParentUserId, userId));
        List<Long> studentIds = students.stream().map(Student::getId).collect(java.util.stream.Collectors.toList());

        List<MineProfileVO.StudentInfo> studentInfos = new ArrayList<>();
        for (Student student : students) {
            MineProfileVO.StudentInfo info = new MineProfileVO.StudentInfo();
            info.setId(student.getId());
            info.setName(student.getName());
            info.setRank(student.getRankCode());
            info.setStatusText("在营");
            info.setStatusClass("tag-success");

            // 查询报名订单获取期数、住宿等信息
            List<RegistrationOrder> orders = registrationOrderMapper.selectList(
                    new LambdaQueryWrapper<RegistrationOrder>()
                            .eq(RegistrationOrder::getStudentId, student.getId())
                            .orderByDesc(RegistrationOrder::getCreateTime));
            if (!orders.isEmpty()) {
                RegistrationOrder order = orders.get(0);
                info.setSessions(order.getSessionId() != null ? "第" + order.getSessionId() + "期" : "-");
                info.setAccommodation(order.getAccommodationType() != null ? order.getAccommodationType() : "不住宿");
                info.setBreakfastSource("-");

                // 查询班级
                List<ClassStudent> classStudents = classStudentMapper.selectList(
                        new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getStudentId, student.getId()));
                if (!classStudents.isEmpty()) {
                    CampClass campClass = campClassMapper.selectById(classStudents.get(0).getClassId());
                    info.setClassName(campClass != null ? campClass.getClassName() : "待分配");
                } else {
                    info.setClassName("待分配");
                }

                // 查询房间
                List<RoomOccupancy> occupancies = roomOccupancyMapper.selectList(
                        new LambdaQueryWrapper<RoomOccupancy>()
                                .eq(RoomOccupancy::getStudentId, student.getId())
                                .eq(RoomOccupancy::getStatus, "occupied"));
                if (!occupancies.isEmpty()) {
                    RoomOccupancy occ = occupancies.get(0);
                    Room room = roomMapper.selectById(occ.getRoomId());
                    if (room != null) {
                        info.setRoomInfo(room.getRoomName() + "-" + occ.getBedNo() + "床");
                    }
                } else {
                    info.setRoomInfo("待系统匹配/待老师调整");
                }
            } else {
                info.setSessions("-");
                info.setClassName("待分配");
                info.setAccommodation("不住宿");
                info.setRoomInfo("待系统匹配/待老师调整");
                info.setBreakfastSource("-");
            }
            studentInfos.add(info);
        }
        vo.setStudents(studentInfos);

        // 3. 请假记录
        List<MineProfileVO.LeaveRecordInfo> leaveInfos = new ArrayList<>();
        if (!studentIds.isEmpty()) {
            List<LeaveRequest> leaves = leaveRequestMapper.selectList(
                    new LambdaQueryWrapper<LeaveRequest>()
                            .in(LeaveRequest::getStudentId, studentIds)
                            .orderByDesc(LeaveRequest::getCreateTime)
                            .last("LIMIT 5"));
            for (LeaveRequest leave : leaves) {
                MineProfileVO.LeaveRecordInfo info = new MineProfileVO.LeaveRecordInfo();
                info.setId(leave.getId());
                Student student = studentMapper.selectById(leave.getStudentId());
                info.setStudentName(student != null ? student.getName() : "");
                info.setDate(leave.getLeaveDate() != null ? leave.getLeaveDate().toString() : "");
                info.setTypeText("no_deduction".equals(leave.getLeaveType()) ? "请假不扣课" : "仅退餐饮");
                info.setStatusText(getStatusText(leave.getStatus()));
                info.setStatusClass(getStatusClass(leave.getStatus()));
                leaveInfos.add(info);
            }
        }
        vo.setLeaveRecords(leaveInfos);

        return vo;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String getStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "pending": return "待审核";
            case "approved": return "已通过";
            case "rejected": return "已驳回";
            case "refunded": return "已退款";
            default: return status;
        }
    }

    private String getStatusClass(String status) {
        if (status == null) return "tag-default";
        switch (status) {
            case "pending": return "tag-warning";
            case "approved": return "tag-success";
            case "rejected": return "tag-danger";
            case "refunded": return "tag-success";
            default: return "tag-default";
        }
    }
}