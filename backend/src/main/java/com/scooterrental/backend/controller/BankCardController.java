package com.scooterrental.backend.controller;

import com.scooterrental.backend.common.Result;
import com.scooterrental.backend.entity.BankCard;
import com.scooterrental.backend.mapper.BankCardMapper;
import com.scooterrental.backend.security.AuthAccess;
import com.scooterrental.backend.service.AuthSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Bank Card Module", description = "Manage user payment methods [ID: 2, 3]")
public class BankCardController {

    @Autowired
    private BankCardMapper bankCardMapper;

    /**
     * Get all bound bank cards for a specific user.
     * All card numbers returned are already masked (e.g., **** 1234).
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Cards", description = "Retrieve all bank cards associated with a user ID")
    public Result<List<BankCard>> getCards(
            @PathVariable Integer userId,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        AuthAccess.requireSelfOrStaff(session, userId);
        List<BankCard> cards = bankCardMapper.selectByUserId(userId);
        return Result.success(cards);
    }

    /**
     * Bind a new bank card to the user account.
     * Implements security masking: only the last 4 digits are preserved [ID: 3].
     */
    @PostMapping("/add")
    @Operation(summary = "Add Bank Card", description = "Bind a new card and ensure it is stored securely with masking")
    public Result<String> addCard(
            @RequestBody BankCard card,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        if (card == null) {
            return Result.error(400, "Card details are required");
        }
        if (!AuthAccess.isStaff(session)) {
            card.setUserId(AuthAccess.requireAuthenticatedUserId(session));
        }
        AuthAccess.requireSelfOrStaff(session, card.getUserId());

        // Security Logic: Mask the card number if it's a full number
        if (card.getCardNumberMasked() != null && card.getCardNumberMasked().length() > 4) {
            String rawNumber = card.getCardNumberMasked().replaceAll("\\s+", "");
            String lastFour = rawNumber.substring(rawNumber.length() - 4);
            card.setCardNumberMasked("**** " + lastFour);
        }

        bankCardMapper.insert(card);
        return Result.success("Bank card bound successfully");
    }

    /**
     * Unbind/Delete a bank card.
     */
    @DeleteMapping("/{cardId}")
    @Operation(summary = "Delete Card", description = "Remove a bank card by its ID")
    public Result<String> deleteCard(
            @PathVariable Integer cardId,
            @AuthenticationPrincipal AuthSessionService.AuthSession session) {
        AuthAccess.requireAuthenticatedUserId(session);
        BankCard existing = bankCardMapper.selectById(cardId);
        if (existing == null) {
            return Result.error(404, "Bank card not found");
        }
        if (!AuthAccess.isStaff(session) && !session.userId().equals(existing.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own cards");
        }
        bankCardMapper.deleteById(cardId);
        return Result.success("Bank card removed successfully");
    }
}
