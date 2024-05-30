package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {
    private Long id;
    private Long adId;
    private Long auctionUserId;
    private double price;
    private Date bidTime;

    // Constructors, getters, setters
}

