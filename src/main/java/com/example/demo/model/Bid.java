package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bid")
public class Bid {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auction_user_id", nullable = false)
    private AuctionUser auctionUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Column(nullable = false)
    private double price;

    @Temporal(TemporalType.TIMESTAMP)
    private Date bidTime;
}

