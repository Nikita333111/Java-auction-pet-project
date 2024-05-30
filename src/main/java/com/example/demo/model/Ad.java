package com.example.demo.model;

import com.example.demo.model.AuctionUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "advertisement")
public class Ad {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private double minPrice;

    @ElementCollection
    private List<String> images;

    @Temporal(TemporalType.TIMESTAMP)
    private Date auctionEndTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auction_user_id", nullable = false)
    private AuctionUser auctionUser;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();
}










