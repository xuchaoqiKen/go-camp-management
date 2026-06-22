package com.gocamp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.entity.RegistrationOrder;
import com.gocamp.vo.OrderListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegistrationOrderMapper extends BaseMapper<RegistrationOrder> {

    Page<OrderListVO> selectOrderPage(Page<OrderListVO> page,
                                      @Param("studentName") String studentName,
                                      @Param("orderNo") String orderNo,
                                      @Param("sessionId") Long sessionId,
                                      @Param("paymentStatus") String paymentStatus,
                                      @Param("roomMatchStatus") String roomMatchStatus,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate);

    OrderListVO selectOrderDetail(@Param("id") Long id);
}
