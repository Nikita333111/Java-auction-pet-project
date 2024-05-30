package com.example.demo.repository;

import com.example.demo.model.AuctionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionUserRepository extends JpaRepository<AuctionUser, Long> {
    AuctionUser findByEmail(String email);
}
