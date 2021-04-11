package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Collection<Role> findByNameIn(Collection<String> name);
}
