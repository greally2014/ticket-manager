package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.ResetPasswordCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordCodeRepository extends JpaRepository<ResetPasswordCode, Long> {
}
