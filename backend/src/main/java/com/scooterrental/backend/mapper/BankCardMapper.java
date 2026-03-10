package com.scooterrental.backend.mapper;

import com.scooterrental.backend.entity.BankCard;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BankCardMapper {

    @Select("SELECT card_id AS cardId, user_id AS userId, card_holder_name AS cardHolderName, " +
            "card_number_masked AS cardNumberMasked, expiry_date AS expiryDate, is_default AS isDefault " +
            "FROM bank_cards WHERE user_id = #{userId}")
    List<BankCard> selectByUserId(Integer userId);

    @Insert("INSERT INTO bank_cards(user_id, card_holder_name, card_number_masked, expiry_date) " +
            "VALUES(#{userId}, #{cardHolderName}, #{cardNumberMasked}, #{expiryDate})")
    void insert(BankCard card);

    @Delete("DELETE FROM bank_cards WHERE card_id = #{cardId}")
    void deleteById(Integer cardId);
}
