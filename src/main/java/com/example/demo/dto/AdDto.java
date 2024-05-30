package com.example.demo.dto;

import com.example.demo.model.Ad;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdDto
{
    private Long id;
    @NotEmpty
    @Size(min = 3, max = 40, message = "Title length should be between 5 and 40 characters")
    private String title;
    @Size(max = 500, message = "Description length should not exceed 500 characters")
    private String description;
    //@NotEmpty(message = "Price should not be empty")
    private double minPrice;
    private double currentPrice;
    @NotEmpty(message = "Images should not be empty")
    @Size(min = 1, max = 5, message = "At least one and max 5 images should be provided")
    private List<String> images;

    private boolean isOwner;

    private Date auctionEndTime;

    public AdDto(Ad ad, boolean isOwner) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.minPrice = ad.getMinPrice();
        this.images = ad.getImages();
        this.isOwner = isOwner;
    }
}
