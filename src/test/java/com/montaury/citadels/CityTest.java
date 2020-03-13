package com.montaury.citadels;

import org.assertj.core.api.Assertions;

import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.DestructibleDistrict;
import com.montaury.citadels.district.District;
import com.montaury.citadels.district.DistrictType;
import com.montaury.citadels.player.Player;
import io.vavr.collection.List;
import org.junit.Test;

import java.util.Optional;

public class Citytest {
    @Test
    public  void  test_cout_construction() {
        Board board =  new  Board ();
        City city =  new  City(board);
        Possession possession =  new  Possession ( 0 , null );
        city.buildDistrict (Card.MANOR_5); // score +3
        city.buildDistrict (Card.WATCHTOWER_2); // +1 score
        city.buildDistrict (Card.TAVERN_5 ); // +1 score
        int score = city.score (possession);
        Assertions.assertThat (score).isEqualTo(5);
    }

    @Test
    public void test_first_player_build7district(){
        Board board = new Board();
        City city = new City(board);
        Possession possession = new Possession(0,null);
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.TRADING_POST_1); // +2 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        city.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city.buildDistrict(Card.TOWN_HALL_2); // +5 score
        city.buildDistrict(Card.CHURCH_3); // +2 score
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(23); // 19 points + 4 points -> 23 au total

    }

    @Test
    public void test_district_all_types(){
        Board board = new Board();
        City city = new City(board);
        Possession possession = new Possession(0,null);
        city.buildDistrict(Card.PALACE_3); // +,NOBLE
        city.buildDistrict(Card.BATTLEFIELD_2); // +3,MILITARY
        city.buildDistrict(Card.DOCKS_3); // +1,TRADE
        city.buildDistrict(Card.CHURCH_3); // +2,RELIGIOUS
        city.buildDistrict(Card.KEEP_1); // +3,SPECIAL
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(13); // Normalement : 10 + Bonus 5 types : 3 -> 13 au total
    }
}



