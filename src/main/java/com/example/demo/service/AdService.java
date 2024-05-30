package com.example.demo.service;

import com.example.demo.dto.AdDto;
import com.example.demo.dto.BidDto;
import com.example.demo.model.Ad;
import com.example.demo.model.Bid;
import com.example.demo.repository.AdRepository;
import com.example.demo.model.AuctionUser;
import com.example.demo.repository.AuctionUserRepository;
import com.example.demo.repository.BidRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdService {
    private AdRepository adRepository;
    private AuctionUserRepository userRepository;
    private BidRepository bidRepository;

    @Value("${auction.duration.minutes}")
    private int auctionDurationMinutes;

    public static final Logger logger = LoggerFactory.getLogger(AdService.class);

    public AdService(AdRepository adRepository, AuctionUserRepository userRepository, BidRepository bidRepository){
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public void saveAd(AdDto adDto) {
        Ad ad = new Ad();
        ad.setTitle(adDto.getTitle());
        ad.setDescription(adDto.getDescription());
        ad.setMinPrice(adDto.getMinPrice());
        ad.setImages(adDto.getImages());

        AuctionUser user = getAuthenticatedUser();

        ad.setAuctionUser(user);
        user.getAds().add(ad);
        adRepository.save(ad);
    }

    public List<AdDto> getAll() {
        AuctionUser user = getAuthenticatedUser();

        /*List<AdDto> ads = adRepository.findAll().stream().map(ad -> new AdDto(ad,ad.getAuctionUser().getId().equals(user.getId()))).toList();
        return ads;*/
        return adRepository.findAll().stream()
                .map(ad -> {
                    List<Bid> bids = bidRepository.findByAdOrderByBidTimeDesc(ad);
                    double currentPrice = bids.isEmpty() ? ad.getMinPrice() : bids.get(0).getPrice();
                    return new AdDto(ad.getId(),ad.getTitle(),ad.getDescription(),ad.getMinPrice(), currentPrice, ad.getImages(), ad.getAuctionUser().getId().equals(user.getId()), ad.getAuctionEndTime());
                }).collect(Collectors.toList());
    }

    public void deleteAd(Ad ad) {
        adRepository.delete(ad);
    }

    public Optional<Ad> findById(Long id) {
        return adRepository.findById(id);
    }

    @Transactional
    public BidDto placeBid(Long adId, double bidPrice){
        AuctionUser currentUser = getAuthenticatedUser();

        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new IllegalArgumentException("Auction with id: " + adId + " ended"));

        if (Objects.equals(ad.getAuctionUser().getId(), currentUser.getId())) {
            throw new IllegalArgumentException("id injection exception. Ad with name: " + ad.getTitle() + ", belongs to you ( " + currentUser.getName() + " );");
        }

        List<Bid> bids = bidRepository.findByAdOrderByBidTimeDesc(ad);
        double currentPrice = bids.isEmpty() ? ad.getMinPrice() : bids.get(0).getPrice();

        if(bidPrice <= currentPrice){
            throw new IllegalArgumentException("Bid price must be higher than current price");
        }

        //q
        ad.setVersion(ad.getVersion());
        try {
            adRepository.save(ad);
        } catch (OptimisticLockingFailureException e){
            throw new IllegalArgumentException("Concurrent update detected, please try again.");
        }

        Bid newBid = new Bid();
        newBid.setAd(ad);
        newBid.setAuctionUser(currentUser);
        newBid.setPrice(bidPrice);
        newBid.setBidTime(new Date());

        ad.getBids().add(newBid);
        currentUser.getBids().add(newBid);

        bidRepository.save(newBid);

        //notify previous bidder
        if(!bids.isEmpty()) {
            Bid previousBid = bids.get(0);
            notifyUser(previousBid.getAuctionUser(), "Your bid has been outbid by " + currentUser.getName());
        }

        notifyUser(ad.getAuctionUser(), "A new bid has been placed on your ad.");

        // timer
        System.out.println(ad.getAuctionEndTime() == null);
        if(ad.getAuctionEndTime() == null){
            Date auctionEndTime = new Date(System.currentTimeMillis() + auctionDurationMinutes * 60 * 1000);
            System.out.println(auctionEndTime);
            ad.setAuctionEndTime(auctionEndTime);
            adRepository.save(ad);
            startAuctionTimer(ad);
        }

        return new BidDto(newBid.getId(), newBid.getAd().getId(), newBid.getAuctionUser().getId(), newBid.getPrice(),newBid.getBidTime());
    }

    private void startAuctionTimer(Ad ad) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                completeAuction(ad);
            }
        };
        timer.schedule(task, ad.getAuctionEndTime());
    }

    private void completeAuction(Ad ad) {
        Bid highestBid = bidRepository.findTopByAdOrderByPriceDesc(ad);
        if(highestBid != null) {
            notifyUser(ad.getAuctionUser(), "Your ad was bought for price equal: $" + highestBid.getPrice());
        }

        adRepository.delete(ad);

    }

    private void notifyUser(AuctionUser user, String message){
        System.out.println("User: " + user.getName() + "; Action: " + message);
        logger.info("User: {}; Action: {}", user.getName(), message);
    }


    private AuctionUser getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByEmail(username);
    }
}
































