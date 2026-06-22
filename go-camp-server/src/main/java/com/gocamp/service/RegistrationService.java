package com.gocamp.service;

import com.gocamp.dto.RegistrationRequest;
import com.gocamp.dto.RegistrationResult;
import com.gocamp.entity.RegistrationOrder;

import java.util.List;

public interface RegistrationService {

    RegistrationResult submitRegistration(RegistrationRequest request);

    RegistrationOrder getOrderDetail(Long orderId);

    List<RegistrationOrder> getOrdersByParent(Long parentUserId);

    List<RegistrationOrder> getOrdersByStudent(Long studentId);
}