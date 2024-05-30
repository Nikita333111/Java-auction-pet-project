package com.example.demo.repository;

import com.example.demo.model.Ad;
import com.example.demo.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAdOrderByBidTimeDesc(Ad ad);

    Bid findTopByAdOrderByPriceDesc(Ad ad);
}
