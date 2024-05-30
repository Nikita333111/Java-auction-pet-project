package com.example.demo.controller;

import com.example.demo.dto.AdDto;
import com.example.demo.model.Ad;
import com.example.demo.service.AdService;
import jakarta.validation.Valid;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdController {
    private AdService adService;

    public AdController(AdService adService){
        this.adService = adService;
    }

    @GetMapping("/ad/list")
    public String adList(Model model){
        model.addAttribute("adList", adService.getAll());
        return "ad-list";
    }

    @PostMapping("/bid/{id}")
    public String placeBid(@PathVariable Long id, @RequestParam double bidPrice, Model model){
        try {
            adService.placeBid(id, bidPrice);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return "redirect:/ad/list";
    }


    @PostMapping("/ad/save")
    public String createAd(@Valid @ModelAttribute("ad") AdDto adDto,
                           BindingResult result,
                           Model model){
        // Проверяем ошибки валидации
        if (result.hasErrors()) {
            // Если есть ошибки, возвращаем пользователя на страницу формы
            model.addAttribute("ad", adDto);
            return "/index";
        }

        // Проверяем каждую ссылку на изображение ЗАТЕСТИТЬ
        UrlValidator urlValidator = new UrlValidator();
        for (String imageUrl : adDto.getImages()) {
            if (!urlValidator.isValid(imageUrl)) {
                // Если ссылка недействительна, добавляем сообщение об ошибке
                result.rejectValue("images", "invalid", "Invalid image URL: " + imageUrl);
            }
        }

        // Проверяем, есть ли ошибки после проверки ссылок
        if (result.hasErrors()) {
            // Если есть ошибки, возвращаем пользователя на страницу формы
            model.addAttribute("ad", adDto);
            return "/index";
        }

        // Если все проверки пройдены успешно, сохраняем объявление
        adService.saveAd(adDto);
        return "redirect:/index";
    }

    @GetMapping("/ad/delete/{id}")
    public String deleteAd(@PathVariable Long id) {
        Ad ad = adService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
        adService.deleteAd(ad);
        return "redirect:/index";
    }
}
